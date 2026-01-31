# 智能变电站巡检系统 - 服务层设计文档

## 一、服务层架构

### 1.1 分层架构
```
Controller Layer (控制器层)
    ↓
Service Layer (服务层)
    ↓
Mapper Layer (数据访问层)
    ↓
Database (数据库)
```

### 1.2 核心服务模块

```
smart-substation-service/
├── com.smart.substation.service
│   ├── auth                    # 认证服务
│   ├── user                    # 用户服务
│   ├── station                 # 变电站服务
│   ├── device                  # 设备服务
│   ├── template                # 巡检模板服务
│   ├── plan                    # 巡检计划服务
│   ├── task                    # 巡检任务服务
│   ├── record                  # 巡检记录服务
│   ├── defect                  # 缺陷服务
│   ├── report                  # 报告服务
│   ├── file                    # 文件服务
│   ├── sync                    # 同步服务
│   ├── statistics              # 统计服务
│   └── system                  # 系统服务
```

---

## 二、核心服务接口设计

### 2.1 认证服务 (IAuthService)

```java
public interface IAuthService {
    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应(包含Token)
     */
    LoginResponse login(LoginRequest request);

    /**
     * 刷新Token
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    TokenResponse refreshToken(String refreshToken);

    /**
     * 用户登出
     * @param token 访问令牌
     */
    void logout(String token);

    /**
     * 获取当前用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfoDTO getUserInfo(Long userId);

    /**
     * 修改密码
     * @param request 修改密码请求
     */
    void changePassword(ChangePasswordRequest request);

    /**
     * 验证Token有效性
     * @param token 访问令牌
     * @return 是否有效
     */
    boolean validateToken(String token);

    /**
     * 生成Token
     * @param user 用户信息
     * @return Token
     */
    String generateToken(User user);
}
```

### 2.2 用户服务 (IUserService)

```java
public interface IUserService extends IService<User> {
    /**
     * 分页查询用户列表
     * @param queryParams 查询参数
     * @return 分页结果
     */
    IPage<UserVO> pageUsers(UserQueryDTO queryParams);

    /**
     * 创建用户
     * @param request 创建请求
     * @return 用户ID
     */
    Long createUser(CreateUserRequest request);

    /**
     * 更新用户
     * @param userId 用户ID
     * @param request 更新请求
     */
    void updateUser(Long userId, UpdateUserRequest request);

    /**
     * 删除用户(逻辑删除)
     * @param userId 用户ID
     */
    void deleteUser(Long userId);

    /**
     * 重置用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     */
    void resetPassword(Long userId, String newPassword);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @param excludeUserId 排除的用户ID(更新时用)
     * @return 是否存在
     */
    boolean checkUsernameExists(String username, Long excludeUserId);

    /**
     * 根据角色ID获取用户列表
     * @param roleId 角色ID
     * @return 用户列表
     */
    List<UserVO> listUsersByRoleId(Long roleId);
}
```

### 2.3 变电站服务 (IStationService)

```java
public interface IStationService extends IService<SubStation> {
    /**
     * 分页查询变电站列表
     * @param queryParams 查询参数
     * @return 分页结果
     */
    IPage<StationVO> pageStations(StationQueryDTO queryParams);

    /**
     * 获取变电站详情
     * @param stationId 变电站ID
     * @return 变电站详情
     */
    StationDetailVO getStationDetail(Long stationId);

    /**
     * 创建变电站
     * @param request 创建请求
     * @return 变电站ID
     */
    Long createStation(CreateStationRequest request);

    /**
     * 更新变电站
     * @param stationId 变电站ID
     * @param request 更新请求
     */
    void updateStation(Long stationId, UpdateStationRequest request);

    /**
     * 删除变电站
     * @param stationId 变电站ID
     */
    void deleteStation(Long stationId);

    /**
     * 获取所有变电站列表(不分页)
     * @return 变电站列表
     */
    List<StationVO> listAllStations();

    /**
     * 检查变电站编码是否存在
     * @param stationCode 变电站编码
     * @param excludeStationId 排除的ID
     * @return 是否存在
     */
    boolean checkStationCodeExists(String stationCode, Long excludeStationId);

    /**
     * 获取变电站统计数据
     * @param stationId 变电站ID
     * @return 统计数据
     */
    StationStatisticsVO getStationStatistics(Long stationId);
}
```

### 2.4 设备服务 (IDeviceService)

```java
public interface IDeviceService extends IService<DeviceInfo> {
    /**
     * 分页查询设备列表
     * @param queryParams 查询参数
     * @return 分页结果
     */
    IPage<DeviceVO> pageDevices(DeviceQueryDTO queryParams);

    /**
     * 获取设备详情
     * @param deviceId 设备ID
     * @return 设备详情
     */
    DeviceDetailVO getDeviceDetail(Long deviceId);

    /**
     * 创建设备
     * @param request 创建请求
     * @return 设备ID
     */
    Long createDevice(CreateDeviceRequest request);

    /**
     * 批量创建设备
     * @param requests 创建请求列表
     * @return 设备ID列表
     */
    List<Long> batchCreateDevices(List<CreateDeviceRequest> requests);

    /**
     * 更新设备
     * @param deviceId 设备ID
     * @param request 更新请求
     */
    void updateDevice(Long deviceId, UpdateDeviceRequest request);

    /**
     * 删除设备
     * @param deviceId 设备ID
     */
    void deleteDevice(Long deviceId);

    /**
     * 批量导入设备
     * @param file Excel文件
     * @param stationId 变电站ID
     * @return 导入结果
     */
    ImportResultVO importDevices(MultipartFile file, Long stationId);

    /**
     * 批量生成二维码
     * @param deviceIds 设备ID列表
     * @return 二维码压缩包下载URL
     */
    String batchGenerateQrCodes(List<Long> deviceIds);

    /**
     * 根据二维码查询设备
     * @param qrCode 二维码内容
     * @return 设备信息
     */
    DeviceVO getDeviceByQrCode(String qrCode);

    /**
     * 获取设备分类树
     * @return 分类树
     */
    List<CategoryTreeVO> getDeviceCategoryTree();

    /**
     * 检查设备编码是否存在
     * @param deviceCode 设备编码
     * @param excludeDeviceId 排除的ID
     * @return 是否存在
     */
    boolean checkDeviceCodeExists(String deviceCode, Long excludeDeviceId);

    /**
     * 增量同步设备数据(离线用)
     * @param lastSyncTime 最后同步时间
     * @return 增量数据
     */
    SyncDataVO<DeviceVO> syncDevices(Long lastSyncTime);

    /**
     * 获取设备巡检历史
     * @param deviceId 设备ID
     * @param limit 数量限制
     * @return 巡检历史
     */
    List<RecordVO> getDeviceInspectionHistory(Long deviceId, Integer limit);
}
```

### 2.5 巡检模板服务 (IInspectionTemplateService)

```java
public interface IInspectionTemplateService extends IService<InspectionTemplate> {
    /**
     * 分页查询模板列表
     * @param queryParams 查询参数
     * @return 分页结果
     */
    IPage<TemplateVO> pageTemplates(TemplateQueryDTO queryParams);

    /**
     * 获取模板详情(包含检查项)
     * @param templateId 模板ID
     * @return 模板详情
     */
    TemplateDetailVO getTemplateDetail(Long templateId);

    /**
     * 创建模板
     * @param request 创建请求
     * @return 模板ID
     */
    Long createTemplate(CreateTemplateRequest request);

    /**
     * 更新模板
     * @param templateId 模板ID
     * @param request 更新请求
     */
    void updateTemplate(Long templateId, UpdateTemplateRequest request);

    /**
     * 删除模板
     * @param templateId 模板ID
     */
    void deleteTemplate(Long templateId);

    /**
     * 获取所有模板列表(不分页)
     * @return 模板列表
     */
    List<TemplateVO> listAllTemplates();

    /**
     * 根据设备分类获取适用模板
     * @param categoryId 设备分类ID
     * @return 模板列表
     */
    List<TemplateVO> listTemplatesByCategory(Long categoryId);

    /**
     * 复制模板
     * @param templateId 模板ID
     * @param newTemplateName 新模板名称
     * @return 新模板ID
     */
    Long copyTemplate(Long templateId, String newTemplateName);
}
```

### 2.6 巡检计划服务 (IInspectionPlanService)

```java
public interface IInspectionPlanService extends IService<InspectionPlan> {
    /**
     * 分页查询计划列表
     * @param queryParams 查询参数
     * @return 分页结果
     */
    IPage<PlanVO> pagePlans(PlanQueryDTO queryParams);

    /**
     * 创建巡检计划
     * @param request 创建请求
     * @return 计划ID
     */
    Long createPlan(CreatePlanRequest request);

    /**
     * 更新巡检计划
     * @param planId 计划ID
     * @param request 更新请求
     */
    void updatePlan(Long planId, UpdatePlanRequest request);

    /**
     * 删除巡检计划
     * @param planId 计划ID
     */
    void deletePlan(Long planId);

    /**
     * 暂停/恢复计划
     * @param planId 计划ID
     * @param status 状态
     */
    void updatePlanStatus(Long planId, String status);

    /**
     * 根据计划生成任务(定时任务调用)
     * @param planId 计划ID
     * @return 生成的任务ID列表
     */
    List<Long> generateTasksByPlan(Long planId);

    /**
     * 批量生成待执行任务(定时任务调用)
     * @return 生成的任务数量
     */
    Integer batchGeneratePendingTasks();

    /**
     * 获取计划下次执行时间
     * @param plan 计划
     * @return 下次执行时间
     */
    Date calculateNextExecuteTime(InspectionPlan plan);
}
```

### 2.7 巡检任务服务 (IInspectionTaskService)

```java
public interface IInspectionTaskService extends IService<InspectionTask> {
    /**
     * 分页查询任务列表
     * @param queryParams 查询参数
     * @return 分页结果
     */
    IPage<TaskVO> pageTasks(TaskQueryDTO queryParams);

    /**
     * 获取任务详情
     * @param taskId 任务ID
     * @return 任务详情
     */
    TaskDetailVO getTaskDetail(Long taskId);

    /**
     * 创建任务
     * @param request 创建请求
     * @return 任务ID
     */
    Long createTask(CreateTaskRequest request);

    /**
     * 开始任务
     * @param taskId 任务ID
     * @param userId 执行人ID
     */
    void startTask(Long taskId, Long userId);

    /**
     * 完成任务
     * @param taskId 任务ID
     * @param request 完成请求
     */
    void completeTask(Long taskId, CompleteTaskRequest request);

    /**
     * 取消任务
     * @param taskId 任务ID
     * @param reason 取消原因
     */
    void cancelTask(Long taskId, String reason);

    /**
     * 删除任务
     * @param taskId 任务ID
     */
    void deleteTask(Long taskId);

    /**
     * 获取我的任务列表(H5专用)
     * @param userId 用户ID
     * @param status 状态筛选
     * @return 任务列表
     */
    MyTasksVO getMyTasks(Long userId, String status);

    /**
     * 获取任务设备列表
     * @param taskId 任务ID
     * @return 设备列表
     */
    List<TaskDeviceVO> getTaskDevices(Long taskId);

    /**
     * 更新任务进度
     * @param taskId 任务ID
     */
    void updateTaskProgress(Long taskId);

    /**
     * 自动标记逾期任务(定时任务调用)
     * @return 逾期任务数量
     */
    Integer markOverdueTasks();

    /**
     * 增量同步任务数据(离线用)
     * @param userId 用户ID
     * @param lastSyncTime 最后同步时间
     * @return 任务数据
     */
    List<TaskVO> syncTasks(Long userId, Long lastSyncTime);
}
```

### 2.8 巡检记录服务 (IInspectionRecordService)

```java
public interface IInspectionRecordService extends IService<InspectionRecord> {
    /**
     * 提交巡检记录
     * @param request 提交请求
     * @return 记录ID列表
     */
    List<Long> submitRecords(SubmitRecordsRequest request);

    /**
     * 批量提交巡检记录
     * @param requests 批量提交请求
     * @return 提交结果
     */
    BatchSubmitResultVO batchSubmitRecords(List<SubmitRecordsRequest> requests);

    /**
     * 查询设备巡检记录列表
     * @param deviceId 设备ID
     * @param queryParams 查询参数
     * @return 分页结果
     */
    IPage<RecordVO> pageRecordsByDevice(Long deviceId, RecordQueryDTO queryParams);

    /**
     * 获取记录详情
     * @param recordId 记录ID
     * @return 记录详情
     */
    RecordDetailVO getRecordDetail(Long recordId);

    /**
     * 根据任务和设备获取记录
     * @param taskId 任务ID
     * @param deviceId 设备ID
     * @return 记录列表
     */
    List<RecordVO> listRecordsByTaskAndDevice(Long taskId, Long deviceId);

    /**
     * 删除记录
     * @param recordId 记录ID
     */
    void deleteRecord(Long recordId);

    /**
     * 批量导入记录(离线同步用)
     * @param records 记录列表
     * @return 导入结果
     */
    ImportResultVO importRecords(List<InspectionRecord> records);

    /**
     * 获取巡检统计
     * @param taskId 任务ID
     * @return 统计数据
     */
    InspectionStatisticsVO getInspectionStatistics(Long taskId);
}
```

### 2.9 缺陷服务 (IDefectService)

```java
public interface IDefectService extends IService<DefectInfo> {
    /**
     * 分页查询缺陷列表
     * @param queryParams 查询参数
     * @return 分页结果
     */
    IPage<DefectVO> pageDefects(DefectQueryDTO queryParams);

    /**
     * 获取缺陷详情
     * @param defectId 缺陷ID
     * @return 缺陷详情
     */
    DefectDetailVO getDefectDetail(Long defectId);

    /**
     * 创建缺陷
     * @param request 创建请求
     * @return 缺陷ID
     */
    Long createDefect(CreateDefectRequest request);

    /**
     * 确认缺陷(班长操作)
     * @param defectId 缺陷ID
     * @param request 确认请求
     */
    void confirmDefect(Long defectId, ConfirmDefectRequest request);

    /**
     * 更新缺陷处理状态
     * @param defectId 缺陷ID
     * @param request 更新请求
     */
    void updateDefectStatus(Long defectId, UpdateDefectStatusRequest request);

    /**
     * 完成缺陷处理
     * @param defectId 缺陷ID
     * @param request 完成请求
     */
    void completeDefect(Long defectId, CompleteDefectRequest request);

    /**
     * 取消缺陷
     * @param defectId 缺陷ID
     * @param reason 取消原因
     */
    void cancelDefect(Long defectId, String reason);

    /**
     * 获取缺陷统计
     * @param queryParams 查询参数
     * @return 统计数据
     */
    DefectStatisticsVO getDefectStatistics(DefectStatisticsQueryDTO queryParams);

    /**
     * 获取缺陷趋势
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param stationId 变电站ID(可选)
     * @return 趋势数据
     */
    List<DefectTrendVO> getDefectTrend(LocalDate startDate, LocalDate endDate, Long stationId);

    /**
     * 批量导入缺陷(离线同步用)
     * @param defects 缺陷列表
     * @return 导入结果
     */
    ImportResultVO importDefects(List<DefectInfo> defects);

    /**
     * 获取高风险缺陷列表
     * @param limit 数量限制
     * @return 缺陷列表
     */
    List<DefectVO> getHighRiskDefects(Integer limit);
}
```

### 2.10 报告服务 (IReportService)

```java
public interface IReportService extends IService<InspectionReport> {
    /**
     * 分页查询报告列表
     * @param queryParams 查询参数
     * @return 分页结果
     */
    IPage<ReportVO> pageReports(ReportQueryDTO queryParams);

    /**
     * 获取报告详情
     * @param reportId 报告ID
     * @return 报告详情
     */
    ReportDetailVO getReportDetail(Long reportId);

    /**
     * 根据任务生成报告
     * @param request 生成请求
     * @return 报告ID
     */
    Long generateReport(GenerateReportRequest request);

    /**
     * 提交报告审核
     * @param reportId 报告ID
     */
    void submitReport(Long reportId);

    /**
     * 审核报告
     * @param reportId 报告ID
     * @param request 审核请求
     */
    void approveReport(Long reportId, ApproveReportRequest request);

    /**
     * 导出报告
     * @param reportId 报告ID
     * @param format 格式(pdf/docx)
     * @return 文件下载URL
     */
    String exportReport(Long reportId, String format);

    /**
     * 删除报告
     * @param reportId 报告ID
     */
    void deleteReport(Long reportId);

    /**
     * 自动生成日报(定时任务调用)
     * @param date 日期
     * @return 生成的报告数量
     */
    Integer autoGenerateDailyReports(LocalDate date);

    /**
     * 自动生成周报(定时任务调用)
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 生成的报告数量
     */
    Integer autoGenerateWeeklyReports(LocalDate startDate, LocalDate endDate);
}
```

### 2.11 文件服务 (IFileService)

```java
public interface IFileService extends IService<SysFile> {
    /**
     * 上传图片
     * @param file 图片文件
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @return 文件信息
     */
    FileVO uploadImage(MultipartFile file, String businessType, Long businessId);

    /**
     * 批量上传图片
     * @param files 图片文件数组
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @return 文件信息列表
     */
    List<FileVO> batchUploadImages(MultipartFile[] files, String businessType, Long businessId);

    /**
     * 上传文档
     * @param file 文档文件
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @return 文件信息
     */
    FileVO uploadDocument(MultipartFile file, String businessType, Long businessId);

    /**
     * 删除文件
     * @param fileId 文件ID
     */
    void deleteFile(Long fileId);

    /**
     * 根据文件编码获取文件
     * @param fileCode 文件编码
     * @return 文件信息
     */
    SysFile getFileByCode(String fileCode);

    /**
     * 根据业务获取文件列表
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @return 文件列表
     */
    List<FileVO> listFilesByBusiness(String businessType, Long businessId);

    /**
     * 生成缩略图
     * @param originalPath 原图路径
     * @return 缩略图路径
     */
    String generateThumbnail(String originalPath);

    /**
     * 检查文件类型是否允许
     * @param contentType 文件类型
     * @param businessType 业务类型
     * @return 是否允许
     */
    boolean checkFileTypeAllowed(String contentType, String businessType);

    /**
     * 检查文件大小是否超限
     * @param fileSize 文件大小
     * @return 是否超限
     */
    boolean checkFileSizeExceeded(Long fileSize);

    /**
     * 保存文件到本地
     * @param file 文件
     * @param path 存储路径
     * @return 文件路径
     */
    String saveFileToLocal(MultipartFile file, String path);

    /**
     * 获取文件访问URL
     * @param filePath 文件路径
     * @return 访问URL
     */
    String getFileAccessUrl(String filePath);

    /**
     * 批量导入文件(离线同步用)
     * @param files 文件列表
     * @return 导入结果
     */
    ImportResultVO importFiles(List<SysFile> files);
}
```

### 2.12 同步服务 (ISyncService)

```java
public interface ISyncService {
    /**
     * 下载基础数据
     * @param lastSyncTime 最后同步时间
     * @return 基础数据
     */
    BaseDataSyncVO downloadBaseData(Long lastSyncTime);

    /**
     * 下载任务数据
     * @param userId 用户ID
     * @param lastSyncTime 最后同步时间
     * @return 任务数据
     */
    TaskDataSyncVO downloadTaskData(Long userId, Long lastSyncTime);

    /**
     * 上传离线数据
     * @param request 上传请求
     * @return 上传结果
     */
    SyncUploadResultVO uploadOfflineData(SyncUploadRequest request);

    /**
     * 上传离线文件
     * @param files 文件数组
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @param offlineCreateTime 离线创建时间
     * @return 上传结果
     */
    FileSyncResultVO uploadOfflineFiles(MultipartFile[] files, String businessType,
                                       Long businessId, Long offlineCreateTime);

    /**
     * 记录同步日志
     * @param userId 用户ID
     * @param syncType 同步类型
     * @param dataType 数据类型
     * @param syncResult 同步结果
     */
    void logSync(Long userId, String syncType, String dataType, SyncResultVO syncResult);

    /**
     * 获取同步日志
     * @param userId 用户ID
     * @param queryParams 查询参数
     * @return 日志列表
     */
    IPage<SyncLogVO> pageSyncLogs(Long userId, SyncLogQueryDTO queryParams);

    /**
     * 处理数据冲突
     * @param conflictData 冲突数据
     * @return 解决方案
     */
    ConflictResolutionVO resolveDataConflict(ConflictDataVO conflictData);
}
```

### 2.13 统计服务 (IStatisticsService)

```java
public interface IStatisticsService {
    /**
     * 获取巡检统计数据
     * @param queryParams 查询参数
     * @return 统计数据
     */
    InspectionStatisticsVO getInspectionStatistics(StatisticsQueryDTO queryParams);

    /**
     * 获取缺陷统计数据
     * @param queryParams 查询参数
     * @return 统计数据
     */
    DefectStatisticsVO getDefectStatistics(StatisticsQueryDTO queryParams);

    /**
     * 获取设备状态统计
     * @param stationId 变电站ID(可选)
     * @return 统计数据
     */
    DeviceStatisticsVO getDeviceStatistics(Long stationId);

    /**
     * 获取用户工作量统计
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计数据
     */
    UserWorkloadStatisticsVO getUserWorkloadStatistics(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取首页概览数据
     * @return 概览数据
     */
    DashboardOverviewVO getDashboardOverview();

    /**
     * 获取待办事项
     * @param userId 用户ID
     * @return 待办事项
     */
    TodoListVO getTodoList(Long userId);

    /**
     * 获取巡检趋势数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param stationId 变电站ID(可选)
     * @return 趋势数据
     */
    List<TrendVO> getInspectionTrend(LocalDate startDate, LocalDate endDate, Long stationId);

    /**
     * 获取缺陷趋势数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param stationId 变电站ID(可选)
     * @return 趋势数据
     */
    List<TrendVO> getDefectTrend(LocalDate startDate, LocalDate endDate, Long stationId);

    /**
     * 获取设备统计排行
     * @param stationId 变电站ID(可选)
     * @param limit 数量限制
     * @return 排行数据
     */
    List<DeviceRankingVO> getDeviceRanking(Long stationId, Integer limit);

    /**
     * 获取用户工作量排行
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param limit 数量限制
     * @return 排行数据
     */
    List<UserRankingVO> getUserRanking(LocalDate startDate, LocalDate endDate, Integer limit);
}
```

---

## 三、事务边界设计

### 3.1 事务管理策略

```java
@Service
@Transactional(readOnly = true)
public class InspectionTaskServiceImpl implements IInspectionTaskService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTask(CreateTaskRequest request) {
        // 1. 创建任务主记录
        InspectionTask task = new InspectionTask();
        // ... 设置属性
        save(task);

        // 2. 关联设备
        List<TaskDeviceRelation> relations = new ArrayList<>();
        for (int i = 0; i < request.getDeviceIds().size(); i++) {
            TaskDeviceRelation relation = new TaskDeviceRelation();
            relation.setTaskId(task.getTaskId());
            relation.setDeviceId(request.getDeviceIds().get(i));
            relation.setSortOrder(i + 1);
            relations.add(relation);
        }
        taskDeviceRelationService.saveBatch(relations);

        return task.getTaskId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeTask(Long taskId, CompleteTaskRequest request) {
        // 1. 更新任务状态
        InspectionTask task = getById(taskId);
        task.setStatus(TaskStatus.COMPLETED.getCode());
        task.setActualEndTime(new Date());
        task.setProgress(100);
        updateById(task);

        // 2. 自动生成报告
        reportService.generateReportFromTask(taskId, request);

        // 3. 发送通知
        notificationService.sendTaskCompleteNotification(taskId);
    }
}
```

### 3.2 事务传播行为

```java
@Service
public class InspectionRecordServiceImpl implements IInspectionRecordService {

    /**
     * 提交巡检记录 - 使用 REQUIRED 传播行为
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<Long> submitRecords(SubmitRecordsRequest request) {
        List<Long> recordIds = new ArrayList<>();

        for (RecordItem item : request.getRecords()) {
            // 保存记录
            InspectionRecord record = buildRecord(request, item);
            save(record);
            recordIds.add(record.getRecordId());

            // 如果检查结果异常,自动创建缺陷
            if ("abnormal".equals(item.getCheckResult())) {
                defectService.createDefectFromRecord(record);
            }
        }

        // 更新任务进度
        taskService.updateTaskProgress(request.getTaskId());

        return recordIds;
    }

    /**
     * 批量导入记录 - 使用 REQUIRES_NEW 传播行为,每个批次独立事务
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public ImportResultVO importRecords(List<InspectionRecord> records) {
        // 批量导入逻辑
    }
}
```

---

## 四、异常处理策略

### 4.1 自定义异常体系

```java
// 基础异常类
public class BaseException extends RuntimeException {
    private Integer code;
    private String message;

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}

// 业务异常
public class BusinessException extends BaseException {
    public BusinessException(String message) {
        super(500, message);
    }

    public BusinessException(Integer code, String message) {
        super(code, message);
    }
}

// 资源不存在异常
public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resourceName, Long id) {
        super(404, String.format("%s [%d] 不存在", resourceName, id));
    }
}

// 权限异常
public class PermissionDeniedException extends BusinessException {
    public PermissionDeniedException(String message) {
        super(403, message);
    }
}

// 数据冲突异常
public class DataConflictException extends BusinessException {
    public DataConflictException(String message) {
        super(409, message);
    }
}

// 离线同步异常
public class OfflineSyncException extends BusinessException {
    public OfflineSyncException(String message) {
        super(1001, message);
    }
}
```

### 4.2 全局异常处理器

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理资源不存在异常
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public Result<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.warn("资源不存在: {}", e.getMessage());
        return Result.error(404, e.getMessage());
    }

    /**
     * 处理权限异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<?> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("权限拒绝: {}", e.getMessage());
        return Result.error(403, "无权限访问");
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        log.warn("参数验证失败: {}", message);
        return Result.error(400, message);
    }

    /**
     * 处理数据库异常
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("数据完整性异常", e);
        return Result.error(500, "数据操作失败,请检查数据完整性");
    }

    /**
     * 处理未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "系统内部错误");
    }
}
```

---

## 五、服务间依赖关系

```
                    ┌─────────────────┐
                    │ IAuthService    │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │ IUserService    │
                    └────────┬────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
┌───────▼───────┐   ┌───────▼───────┐   ┌───────▼───────┐
│IStationService│   │IDeviceService │   │ IPlanService │
└───────┬───────┘   └───────┬───────┘   └───────┬───────┘
        │                   │                   │
        └───────────────────┼───────────────────┘
                            │
                   ┌────────▼────────┐
                   │ ITaskService    │
                   └────────┬────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
┌───────▼───────┐  ┌────────▼────────┐  ┌──────▼───────┐
│IRecordService │  │ IDefectService  │  │IReportService│
└───────────────┘  └─────────────────┘  └──────────────┘
        │                   │                   │
        └───────────────────┼───────────────────┘
                            │
                   ┌────────▼────────┐
                   │ IFileService    │
                   └─────────────────┘

                            │
                   ┌────────▼────────┐
                   │ ISyncService    │
                   └────────┬────────┘
                            │
                   ┌────────▼────────┐
                   │IStatisticsService│
                   └─────────────────┘
```

---

## 六、缓存策略

### 6.1 缓存使用场景

```java
@Service
public class DeviceServiceImpl implements IDeviceService {

    private static final String CACHE_KEY_PREFIX = "device:";
    private static final String CACHE_KEY_ALL = "device:all";

    /**
     * 获取设备详情 - 使用缓存
     */
    @Cacheable(value = "device", key = "#deviceId", unless = "#result == null")
    @Override
    public DeviceDetailVO getDeviceDetail(Long deviceId) {
        DeviceInfo device = getById(deviceId);
        if (device == null) {
            throw new ResourceNotFoundException("设备", deviceId);
        }
        return buildDeviceDetail(device);
    }

    /**
     * 更新设备 - 清除缓存
     */
    @CacheEvict(value = "device", key = "#deviceId")
    @Override
    public void updateDevice(Long deviceId, UpdateDeviceRequest request) {
        // 更新逻辑
    }

    /**
     * 获取所有设备列表 - 使用缓存
     */
    @Cacheable(value = "device", key = "'all'", unless = "#result.isEmpty()")
    @Override
    public List<DeviceVO> listAllDevices() {
        return list().stream()
            .map(this::buildDeviceVO)
            .collect(Collectors.toList());
    }

    /**
     * 批量创建设备 - 清除所有设备缓存
     */
    @CacheEvict(value = "device", allEntries = true)
    @Override
    public List<Long> batchCreateDevices(List<CreateDeviceRequest> requests) {
        // 批量创建逻辑
    }
}
```

### 6.2 缓存配置

```java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30))
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()))
            .disableCachingNullValues();

        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .build();
    }
}
```

---

## 七、异步处理

### 7.1 异步任务配置

```java
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean("taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
```

### 7.2 异步服务方法

```java
@Service
public class NotificationServiceImpl implements INotificationService {

    /**
     * 异步发送通知
     */
    @Async("taskExecutor")
    @Override
    public void sendNotificationAsync(NotificationMessage message) {
        // 发送通知逻辑
    }
}
```

---

## 八、定时任务配置

```java
@Component
@Slf4j
public class ScheduledTasks {

    @Autowired
    private IInspectionPlanService planService;

    @Autowired
    private IInspectionTaskService taskService;

    @Autowired
    private IReportService reportService;

    /**
     * 每天凌晨1点生成待执行任务
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void generateDailyTasks() {
        log.info("开始生成每日巡检任务");
        Integer count = planService.batchGeneratePendingTasks();
        log.info("生成每日巡检任务完成,共生成{}个任务", count);
    }

    /**
     * 每小时标记逾期任务
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void markOverdueTasks() {
        log.info("开始标记逾期任务");
        Integer count = taskService.markOverdueTasks();
        log.info("标记逾期任务完成,共{}个任务", count);
    }

    /**
     * 每天早上8点自动生成前一天的报告
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void autoGenerateDailyReports() {
        log.info("开始生成每日巡检报告");
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Integer count = reportService.autoGenerateDailyReports(yesterday);
        log.info("生成每日巡检报告完成,共生成{}个报告", count);
    }
}
```
