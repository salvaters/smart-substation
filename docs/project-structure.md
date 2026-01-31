# 智能变电站巡检系统 - 项目结构文档

## 一、后端项目结构 (Spring Boot)

```
smart-substation-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── smart/
│   │   │           └── substation/
│   │   │               ├── SmartSubstationApplication.java    # 启动类
│   │   │               │
│   │   │               ├── config/                            # 配置类
│   │   │               │   ├── AsyncConfig.java               # 异步配置
│   │   │               │   ├── CacheConfig.java               # 缓存配置
│   │   │               │   ├── CorsConfig.java                # 跨域配置
│   │   │               │   ├── InterceptorConfig.java         # 拦截器配置
│   │   │               │   ├── MybatisPlusConfig.java         # MP配置
│   │   │               │   ├── RedisConfig.java               # Redis配置
│   │   │               │   ├── SecurityConfig.java            # 安全配置
│   │   │               │   ├── SwaggerConfig.java             # API文档配置
│   │   │               │   └── WebSocketConfig.java           # WebSocket配置
│   │   │               │
│   │   │               ├── common/                            # 公共模块
│   │   │               │   ├── annotation/                    # 自定义注解
│   │   │               │   │   ├── RequiresPermission.java    # 权限注解
│   │   │               │   │   ├── OperationLog.java          # 操作日志注解
│   │   │               │   │   ├── RateLimit.java             # 限流注解
│   │   │               │   │   └── DataScope.java             # 数据权限注解
│   │   │               │   ├── constant/                      # 常量定义
│   │   │               │   │   ├── CacheConstants.java        # 缓存常量
│   │   │               │   │   ├── CommonConstants.java       # 通用常量
│   │   │               │   │   ├── HttpStatus.java            # 状态码常量
│   │   │               │   │   └── ScheduleConstants.java     # 定时任务常量
│   │   │               │   ├── domain/                        # 公共实体
│   │   │               │   │   ├── BaseEntity.java            # 基础实体
│   │   │               │   │   ├── R.java                     # 统一响应体
│   │   │               │   │   └── PageResult.java            # 分页结果
│   │   │               │   ├── enums/                         # 枚举类
│   │   │               │   │   ├── ErrorCode.java             # 错误码枚举
│   │   │               │   │   ├── OperationType.java         # 操作类型枚举
│   │   │               │   │   ├── TaskStatus.java            # 任务状态枚举
│   │   │               │   │   └── UserType.java              # 用户类型枚举
│   │   │               │   ├── exception/                     # 异常处理
│   │   │               │   │   ├── BaseException.java         # 基础异常
│   │   │               │   │   ├── BusinessException.java     # 业务异常
│   │   │               │   │   ├── PermissionException.java   # 权限异常
│   │   │               │   │   └── ServiceException.java      # 服务异常
│   │   │               │   ├── handler/                       # 处理器
│   │   │               │   │   ├── GlobalExceptionHandler.java # 全局异常处理
│   │   │               │   │   └── MyMetaObjectHandler.java   # 字段自动填充
│   │   │               │   ├── interceptor/                   # 拦截器
│   │   │               │   │   ├── LoginInterceptor.java      # 登录拦截器
│   │   │               │   │   ├── PermissionInterceptor.java # 权限拦截器
│   │   │               │   │   └── RateLimitInterceptor.java  # 限流拦截器
│   │   │               │   ├── properties/                    # 配置属性
│   │   │               │   │   └── AppProperties.java         # 应用配置属性
│   │   │               │   ├── utils/                         # 工具类
│   │   │               │   │   ├── DateUtils.java             # 日期工具
│   │   │               │   │   ├── StringUtils.java           # 字符串工具
│   │   │               │   │   ├── BeanUtils.java             # Bean工具
│   │   │               │   │   ├── SecurityUtils.java         # 安全工具
│   │   │               │   │   ├── IpUtils.java               # IP工具
│   │   │               │   │   ├── ServletUtils.java          # Servlet工具
│   │   │               │   │   ├── EncryptUtils.java          # 加密工具
│   │   │               │   │   └── JwtTokenProvider.java      # JWT工具
│   │   │               │   └── filter/                        # 过滤器
│   │   │               │       ├── XssFilter.java             # XSS过滤
│   │   │               │       └── RepeatableFilter.java      # 可重复读过滤
│   │   │               │
│   │   │               ├── modules/                           # 业务模块
│   │   │               │   ├── auth/                          # 认证模块
│   │   │               │   │   ├── controller/
│   │   │               │   │   │   └── AuthController.java
│   │   │               │   │   ├── service/
│   │   │               │   │   │   ├── IAuthService.java
│   │   │               │   │   │   └── impl/AuthServiceImpl.java
│   │   │               │   │   ├── domain/
│   │   │               │   │   │   ├── LoginRequest.java
│   │   │               │   │   │   ├── LoginResponse.java
│   │   │               │   │   │   └── TokenResponse.java
│   │   │               │   │   └── mapper/
│   │   │               │   │
│   │   │               │   ├── system/                        # 系统管理模块
│   │   │               │   │   ├── controller/
│   │   │               │   │   │   ├── SysUserController.java
│   │   │               │   │   │   ├── SysRoleController.java
│   │   │               │   │   │   ├── SysPermissionController.java
│   │   │               │   │   │   └── SysConfigController.java
│   │   │               │   │   ├── service/
│   │   │               │   │   │   ├── ISysUserService.java
│   │   │               │   │   │   ├── ISysRoleService.java
│   │   │               │   │   │   ├── ISysPermissionService.java
│   │   │               │   │   │   └── impl/
│   │   │               │   │   ├── domain/
│   │   │               │   │   │   ├── SysUser.java
│   │   │               │   │   │   ├── SysRole.java
│   │   │               │   │   │   ├── SysPermission.java
│   │   │               │   │   │   └── SysConfig.java
│   │   │               │   │   ├── mapper/
│   │   │               │   │   │   ├── SysUserMapper.java
│   │   │               │   │   │   ├── SysRoleMapper.java
│   │   │               │   │   │   └── SysPermissionMapper.java
│   │   │               │   │   └── dto/
│   │   │               │   │       ├── UserQueryDTO.java
│   │   │               │   │       └── UserVO.java
│   │   │               │   │
│   │   │               │   ├── station/                       # 变电站模块
│   │   │               │   │   ├── controller/
│   │   │               │   │   │   └── StationController.java
│   │   │               │   │   ├── service/
│   │   │               │   │   │   ├── IStationService.java
│   │   │               │   │   │   └── impl/StationServiceImpl.java
│   │   │               │   │   ├── domain/
│   │   │               │   │   │   └── SubStation.java
│   │   │               │   │   ├── mapper/
│   │   │               │   │   │   └── SubStationMapper.java
│   │   │               │   │   └── dto/
│   │   │               │   │       ├── StationQueryDTO.java
│   │   │               │   │       └── StationVO.java
│   │   │               │   │
│   │   │               │   ├── device/                        # 设备模块
│   │   │               │   │   ├── controller/
│   │   │               │   │   │   └── DeviceController.java
│   │   │               │   │   ├── service/
│   │   │               │   │   │   ├── IDeviceService.java
│   │   │               │   │   │   └── impl/DeviceServiceImpl.java
│   │   │               │   │   ├── domain/
│   │   │               │   │   │   ├── DeviceInfo.java
│   │   │               │   │   │   └── DeviceCategory.java
│   │   │               │   │   ├── mapper/
│   │   │               │   │   │   ├── DeviceInfoMapper.java
│   │   │               │   │   │   └── DeviceCategoryMapper.java
│   │   │               │   │   └── dto/
│   │   │               │   │
│   │   │               │   ├── template/                      # 巡检模板模块
│   │   │               │   │   ├── controller/
│   │   │               │   │   ├── service/
│   │   │               │   │   ├── domain/
│   │   │               │   │   │   ├── InspectionTemplate.java
│   │   │               │   │   │   └── InspectionTemplateItem.java
│   │   │               │   │   └── mapper/
│   │   │               │   │
│   │   │               │   ├── plan/                          # 巡检计划模块
│   │   │               │   │   ├── controller/
│   │   │               │   │   ├── service/
│   │   │               │   │   ├── domain/
│   │   │               │   │   │   └── InspectionPlan.java
│   │   │               │   │   └── mapper/
│   │   │               │   │
│   │   │               │   ├── task/                          # 巡检任务模块
│   │   │               │   │   ├── controller/
│   │   │               │   │   ├── service/
│   │   │               │   │   ├── domain/
│   │   │               │   │   │   ├── InspectionTask.java
│   │   │               │   │   │   └── TaskDeviceRelation.java
│   │   │               │   │   └── mapper/
│   │   │               │   │
│   │   │               │   ├── record/                        # 巡检记录模块
│   │   │               │   │   ├── controller/
│   │   │               │   │   ├── service/
│   │   │               │   │   ├── domain/
│   │   │               │   │   │   └── InspectionRecord.java
│   │   │               │   │   └── mapper/
│   │   │               │   │
│   │   │               │   ├── defect/                        # 缺陷管理模块
│   │   │               │   │   ├── controller/
│   │   │               │   │   ├── service/
│   │   │               │   │   ├── domain/
│   │   │               │   │   │   └── DefectInfo.java
│   │   │               │   │   └── mapper/
│   │   │               │   │
│   │   │               │   ├── report/                        # 巡检报告模块
│   │   │               │   │   ├── controller/
│   │   │               │   │   ├── service/
│   │   │               │   │   ├── domain/
│   │   │               │   │   │   └── InspectionReport.java
│   │   │               │   │   └── mapper/
│   │   │               │   │
│   │   │               │   ├── file/                          # 文件管理模块
│   │   │               │   │   ├── controller/
│   │   │               │   │   ├── service/
│   │   │               │   │   ├── domain/
│   │   │               │   │   │   └── SysFile.java
│   │   │               │   │   └── mapper/
│   │   │               │   │
│   │   │               │   ├── sync/                          # 同步模块
│   │   │               │   │   ├── controller/
│   │   │               │   │   ├── service/
│   │   │               │   │   ├── domain/
│   │   │               │   │   │   └── SyncLog.java
│   │   │               │   │   └── mapper/
│   │   │               │   │
│   │   │               │   └── statistics/                    # 统计分析模块
│   │   │               │       ├── controller/
│   │   │               │       └── service/
│   │   │               │
│   │   │               ├── job/                              # 定时任务
│   │   │               │   ├── TaskGenerateJob.java          # 任务生成定时任务
│   │   │               │   ├── TaskOverdueCheckJob.java      # 逾期检查定时任务
│   │   │               │   ├── ReportGenerateJob.java        # 报告生成定时任务
│   │   │               │   └── DataCleanupJob.java           # 数据清理定时任务
│   │   │               │
│   │   │               └── websocket/                        # WebSocket
│   │   │                   ├── config/
│   │   │                   ├── handler/
│   │   │                   │   └── NotificationHandler.java
│   │   │                   └── interceptor/
│   │   │
│   │   └── resources/
│   │       ├── mapper/                              # MyBatis映射文件
│   │       │   ├── system/
│   │       │   │   ├── SysUserMapper.xml
│   │       │   │   ├── SysRoleMapper.xml
│   │       │   │   └── SysPermissionMapper.xml
│   │       │   ├── station/
│   │       │   ├── device/
│   │       │   ├── template/
│   │       │   ├── task/
│   │       │   ├── record/
│   │       │   ├── defect/
│   │       │   └── report/
│   │       │
│   │       ├── application.yml                       # 主配置文件
│   │       ├── application-dev.yml                   # 开发环境配置
│   │       ├── application-test.yml                  # 测试环境配置
│   │       ├── application-prod.yml                  # 生产环境配置
│   │       ├── logback-spring.xml                    # 日志配置
│   │       └── banner.txt                            # 启动横幅
│   │
│   └── test/                                         # 测试代码
│       └── java/
│           └── com/smart/substation/
│               ├── controller/                       # 控制器测试
│               ├── service/                          # 服务测试
│               └── mapper/                           # Mapper测试
│
├── .gitignore                                        # Git忽略文件
├── pom.xml                                           # Maven配置
├── Dockerfile                                        # Docker镜像构建
└── README.md                                         # 项目说明
```

---

## 二、前端H5项目结构 (Vue3 + Vant)

```
smart-substation-h5/
├── public/
│   ├── icons/                                       # 图标资源
│   │   ├── icon-192x192.png
│   │   └── icon-512x512.png
│   ├── favicon.ico
│   └── sw.js                                        # Service Worker
│
├── src/
│   ├── assets/                                      # 静态资源
│   │   ├── images/
│   │   │   ├── default-avatar.png
│   │   │   ├── logo.png
│   │   │   └── placeholder.png
│   │   ├── styles/
│   │   │   ├── variables.scss                       # 样式变量
│   │   │   ├── mixins.scss                          # 样式混入
│   │   │   ├── common.scss                          # 公共样式
│   │   │   └── vant-custom.scss                     # Vant定制样式
│   │   └── fonts/
│   │
│   ├── components/                                  # 组件
│   │   ├── common/                                  # 通用组件
│   │   │   ├── PageHeader.vue
│   │   │   ├── PageFooter.vue
│   │   │   ├── Empty.vue
│   │   │   └── Loading.vue
│   │   ├── business/                                # 业务组件
│   │   │   ├── DeviceCard.vue
│   │   │   ├── TaskCard.vue
│   │   │   ├── DefectCard.vue
│   │   │   ├── PhotoUploader.vue
│   │   │   ├── QrScanner.vue
│   │   │   └── OfflineBanner.vue
│   │   └── form/                                    # 表单组件
│   │       ├── InspectionForm.vue
│   │       ├── DefectForm.vue
│   │       └── RecordFormItem.vue
│   │
│   ├── views/                                       # 页面
│   │   ├── auth/
│   │   │   └── Login.vue
│   │   ├── home/
│   │   │   └── Index.vue
│   │   ├── task/
│   │   │   ├── TaskList.vue
│   │   │   ├── TaskDetail.vue
│   │   │   └── TaskInspection.vue
│   │   ├── device/
│   │   │   ├── DeviceList.vue
│   │   │   └── DeviceDetail.vue
│   │   ├── record/
│   │   │   └── RecordList.vue
│   │   ├── defect/
│   │   │   ├── DefectList.vue
│   │   │   └── DefectDetail.vue
│   │   ├── report/
│   │   │   ├── ReportList.vue
│   │   │   └── ReportDetail.vue
│   │   ├── profile/
│   │   │   ├── Profile.vue
│   │   │   └── Settings.vue
│   │   └── common/
│   │       └── 404.vue
│   │
│   ├── stores/                                      # Pinia状态管理
│   │   ├── user.ts
│   │   ├── task.ts
│   │   ├── offline.ts
│   │   └── app.ts
│   │
│   ├── services/                                    # API服务
│   │   ├── api.ts                                   # API配置
│   │   ├── auth.service.ts
│   │   ├── task.service.ts
│   │   ├── device.service.ts
│   │   ├── record.service.ts
│   │   ├── defect.service.ts
│   │   ├── file.service.ts
│   │   └── sync.service.ts
│   │
│   ├── db/                                          # IndexedDB
│   │   ├── index.ts                                 # DB初始化
│   │   ├── stores/
│   │   │   ├── task.store.ts
│   │   │   ├── record.store.ts
│   │   │   ├── device.store.ts
│   │   │   ├── file.store.ts
│   │   │   └── sync.store.ts
│   │   └── sync.ts                                  # 同步逻辑
│   │
│   ├── composables/                                 # 组合式API
│   │   ├── useAuth.ts
│   │   ├── useTask.ts
│   │   ├── useOffline.ts
│   │   ├── useQrScanner.ts
│   │   ├── useUpload.ts
│   │   └── useNetwork.ts
│   │
│   ├── router/                                      # 路由
│   │   ├── index.ts
│   │   └── routes.ts
│   │
│   ├── types/                                       # 类型定义
│   │   ├── api.d.ts
│   │   ├── models.d.ts
│   │   ├── components.d.ts
│   │   └── global.d.ts
│   │
│   ├── constants/                                   # 常量
│   │   ├── index.ts
│   │   ├── enums.ts
│   │   └── config.ts
│   │
│   ├── utils/                                       # 工具函数
│   │   ├── request.ts                               # 请求封装
│   │   ├── storage.ts                               # 存储封装
│   │   ├── auth.ts                                  # 认证工具
│   │   ├── validate.ts                              # 验证工具
│   │   ├── format.ts                                # 格式化工具
│   │   ├── date.ts                                  # 日期工具
│   │   ├── image.ts                                 # 图片处理
│   │   └── network.ts                               # 网络状态
│   │
│   ├── App.vue
│   └── main.ts
│
├── .env                                             # 环境变量
├── .env.development
├── .env.production
├── .eslintrc.js                                     # ESLint配置
├── .prettierrc                                      # Prettier配置
├── tsconfig.json                                    # TypeScript配置
├── vite.config.ts                                   # Vite配置
├── index.html
├── package.json
└── README.md
```

---

## 三、Web端项目结构 (Vue3 + Element Plus)

```
smart-substation-web/
├── public/
│   ├── icons/
│   └── favicon.ico
│
├── src/
│   ├── assets/                                      # 静态资源
│   │   ├── images/
│   │   ├── styles/
│   │   │   ├── variables.scss
│   │   │   ├── mixins.scss
│   │   │   ├── common.scss
│   │   │   └── element-custom.scss                  # Element定制样式
│   │   └── fonts/
│   │
│   ├── components/                                  # 组件
│   │   ├── common/                                  # 通用组件
│   │   │   ├── AppHeader.vue
│   │   │   ├── AppAside.vue
│   │   │   ├── AppBreadcrumb.vue
│   │   │   └── AppFooter.vue
│   │   ├── business/                                # 业务组件
│   │   │   ├── StationTree.vue
│   │   │   ├── DeviceTable.vue
│   │   │   ├── TaskTimeline.vue
│   │   │   ├── StatisticsCard.vue
│   │   │   └── ChartContainer.vue
│   │   └── form/                                    # 表单组件
│   │       ├── UserForm.vue
│   │       ├── StationForm.vue
│   │       ├── DeviceForm.vue
│   │       ├── PlanForm.vue
│   │       └── TaskForm.vue
│   │
│   ├── views/                                       # 页面
│   │   ├── auth/
│   │   │   └── Login.vue
│   │   ├── dashboard/
│   │   │   └── Index.vue
│   │   ├── system/
│   │   │   ├── user/
│   │   │   ├── role/
│   │   │   └── config/
│   │   ├── station/
│   │   ├── device/
│   │   │   ├── Index.vue
│   │   │   ├── Form.vue
│   │   │   ├── Import.vue
│   │   │   └── QrCode.vue
│   │   ├── template/
│   │   ├── plan/
│   │   ├── task/
│   │   ├── record/
│   │   ├── defect/
│   │   ├── report/
│   │   └── statistics/
│   │
│   ├── stores/                                      # Pinia状态
│   │   ├── user.ts
│   │   ├── app.ts
│   │   └── dictionary.ts
│   │
│   ├── services/                                    # API服务
│   │   ├── api.ts
│   │   ├── auth.service.ts
│   │   ├── user.service.ts
│   │   ├── station.service.ts
│   │   ├── device.service.ts
│   │   ├── template.service.ts
│   │   ├── plan.service.ts
│   │   ├── task.service.ts
│   │   ├── record.service.ts
│   │   ├── defect.service.ts
│   │   ├── report.service.ts
│   │   ├── file.service.ts
│   │   ├── statistics.service.ts
│   │   └── system.service.ts
│   │
│   ├── router/                                      # 路由
│   │   ├── index.ts
│   │   └── routes.ts
│   │
│   ├── directives/                                  # 自定义指令
│   │   ├── permission.ts
│   │   └── loading.ts
│   │
│   ├── types/                                       # 类型定义
│   │   ├── api.d.ts
│   │   ├── models.d.ts
│   │   └── components.d.ts
│   │
│   ├── constants/                                   # 常量
│   │   ├── index.ts
│   │   ├── enums.ts
│   │   └── config.ts
│   │
│   ├── utils/                                       # 工具函数
│   │   ├── request.ts
│   │   ├── storage.ts
│   │   ├── auth.ts
│   │   ├── validate.ts
│   │   ├── format.ts
│   │   ├── date.ts
│   │   ├── download.ts
│   │   └── permission.ts
│   │
│   ├── App.vue
│   └── main.ts
│
├── .env
├── .env.development
├── .env.production
├── .eslintrc.js
├── .prettierrc
├── tsconfig.json
├── vite.config.ts
├── index.html
├── package.json
└── README.md
```

---

## 四、项目依赖说明

### 4.1 后端依赖 (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- Spring Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- MyBatis Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>3.5.5</version>
    </dependency>

    <!-- MySQL Driver -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Redis -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>

    <!-- BCrypt -->
    <dependency>
        <groupId>org.mindrot</groupId>
        <artifactId>jbcrypt</artifactId>
        <version>0.4</version>
    </dependency>

    <!-- Swagger/OpenAPI -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.3.0</version>
    </dependency>

    <!-- Apache POI (Excel处理) -->
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
        <version>5.2.5</version>
    </dependency>

    <!-- iText (PDF生成) -->
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>itextpdf</artifactId>
        <version>5.5.13.3</version>
    </dependency>

    <!-- ZXing (二维码) -->
    <dependency>
        <groupId>com.google.zxing</groupId>
        <artifactId>core</artifactId>
        <version>3.5.2</version>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Hutool工具类 -->
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
        <version>5.8.24</version>
    </dependency>
</dependencies>
```

### 4.2 H5端依赖 (package.json)

```json
{
  "dependencies": {
    "vue": "^3.4.0",
    "vue-router": "^4.2.5",
    "pinia": "^2.1.7",
    "vant": "^4.8.0",
    "axios": "^1.6.0",
    "dexie": "^3.2.4",
    "compressorjs": "^1.2.1",
    "qr-scanner": "^1.4.2",
    "qrcode": "^1.5.3",
    "dayjs": "^1.11.10"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.0",
    "vite": "^5.0.0",
    "vite-plugin-pwa": "^0.17.0",
    "typescript": "^5.3.0",
    "sass": "^1.69.0",
    "eslint": "^8.55.0",
    "prettier": "^3.1.0",
    "@types/qrcode": "^1.5.5"
  }
}
```

### 4.3 Web端依赖 (package.json)

```json
{
  "dependencies": {
    "vue": "^3.4.0",
    "vue-router": "^4.2.5",
    "pinia": "^2.1.7",
    "element-plus": "^2.5.0",
    "axios": "^1.6.0",
    "echarts": "^5.4.3",
    "@wangeditor/editor-for-vue": "^5.1.23",
    "xlsx": "^0.18.5",
    "dayjs": "^1.11.10"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.0",
    "vite": "^5.0.0",
    "typescript": "^5.3.0",
    "sass": "^1.69.0",
    "unplugin-vue-components": "^0.26.0",
    "eslint": "^8.55.0",
    "prettier": "^3.1.0"
  }
}
```
