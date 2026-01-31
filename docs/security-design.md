# 智能变电站巡检系统 - 安全设计文档

## 一、安全架构概述

### 1.1 安全设计原则
1. **纵深防御**: 多层安全防护机制
2. **最小权限**: 用户仅拥有完成工作所需的最小权限
3. **默认拒绝**: 默认拒绝所有访问,显式授权才能访问
4. **审计跟踪**: 记录所有关键操作日志
5. **数据加密**: 敏感数据加密存储和传输
6. **安全编码**: 遵循安全编码规范,防止常见漏洞

---

## 二、认证与授权

### 2.1 JWT Token机制

#### Token结构
```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "userId": 1,
    "username": "admin",
    "roleId": 1,
    "roleCode": "ADMIN",
    "iat": 1706600000,
    "exp": 1706607200
  },
  "signature": "..."
}
```

#### Token生成
```java
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 生成Token
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
            .setSubject(String.valueOf(user.getUserId()))
            .claim("username", user.getUsername())
            .claim("roleId", user.getRoleId())
            .claim("roleCode", user.getRoleCode())
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact();
    }

    /**
     * 验证Token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 从Token获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody();
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 获取Token过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody();
        return claims.getExpiration();
    }
}
```

#### Token刷新策略
```java
@Service
public class TokenRefreshService {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private RedisCache redisCache;

    /**
     * 刷新Token
     */
    public String refreshToken(String oldToken) {
        // 验证旧Token
        if (!tokenProvider.validateToken(oldToken)) {
            throw new BusinessException("Token无效");
        }

        // 检查是否在黑名单中
        if (redisCache.hasKey("token:blacklist:" + oldToken)) {
            throw new BusinessException("Token已失效");
        }

        // 获取用户信息
        Long userId = tokenProvider.getUserIdFromToken(oldToken);
        User user = userService.getById(userId);

        // 生成新Token
        String newToken = tokenProvider.generateToken(user);

        // 将旧Token加入黑名单
        long ttl = tokenProvider.getExpirationDateFromToken(oldToken).getTime()
                   - System.currentTimeMillis();
        redisCache.set("token:blacklist:" + oldToken, "1", ttl);

        return newToken;
    }
}
```

### 2.2 RBAC权限控制

#### 权限注解
```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {
    /**
     * 需要的权限
     */
    String[] value() default {};

    /**
     * 权限逻辑关系(AND/OR)
     */
    Logical logical() default Logical.AND;
}

public enum Logical {
    AND, OR
}
```

#### 权限拦截器
```java
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequiresPermission annotation = handlerMethod.getMethodAnnotation(RequiresPermission.class);

        if (annotation == null) {
            return true;
        }

        // 获取当前用户
        UserContext user = UserContextHolder.get();
        if (user == null) {
            throw new PermissionDeniedException("未登录");
        }

        // 获取用户权限
        Set<String> permissions = userService.getUserPermissions(user.getUserId());

        // 检查权限
        boolean hasPermission = checkPermission(permissions, annotation);

        if (!hasPermission) {
            throw new PermissionDeniedException("无权限访问");
        }

        return true;
    }

    private boolean checkPermission(Set<String> userPermissions, RequiresPermission annotation) {
        String[] required = annotation.value();
        Logical logical = annotation.logical();

        if (logical == Logical.AND) {
            return Arrays.stream(required)
                .allMatch(userPermissions::contains);
        } else {
            return Arrays.stream(required)
                .anyMatch(userPermissions::contains);
        }
    }
}
```

#### 数据权限控制
```java
@Aspect
@Component
public class DataPermissionAspect {

    @Before("execution(* com.smart.substation.service.*.*(..))")
    public void doBefore(JoinPoint point) {
        UserContext user = UserContextHolder.get();

        // 管理员拥有所有数据权限
        if (user.isAdmin()) {
            return;
        }

        // 处理数据权限
        handleDataPermission(point, user);
    }

    private void handleDataPermission(JoinPoint point, UserContext user) {
        String methodName = point.getSignature().getName();
        Object[] args = point.getArgs();

        // 巡检员只能看到分配给自己的任务
        if (user.isInspector() && methodName.contains("Task")) {
            for (Object arg : args) {
                if (arg instanceof TaskQueryDTO) {
                    ((TaskQueryDTO) arg).setAssigneeId(user.getUserId());
                }
            }
        }

        // 班长可以查看本班组的所有任务
        if (user.isMonitor() && methodName.contains("Task")) {
            for (Object arg : args) {
                if (arg instanceof TaskQueryDTO) {
                    ((TaskQueryDTO) arg).setDeptId(user.getDeptId());
                }
            }
        }
    }
}
```

---

## 三、密码安全

### 3.1 密码加密存储

```java
@Component
public class PasswordEncoder {

    private static final int STRENGTH = 12;

    /**
     * 加密密码
     */
    public String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(STRENGTH));
    }

    /**
     * 验证密码
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
```

### 3.2 密码复杂度验证

```java
@Component
public class PasswordValidator {

    /**
     * 密码复杂度规则
     * - 最小8位
     * - 包含大小写字母
     * - 包含数字
     * - 包含特殊字符
     */
    private static final Pattern PASSWORD_PATTERN =
        Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    public void validate(String password) {
        if (password == null || password.isEmpty()) {
            throw new BusinessException("密码不能为空");
        }

        if (password.length() < 8) {
            throw new BusinessException("密码长度不能少于8位");
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new BusinessException(
                "密码必须包含大小写字母、数字和特殊字符"
            );
        }

        // 检查是否为弱密码
        if (isWeakPassword(password)) {
            throw new BusinessException("密码过于简单");
        }
    }

    private boolean isWeakPassword(String password) {
        List<String> weakPasswords = Arrays.asList(
            "12345678", "password", "abcd1234", "admin123"
        );
        return weakPasswords.contains(password.toLowerCase());
    }

    /**
     * 检查密码历史(防止重复使用旧密码)
     */
    public void checkHistory(Long userId, String newPassword) {
        List<String> history = passwordHistoryService.getHistory(userId, 5);

        for (String oldPassword : history) {
            if (passwordEncoder.matches(newPassword, oldPassword)) {
                throw new BusinessException("不能使用最近5次使用过的密码");
            }
        }
    }
}
```

### 3.3 密码修改安全

```java
@Service
public class PasswordChangeService {

    @Autowired
    private PasswordValidator passwordValidator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        // 验证旧密码
        User user = userService.getById(request.getUserId());
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 验证新密码复杂度
        passwordValidator.validate(request.getNewPassword());

        // 检查密码历史
        passwordValidator.checkHistory(request.getUserId(), request.getNewPassword());

        // 加密新密码
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());

        // 更新密码
        user.setPassword(encodedPassword);
        user.setPasswordChangeTime(new Date());
        user.setForceChangePassword(false);
        userService.updateById(user);

        // 记录密码历史
        passwordHistoryService.addHistory(user.getUserId(), encodedPassword);

        // 清除用户所有Token,强制重新登录
        tokenService.clearAllUserTokens(user.getUserId());

        // 发送密码修改通知
        notificationService.sendPasswordChangeNotification(user);
    }
}
```

---

## 四、数据加密

### 4.1 敏感数据加密

```java
@Component
public class DataEncryptor {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY = "xxx"; // 从配置读取
    private static final String IV = "xxx";

    /**
     * 加密
     */
    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(data.getBytes());

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("加密失败", e);
        }
    }

    /**
     * 解密
     */
    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes());

            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));

            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("解密失败", e);
        }
    }
}
```

### 4.2 数据脱敏

```java
@Component
public class DataMasker {

    /**
     * 手机号脱敏
     */
    public String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 身份证脱敏
     */
    public String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            return idCard;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(14);
    }

    /**
     * 邮箱脱敏
     */
    public String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String name = parts[0];
        if (name.length() <= 2) {
            return "*@" + parts[1];
        }
        return name.substring(0, 2) + "***@" + parts[1];
    }
}
```

---

## 五、文件上传安全

### 5.1 文件类型验证

```java
@Component
public class FileSecurityValidator {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
        "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        ".jpg", ".jpeg", ".png", ".gif", ".webp", ".pdf", ".doc", ".docx"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * 验证文件
     */
    public void validate(MultipartFile file) {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小不能超过10MB");
        }

        // 检查文件扩展名
        String filename = file.getOriginalFilename();
        String extension = getFileExtension(filename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException("不支持的文件类型");
        }

        // 检查MIME类型
        String contentType = file.getContentType();
        if (!isAllowedContentType(contentType, extension)) {
            throw new BusinessException("文件类型不匹配");
        }

        // 检查文件内容
        if (!isValidFileContent(file)) {
            throw new BusinessException("文件内容无效");
        }
    }

    /**
     * 验证MIME类型与扩展名是否匹配
     */
    private boolean isAllowedContentType(String contentType, String extension) {
        // 图片文件严格验证
        if (extension.matches("\\.(jpg|jpeg|png|gif|webp)")) {
            return ALLOWED_IMAGE_TYPES.contains(contentType);
        }
        return true;
    }

    /**
     * 验证文件内容(防止伪造)
     */
    private boolean isValidFileContent(MultipartFile file) {
        try {
            byte[] header = new byte[8];
            file.getInputStream().read(header);

            // 检查文件头魔数
            String fileType = detectFileType(header);

            String extension = getFileExtension(file.getOriginalFilename());
            return isMatch(fileType, extension);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 检测文件类型
     */
    private String detectFileType(byte[] header) {
        if (header.length < 2) return "unknown";

        // JPEG: FF D8
        if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8) {
            return "jpeg";
        }
        // PNG: 89 50 4E 47
        if (header[0] == (byte) 0x89 && header[1] == (byte) 0x50 &&
            header[2] == (byte) 0x4E && header[3] == (byte) 0x47) {
            return "png";
        }
        // GIF: 47 49 46
        if (header[0] == (byte) 0x47 && header[1] == (byte) 0x49 &&
            header[2] == (byte) 0x46) {
            return "gif";
        }

        return "unknown";
    }

    /**
     * 验证文件类型与扩展名是否匹配
     */
    private boolean isMatch(String fileType, String extension) {
        if (fileType.equals("unknown")) return false;

        switch (fileType) {
            case "jpeg":
                return extension.matches("\\.(jpg|jpeg)");
            case "png":
                return extension.equals(".png");
            case "gif":
                return extension.equals(".gif");
            default:
                return true;
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
```

### 5.2 文件存储安全

```java
@Service
public class FileSecurityService {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Autowired
    private FileSecurityValidator validator;

    /**
     * 安全保存文件
     */
    public String saveFileSecurely(MultipartFile file, String businessType) {
        // 验证文件
        validator.validate(file);

        // 生成安全的文件名
        String safeFilename = generateSafeFilename(file.getOriginalFilename());

        // 按日期分目录存储
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = businessType + "/" + datePath + "/" + safeFilename;
        String fullPath = uploadPath + "/" + relativePath;

        // 创建目录
        File directory = new File(fullPath).getParentFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            // 保存文件
            file.transferTo(new File(fullPath));

            // 设置文件权限
            File savedFile = new File(fullPath);
            savedFile.setReadable(true, false); // 所有者可读
            savedFile.setWritable(false, false); // 所有者不可写

            return relativePath;
        } catch (IOException e) {
            throw new BusinessException("文件保存失败");
        }
    }

    /**
     * 生成安全的文件名
     */
    private String generateSafeFilename(String originalFilename) {
        // 获取扩展名
        String extension = getFileExtension(originalFilename);

        // 生成UUID作为文件名
        String uuid = UUID.randomUUID().toString().replace("-", "");

        return uuid + extension;
    }

    /**
     * 获取文件访问URL
     */
    public String getFileAccessUrl(String relativePath) {
        // 使用临时Token,防止直接访问
        String token = generateAccessToken(relativePath);
        return "/api/v1/file/access?path=" + URLEncoder.encode(relativePath) + "&token=" + token;
    }

    /**
     * 生成访问Token
     */
    private String generateAccessToken(String filePath) {
        // Token有效期1小时
        long expiry = System.currentTimeMillis() + 3600 * 1000;

        String data = filePath + ":" + expiry;
        String signature = DigestUtils.md5DigestAsHex(data.getBytes());

        return Base64.getEncoder().encodeToString(
            (data + ":" + signature).getBytes()
        );
    }

    /**
     * 验证访问Token
     */
    public boolean validateAccessToken(String filePath, String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            String[] parts = decoded.split(":");

            if (parts.length != 3) return false;

            String tokenPath = parts[0];
            long expiry = Long.parseLong(parts[1]);
            String signature = parts[2];

            // 检查路径是否匹配
            if (!tokenPath.equals(filePath)) return false;

            // 检查是否过期
            if (System.currentTimeMillis() > expiry) return false;

            // 验证签名
            String data = filePath + ":" + expiry;
            String expectedSignature = DigestUtils.md5DigestAsHex(data.getBytes());

            return expectedSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
}
```

---

## 六、SQL注入防护

### 6.1 MyBatis防注入

```xml
<!-- 使用#{}而非${} -->
<select id="getUserByUsername" parameterType="string" resultType="User">
    SELECT * FROM sys_user
    WHERE username = #{username}
    AND deleted = 0
</select>

<!-- LIKE查询需要拼接 -->
<select id="searchUsers" parameterType="map" resultType="User">
    SELECT * FROM sys_user
    WHERE real_name LIKE CONCAT('%', #{keyword}, '%')
    AND deleted = 0
</select>
```

### 6.2 参数校验

```java
@Component
public class SqlInjectionValidator {

    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        ".*(union|select|insert|update|delete|drop|truncate|exec|execute|script|javascript|alert|onload|onerror).*",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * 验证参数是否包含SQL注入特征
     */
    public void validate(String... params) {
        for (String param : params) {
            if (param == null) continue;

            if (SQL_INJECTION_PATTERN.matcher(param).matches()) {
                throw new BusinessException("参数包含非法字符");
            }
        }
    }
}
```

---

## 七、XSS防护

### 7.1 输入过滤

```java
@Component
public class XssFilter implements Filter {

    private static final Pattern XSS_PATTERN = Pattern.compile(
        "<(script|iframe|object|embed|form|input|textarea|link|style|meta).*?>.*?</\\1>|on\\w+\\s*=|javascript:",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        XssHttpServletRequestWrapper wrappedRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request);
        chain.doFilter(wrappedRequest, response);
    }

    /**
     * XSS请求包装器
     */
    private static class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

        public XssHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            return cleanXss(value);
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null) return null;

            String[] cleaned = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                cleaned[i] = cleanXss(values[i]);
            }
            return cleaned;
        }

        private String cleanXss(String value) {
            if (value == null) return null;

            // HTML转义
            value = StringEscapeUtils.escapeHtml4(value);

            // 移除危险标签
            value = XSS_PATTERN.matcher(value).replaceAll("");

            return value;
        }
    }
}
```

### 7.2 输出编码

```java
@Component
public class OutputEncoder {

    /**
     * HTML编码
     */
    public String forHtml(String input) {
        if (input == null) return null;
        return StringEscapeUtils.escapeHtml4(input);
    }

    /**
     * JavaScript编码
     */
    public String forJs(String input) {
        if (input == null) return null;
        return StringEscapeUtils.escapeEcmaScript(input);
    }

    /**
     * URL编码
     */
    public String forUrl(String input) {
        if (input == null) return null;
        try {
            return URLEncoder.encode(input, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return input;
        }
    }
}
```

---

## 八、安全审计

### 8.1 操作日志记录

```java
@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperateLogService operateLogService;

    /**
     * 记录操作日志
     */
    @Around("@annotation(operationLog)")
    public Object recordLog(ProceedingJoinPoint point, OperationLog operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取请求信息
        HttpServletRequest request = ServletUtils.getRequest();
        UserContext user = UserContextHolder.get();

        OperateLog log = new OperateLog();
        log.setUserId(user != null ? user.getUserId() : null);
        log.setUsername(user != null ? user.getUsername() : null);
        log.setOperation(operationLog.value());
        log.setModule(operationLog.module());
        log.setRequestMethod(request.getMethod());
        log.setRequestUrl(request.getRequestURI());
        log.setIpAddress(ServletUtils.getClientIP(request));
        log.setLocation(IpUtils.getLocation(log.getIpAddress()));
        log.setUserAgent(request.getHeader("User-Agent"));
        log.setCreateTime(new Date());

        try {
            // 执行方法
            Object result = point.proceed();

            log.setStatus(1);
            log.setExecuteTime((int) (System.currentTimeMillis() - startTime));

            return result;
        } catch (Exception e) {
            log.setStatus(0);
            log.setErrorMsg(e.getMessage());
            log.setExecuteTime((int) (System.currentTimeMillis() - startTime));
            throw e;
        } finally {
            // 异步保存日志
            operateLogService.saveAsync(log);
        }
    }
}
```

### 8.2 敏感操作监控

```java
@Component
public class SecurityMonitor {

    @Autowired
    private RedisCache redisCache;

    private static final String LOGIN_FAIL_KEY = "security:login:fail:";
    private static final String IP_BLACKLIST_KEY = "security:blacklist:ip:";
    private static final int MAX_FAIL_COUNT = 5;
    private static final int LOCK_MINUTES = 30;

    /**
     * 记录登录失败
     */
    public void recordLoginFail(String username, String ip) {
        String key = LOGIN_FAIL_KEY + ip + ":" + username;
        Long count = redisCache.increment(key);

        if (count == 1) {
            redisCache.expire(key, LOCK_MINUTES, TimeUnit.MINUTES);
        }

        // 超过最大失败次数,加入黑名单
        if (count >= MAX_FAIL_COUNT) {
            String blacklistKey = IP_BLACKLIST_KEY + ip;
            redisCache.set(blacklistKey, "1", LOCK_MINUTES, TimeUnit.MINUTES);

            // 发送告警通知
            notificationService.sendSecurityAlert(
                "IP " + ip + " 登录失败次数过多,已被锁定"
            );
        }
    }

    /**
     * 检查IP是否在黑名单
     */
    public boolean isIpBlacklisted(String ip) {
        String key = IP_BLACKLIST_KEY + ip;
        return redisCache.hasKey(key);
    }

    /**
     * 清除登录失败记录
     */
    public void clearLoginFail(String username, String ip) {
        String key = LOGIN_FAIL_KEY + ip + ":" + username;
        redisCache.delete(key);
    }
}
```

---

## 九、HTTPS配置

```yaml
# application-prod.yml
server:
  port: 443
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEY_STORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: tomcat

# HTTP自动跳转HTTPS
server:
  port: 80
  # 使用反向代理(Nginx)处理80到443的跳转
```
