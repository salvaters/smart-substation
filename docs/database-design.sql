-- ============================================
-- 智能变电站巡检系统 - 数据库设计脚本
-- 数据库: smart_substation
-- 字符集: utf8mb4
-- ============================================

CREATE DATABASE IF NOT EXISTS smart_substation DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smart_substation;

-- ============================================
-- 1. 用户表 (sys_user)
-- ============================================
CREATE TABLE sys_user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    dept_id BIGINT COMMENT '部门ID',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
    INDEX idx_username (username),
    INDEX idx_role_id (role_id),
    INDEX idx_dept_id (dept_id),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 2. 角色表 (sys_role)
-- ============================================
CREATE TABLE sys_role (
    role_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    description VARCHAR(200) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
    INDEX idx_role_code (role_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ============================================
-- 3. 权限表 (sys_permission)
-- ============================================
CREATE TABLE sys_permission (
    permission_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    resource_type VARCHAR(20) NOT NULL COMMENT '资源类型: menu-菜单, button-按钮, api-接口',
    resource_path VARCHAR(255) COMMENT '资源路径',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
    INDEX idx_permission_code (permission_code),
    INDEX idx_resource_type (resource_type),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- ============================================
-- 4. 角色权限关联表 (sys_role_permission)
-- ============================================
CREATE TABLE sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ============================================
-- 5. 变电站表 (sub_station)
-- ============================================
CREATE TABLE sub_station (
    station_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '变电站ID',
    station_code VARCHAR(50) NOT NULL UNIQUE COMMENT '变电站编码',
    station_name VARCHAR(100) NOT NULL COMMENT '变电站名称',
    station_type VARCHAR(50) COMMENT '变电站类型: 500kV, 220kV, 110kV, 35kV',
    address VARCHAR(255) COMMENT '地址',
    longitude DECIMAL(10, 7) COMMENT '经度',
    latitude DECIMAL(10, 7) COMMENT '纬度',
    capacity VARCHAR(50) COMMENT '容量',
    voltage_level VARCHAR(50) COMMENT '电压等级',
    operator VARCHAR(100) COMMENT '运维单位',
    responsible_person VARCHAR(50) COMMENT '负责人',
    responsible_phone VARCHAR(20) COMMENT '负责人电话',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-运行中, 2-停运, 3-检修中',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
    INDEX idx_station_code (station_code),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='变电站表';

-- ============================================
-- 6. 设备分类表 (device_category)
-- ============================================
CREATE TABLE device_category (
    category_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    category_code VARCHAR(50) NOT NULL UNIQUE COMMENT '分类编码',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    level INT DEFAULT 1 COMMENT '层级',
    path VARCHAR(500) COMMENT '分类路径',
    icon VARCHAR(255) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
    INDEX idx_category_code (category_code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备分类表';

-- ============================================
-- 7. 设备表 (device_info)
-- ============================================
CREATE TABLE device_info (
    device_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '设备ID',
    device_code VARCHAR(50) NOT NULL UNIQUE COMMENT '设备编码',
    device_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    device_model VARCHAR(100) COMMENT '设备型号',
    manufacturer VARCHAR(100) COMMENT '制造商',
    category_id BIGINT NOT NULL COMMENT '设备分类ID',
    station_id BIGINT NOT NULL COMMENT '所属变电站ID',
    location VARCHAR(255) COMMENT '安装位置',
    qr_code VARCHAR(255) NOT NULL COMMENT '二维码内容(唯一标识)',
    qr_code_url VARCHAR(255) COMMENT '二维码图片URL',
    rated_voltage VARCHAR(50) COMMENT '额定电压',
    rated_current VARCHAR(50) COMMENT '额定电流',
    rated_capacity VARCHAR(50) COMMENT '额定容量',
    commissioning_date DATE COMMENT '投运日期',
    warranty_expire_date DATE COMMENT '质保到期日',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 2-故障, 3-检修, 4-停用',
    version INT DEFAULT 1 COMMENT '版本号(离线同步用)',
    last_sync_time DATETIME COMMENT '最后同步时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
    INDEX idx_device_code (device_code),
    INDEX idx_category_id (category_id),
    INDEX idx_station_id (station_id),
    INDEX idx_qr_code (qr_code),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted),
    INDEX idx_version (version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备表';

-- ============================================
-- 8. 巡检项目模板表 (inspection_template)
-- ============================================
CREATE TABLE inspection_template (
    template_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '模板ID',
    template_code VARCHAR(50) NOT NULL UNIQUE COMMENT '模板编码',
    template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    category_id BIGINT COMMENT '适用设备分类ID',
    inspection_type VARCHAR(50) NOT NULL COMMENT '巡检类型: daily-日巡, weekly-周巡, monthly-月巡, special-专项巡检',
    cycle_days INT COMMENT '周期天数',
    description VARCHAR(500) COMMENT '模板描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    version INT DEFAULT 1 COMMENT '版本号',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
    INDEX idx_template_code (template_code),
    INDEX idx_category_id (category_id),
    INDEX idx_inspection_type (inspection_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡检项目模板表';

-- ============================================
-- 9. 巡检项目明细表 (inspection_template_item)
-- ============================================
CREATE TABLE inspection_template_item (
    item_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '项目ID',
    template_id BIGINT NOT NULL COMMENT '模板ID',
    item_code VARCHAR(50) NOT NULL COMMENT '项目编码',
    item_name VARCHAR(200) NOT NULL COMMENT '项目名称',
    item_type VARCHAR(50) NOT NULL COMMENT '项目类型: text-文本, number-数值, select-选择, boolean-是否, photo-照片',
    check_method VARCHAR(200) COMMENT '检查方法',
    acceptance_criteria VARCHAR(500) COMMENT '验收标准',
    options JSON COMMENT '选项(JSON格式, select类型用)',
    default_value VARCHAR(200) COMMENT '默认值',
    unit VARCHAR(20) COMMENT '单位',
    is_required TINYINT DEFAULT 0 COMMENT '是否必填: 1-是, 0-否',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_template_id (template_id),
    INDEX idx_item_code (item_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡检项目明细表';

-- ============================================
-- 10. 巡检计划表 (inspection_plan)
-- ============================================
CREATE TABLE inspection_plan (
    plan_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '计划ID',
    plan_code VARCHAR(50) NOT NULL UNIQUE COMMENT '计划编码',
    plan_name VARCHAR(100) NOT NULL COMMENT '计划名称',
    station_id BIGINT NOT NULL COMMENT '变电站ID',
    template_id BIGINT NOT NULL COMMENT '模板ID',
    inspection_type VARCHAR(50) NOT NULL COMMENT '巡检类型',
    cycle_type VARCHAR(50) NOT NULL COMMENT '周期类型: daily-每日, weekly-每周, monthly-每月',
    cycle_value INT COMMENT '周期值(如每周几)',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    assignee_id BIGINT NOT NULL COMMENT '执行人ID',
    assignee_name VARCHAR(50) COMMENT '执行人姓名(冗余)',
    reviewer_id BIGINT COMMENT '审核人ID(班长)',
    reviewer_name VARCHAR(50) COMMENT '审核人姓名',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态: active-活动中, paused-暂停, completed-已完成, cancelled-已取消',
    last_execute_time DATETIME COMMENT '最后执行时间',
    next_execute_time DATETIME COMMENT '下次执行时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
    INDEX idx_plan_code (plan_code),
    INDEX idx_station_id (station_id),
    INDEX idx_assignee_id (assignee_id),
    INDEX idx_status (status),
    INDEX idx_next_execute_time (next_execute_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡检计划表';

-- ============================================
-- 11. 巡检任务表 (inspection_task)
-- ============================================
CREATE TABLE inspection_task (
    task_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    task_code VARCHAR(50) NOT NULL UNIQUE COMMENT '任务编码',
    plan_id BIGINT COMMENT '计划ID',
    station_id BIGINT NOT NULL COMMENT '变电站ID',
    station_name VARCHAR(100) COMMENT '变电站名称(冗余)',
    template_id BIGINT NOT NULL COMMENT '模板ID',
    template_name VARCHAR(100) COMMENT '模板名称(冗余)',
    inspection_type VARCHAR(50) NOT NULL COMMENT '巡检类型',
    title VARCHAR(200) NOT NULL COMMENT '任务标题',
    description VARCHAR(500) COMMENT '任务描述',
    assignee_id BIGINT NOT NULL COMMENT '执行人ID',
    assignee_name VARCHAR(50) COMMENT '执行人姓名',
    planned_start_time DATETIME NOT NULL COMMENT '计划开始时间',
    planned_end_time DATETIME NOT NULL COMMENT '计划结束时间',
    actual_start_time DATETIME COMMENT '实际开始时间',
    actual_end_time DATETIME COMMENT '实际结束时间',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending-待执行, in_progress-进行中, completed-已完成, overdue-已逾期, cancelled-已取消',
    progress INT DEFAULT 0 COMMENT '进度百分比',
    device_count INT DEFAULT 0 COMMENT '设备数量',
    completed_count INT DEFAULT 0 COMMENT '已完成设备数',
    defect_count INT DEFAULT 0 COMMENT '缺陷数量',
    version INT DEFAULT 1 COMMENT '版本号(离线同步用)',
    is_offline TINYINT DEFAULT 0 COMMENT '是否离线创建: 1-是, 0-否',
    offline_create_time DATETIME COMMENT '离线创建时间',
    last_sync_time DATETIME COMMENT '最后同步时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
    INDEX idx_task_code (task_code),
    INDEX idx_plan_id (plan_id),
    INDEX idx_station_id (station_id),
    INDEX idx_assignee_id (assignee_id),
    INDEX idx_status (status),
    INDEX idx_planned_start_time (planned_start_time),
    INDEX idx_version (version),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡检任务表';

-- ============================================
-- 12. 任务设备关联表 (task_device_relation)
-- ============================================
CREATE TABLE task_device_relation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    device_id BIGINT NOT NULL COMMENT '设备ID',
    device_code VARCHAR(50) COMMENT '设备编码(冗余)',
    device_name VARCHAR(100) COMMENT '设备名称(冗余)',
    location VARCHAR(255) COMMENT '设备位置(冗余)',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending-待检, completed-已检, skipped-跳过',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_task_device (task_id, device_id),
    INDEX idx_task_id (task_id),
    INDEX idx_device_id (device_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务设备关联表';

-- ============================================
-- 13. 巡检记录表 (inspection_record)
-- ============================================
CREATE TABLE inspection_record (
    record_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    record_code VARCHAR(50) NOT NULL UNIQUE COMMENT '记录编码',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    device_id BIGINT NOT NULL COMMENT '设备ID',
    device_code VARCHAR(50) COMMENT '设备编码(冗余)',
    device_name VARCHAR(100) COMMENT '设备名称(冗余)',
    item_id BIGINT NOT NULL COMMENT '检查项ID',
    item_name VARCHAR(200) COMMENT '检查项名称(冗余)',
    item_type VARCHAR(50) COMMENT '检查项类型',
    check_value TEXT COMMENT '检查值',
    check_result VARCHAR(20) COMMENT '检查结果: normal-正常, abnormal-异常, serious-严重',
    photo_urls TEXT COMMENT '照片URL(JSON数组)',
    description TEXT COMMENT '描述说明',
    defect_id BIGINT COMMENT '关联缺陷ID',
    inspector_id BIGINT COMMENT '检查人ID',
    inspector_name VARCHAR(50) COMMENT '检查人姓名',
    check_time DATETIME COMMENT '检查时间',
    is_offline TINYINT DEFAULT 0 COMMENT '是否离线记录: 1-是, 0-否',
    offline_create_time DATETIME COMMENT '离线创建时间',
    version INT DEFAULT 1 COMMENT '版本号',
    last_sync_time DATETIME COMMENT '最后同步时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
    INDEX idx_record_code (record_code),
    INDEX idx_task_id (task_id),
    INDEX idx_device_id (device_id),
    INDEX idx_item_id (item_id),
    INDEX idx_check_result (check_result),
    INDEX idx_defect_id (defect_id),
    INDEX idx_version (version),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡检记录表';

-- ============================================
-- 14. 缺陷表 (defect_info)
-- ============================================
CREATE TABLE defect_info (
    defect_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '缺陷ID',
    defect_code VARCHAR(50) NOT NULL UNIQUE COMMENT '缺陷编号',
    station_id BIGINT NOT NULL COMMENT '变电站ID',
    station_name VARCHAR(100) COMMENT '变电站名称',
    device_id BIGINT NOT NULL COMMENT '设备ID',
    device_code VARCHAR(50) COMMENT '设备编码',
    device_name VARCHAR(100) COMMENT '设备名称',
    record_id BIGINT COMMENT '关联巡检记录ID',
    defect_type VARCHAR(20) NOT NULL COMMENT '缺陷类型: critical-危急, serious-严重, general-一般',
    defect_description TEXT NOT NULL COMMENT '缺陷描述',
    defect_level VARCHAR(20) COMMENT '缺陷等级: A-紧急, B-重要, C-一般',
    photo_urls TEXT COMMENT '照片URL(JSON数组)',
    discoverer_id BIGINT NOT NULL COMMENT '发现人ID',
    discoverer_name VARCHAR(50) COMMENT '发现人姓名',
    discover_time DATETIME NOT NULL COMMENT '发现时间',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending-待处理, confirmed-已确认, repairing-处理中, completed-已完成, cancelled-已取消',
    confirmed_by BIGINT COMMENT '确认人ID',
    confirmed_time DATETIME COMMENT '确认时间',
    repair_plan TEXT COMMENT '整改方案',
    repair_person VARCHAR(100) COMMENT '整改人',
    repair_deadline DATE COMMENT '整改期限',
    actual_repair_time DATETIME COMMENT '实际整改时间',
    repair_description TEXT COMMENT '整改说明',
    repair_photos TEXT COMMENT '整改照片URL(JSON数组)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
    INDEX idx_defect_code (defect_code),
    INDEX idx_station_id (station_id),
    INDEX idx_device_id (device_id),
    INDEX idx_defect_type (defect_type),
    INDEX idx_status (status),
    INDEX idx_discover_time (discover_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='缺陷表';

-- ============================================
-- 15. 巡检报告表 (inspection_report)
-- ============================================
CREATE TABLE inspection_report (
    report_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '报告ID',
    report_code VARCHAR(50) NOT NULL UNIQUE COMMENT '报告编号',
    report_type VARCHAR(20) NOT NULL COMMENT '报告类型: daily-日报, weekly-周报, monthly-月报, custom-自定义',
    task_id BIGINT NOT NULL COMMENT '关联任务ID',
    station_id BIGINT NOT NULL COMMENT '变电站ID',
    station_name VARCHAR(100) COMMENT '变电站名称',
    report_period VARCHAR(50) COMMENT '报告周期',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    summary TEXT COMMENT '巡检总结',
    statistics JSON COMMENT '统计数据(JSON)',
    device_count INT COMMENT '设备总数',
    normal_count INT COMMENT '正常数量',
    abnormal_count INT COMMENT '异常数量',
    defect_count INT COMMENT '缺陷数量',
    creator_id BIGINT NOT NULL COMMENT '创建人ID',
    creator_name VARCHAR(50) COMMENT '创建人姓名',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态: draft-草稿, submitted-已提交, approved-已审核, rejected-已驳回',
    submit_time DATETIME COMMENT '提交时间',
    approver_id BIGINT COMMENT '审核人ID(班长)',
    approver_name VARCHAR(50) COMMENT '审核人姓名',
    approve_time DATETIME COMMENT '审核时间',
    approve_comment VARCHAR(500) COMMENT '审核意见',
    file_url VARCHAR(255) COMMENT '报告文件URL',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
    INDEX idx_report_code (report_code),
    INDEX idx_task_id (task_id),
    INDEX idx_station_id (station_id),
    INDEX idx_report_type (report_type),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡检报告表';

-- ============================================
-- 16. 文件表 (sys_file)
-- ============================================
CREATE TABLE sys_file (
    file_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文件ID',
    file_code VARCHAR(50) NOT NULL UNIQUE COMMENT '文件编码',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_type VARCHAR(50) NOT NULL COMMENT '文件类型: image-图片, video-视频, document-文档, other-其他',
    file_extension VARCHAR(20) NOT NULL COMMENT '文件扩展名',
    file_size BIGINT NOT NULL COMMENT '文件大小(字节)',
    file_path VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    file_url VARCHAR(500) COMMENT '文件访问URL',
    mime_type VARCHAR(100) COMMENT 'MIME类型',
    md5 VARCHAR(32) COMMENT '文件MD5',
    business_type VARCHAR(50) COMMENT '业务类型: inspection-巡检, defect-缺陷, avatar-头像等',
    business_id BIGINT COMMENT '业务ID',
    upload_user_id BIGINT COMMENT '上传人ID',
    upload_user_name VARCHAR(50) COMMENT '上传人姓名',
    is_offline TINYINT DEFAULT 0 COMMENT '是否离线上传: 1-是, 0-否',
    offline_create_time DATETIME COMMENT '离线创建时间',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 0-删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_file_code (file_code),
    INDEX idx_business (business_type, business_id),
    INDEX idx_upload_user_id (upload_user_id),
    INDEX idx_md5 (md5),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件表';

-- ============================================
-- 17. 离线数据同步日志表 (sync_log)
-- ============================================
CREATE TABLE sync_log (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    device_info VARCHAR(255) COMMENT '设备信息',
    sync_type VARCHAR(20) NOT NULL COMMENT '同步类型: upload-上传, download-下载',
    data_type VARCHAR(50) NOT NULL COMMENT '数据类型: task-任务, record-记录, file-文件',
    data_count INT DEFAULT 0 COMMENT '数据条数',
    success_count INT DEFAULT 0 COMMENT '成功条数',
    fail_count INT DEFAULT 0 COMMENT '失败条数',
    error_message TEXT COMMENT '错误信息',
    sync_start_time DATETIME NOT NULL COMMENT '同步开始时间',
    sync_end_time DATETIME COMMENT '同步结束时间',
    status VARCHAR(20) DEFAULT 'success' COMMENT '状态: success-成功, failed-失败, partial-部分成功',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_sync_type (sync_type),
    INDEX idx_status (status),
    INDEX idx_sync_start_time (sync_start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='离线数据同步日志表';

-- ============================================
-- 18. 系统配置表 (sys_config)
-- ============================================
CREATE TABLE sys_config (
    config_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(50) COMMENT '配置类型: string-字符串, number-数字, boolean-布尔, json-JSON',
    description VARCHAR(200) COMMENT '配置描述',
    group_name VARCHAR(50) COMMENT '配置分组',
    is_system TINYINT DEFAULT 0 COMMENT '是否系统配置: 1-是, 0-否',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_config_key (config_key),
    INDEX idx_group_name (group_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ============================================
-- 19. 操作日志表 (sys_operate_log)
-- ============================================
CREATE TABLE sys_operate_log (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    operation VARCHAR(50) COMMENT '操作类型: login-登录, logout-登出, create-创建, update-更新, delete-删除, export-导出等',
    module VARCHAR(50) COMMENT '模块名称',
    business_type VARCHAR(50) COMMENT '业务类型',
    business_id BIGINT COMMENT '业务ID',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_params TEXT COMMENT '请求参数',
    response_result TEXT COMMENT '响应结果',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    location VARCHAR(200) COMMENT '登录地点',
    browser VARCHAR(50) COMMENT '浏览器',
    os VARCHAR(50) COMMENT '操作系统',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-成功, 0-失败',
    error_msg TEXT COMMENT '错误信息',
    execute_time INT COMMENT '执行时长(毫秒)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_user_id (user_id),
    INDEX idx_operation (operation),
    INDEX idx_module (module),
    INDEX idx_create_time (create_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入默认角色
INSERT INTO sys_role (role_code, role_name, description) VALUES
('ADMIN', '系统管理员', '拥有系统所有权限'),
('MONITOR', '班长', '负责审核任务和报告'),
('INSPECTOR', '巡检员', '执行巡检任务'),
('GUEST', '访客', '只读权限');

-- 插入默认管理员 (密码: admin123, 使用BCrypt加密后)
INSERT INTO sys_user (username, password, real_name, role_id, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 1, 1);

-- 插入默认设备分类
INSERT INTO device_category (category_code, category_name, parent_id, level) VALUES
('PRIMARY', '一次设备', 0, 1),
('SECONDARY', '二次设备', 0, 1),
('PRIMARY_TRANSFORMER', '变压器', 1, 2),
('PRIMARY_SWITCH', '开关设备', 1, 2),
('PRIMARY_GIS', 'GIS设备', 1, 2),
('SECONDARY_PROTECTION', '继电保护', 2, 2),
('SECONDARY_CONTROL', '控制设备', 2, 2),
('SECONDARY_MEASURE', '计量设备', 2, 2);

-- 插入系统配置
INSERT INTO sys_config (config_key, config_value, config_type, description, group_name) VALUES
('file.upload.maxSize', '10485760', 'number', '文件上传最大大小(字节)', 'file'),
('file.upload.allowedTypes', 'jpg,jpeg,png,pdf,doc,docx,xls,xlsx', 'string', '允许上传的文件类型', 'file'),
('task.auto.create.days', '7', 'number', '自动创建任务天数', 'task'),
('offline.sync.retryTimes', '3', 'number', '离线同步重试次数', 'offline'),
('offline.sync.interval', '300', 'number', '离线同步间隔(秒)', 'offline'),
('token.expire.time', '7200', 'number', 'Token过期时间(秒)', 'security');
