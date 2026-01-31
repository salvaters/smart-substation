# 智能变电站设备巡检管理系统

## 项目简介

智能变电站设备巡检管理系统是一套完整的变电站设备巡检解决方案，采用前后端分离架构，支持移动端H5离线巡检和Web端管理后台。

### 核心功能

- 巡检任务管理 - 智能任务调度、分配、执行跟踪
- 设备二维码识别 - 一物一码、扫码快速巡检
- 缺陷上报 - 全流程缺陷管理、照片上传
- 报告生成 - 自动生成巡检报告、两级审批
- 离线数据同步 - H5支持离线操作、自动同步

## 技术架构

| 层级 | 技术选型 | 说明 |
|------|---------|------|
| 移动端 | Vue3 + Vant | H5移动端，支持离线操作 |
| Web端 | Vue3 + Ant Design Vue | 管理后台 |
| 后端 | Spring Boot + MyBatis Plus | RESTful API服务 |
| 数据库 | MySQL 8.0+ | 关系型数据库 |
| 缓存 | Redis 7.0+ | 缓存和会话管理 |
| 存储 | 本地文件系统 | 文件存储 |

## 项目结构

```
smart-substation/
├── backend/          # 后端Spring Boot项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/smartsubstation/
│   │   │   │       ├── common/      # 通用模块
│   │   │   │       ├── config/      # 配置类
│   │   │   │       ├── controller/  # 控制器
│   │   │   │       ├── dto/         # 数据传输对象
│   │   │   │       ├── entity/      # 实体类
│   │   │   │       ├── mapper/      # MyBatis Mapper
│   │   │   │       └── service/     # 服务层
│   │   │   └── resources/
│   │   │       ├── application.yml  # 配置文件
│   │   │       └── mapper/          # MyBatis XML
│   │   └── test/
│   └── pom.xml
├── mobile-h5/        # 移动端H5项目
│   ├── src/
│   │   ├── api/           # API接口
│   │   ├── assets/        # 静态资源
│   │   ├── components/    # 组件
│   │   ├── db/            # IndexedDB数据库
│   │   ├── router/        # 路由配置
│   │   ├── stores/        # Pinia状态管理
│   │   ├── utils/         # 工具函数
│   │   ├── views/         # 页面
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
├── web-admin/         # Web管理后台项目
│   ├── src/
│   │   ├── api/           # API接口
│   │   ├── assets/        # 静态资源
│   │   ├── components/    # 组件
│   │   ├── layouts/       # 布局组件
│   │   ├── router/        # 路由配置
│   │   ├── stores/        # Pinia状态管理
│   │   ├── utils/         # 工具函数
│   │   ├── views/         # 页面
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
└── docs/              # 设计文档
    ├── SUMMARY.md              # 项目总览
    ├── database-design.sql     # 数据库设计
    ├── api-design.md           # API接口设计
    ├── service-layer-design.md # 服务层设计
    ├── frontend-architecture.md# 前端架构设计
    ├── offline-sync-design.md  # 离线同步设计
    ├── security-design.md      # 安全设计
    ├── project-structure.md    # 项目结构
    └── implementation-plan.md  # 实施计划
```

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 7.0+

### 后端启动

```bash
cd backend

# 1. 修改配置文件
# 编辑 src/main/resources/application.yml
# 配置数据库连接和Redis连接

# 2. 执行数据库脚本
mysql -u root -p < docs/database-design.sql

# 3. 启动项目
mvn spring-boot:run
```

后端服务地址: http://localhost:8080/api

### 移动端H5启动

```bash
cd mobile-h5

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

移动端地址: http://localhost:3000

### Web管理后台启动

```bash
cd web-admin

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

Web管理后台地址: http://localhost:3001

### 默认账号

- 用户名: `admin`
- 密码: `admin123`

## 核心功能说明

### 1. 离线数据同步

移动端H5使用IndexedDB实现离线数据存储，支持：
- 离线任务列表缓存
- 离线填写巡检记录
- 照片延迟上传
- 网络恢复自动同步

### 2. 二维码巡检

- 设备二维码自动生成
- H5扫码快速识别设备
- 批量二维码导出

### 3. 报告审批

- 自动生成巡检报告
- 班长→管理员两级审批
- 报告导出（Word/Excel/PDF）

## API文档

后端使用Swagger生成API文档，启动后访问:
http://localhost:8080/api/swagger-ui.html

## 开发规范

### 后端代码规范

- 使用MyBatis Plus简化CRUD操作
- 统一使用`Result<T>`作为响应格式
- 使用`@Valid`进行参数校验
- 业务异常使用`BusinessException`

### 前端代码规范

- 使用Vue3 Composition API
- 使用Pinia进行状态管理
- 组件使用大驼峰命名
- API统一放在`api/`目录

## 数据库设计

数据库设计文档: [docs/database-design.sql](docs/database-design.sql)

核心数据表：
- `sys_user` - 用户表
- `device_info` - 设备表
- `inspection_task` - 巡检任务表
- `inspection_record` - 巡检记录表
- `defect_info` - 缺陷表
- `inspection_report` - 巡检报告表

## 部署说明

### 后端部署

```bash
# 打包项目
mvn clean package

# 运行jar包
java -jar target/smart-substation-1.0.0.jar
```

### 前端部署

```bash
# 构建生产版本
npm run build

# 将dist目录部署到Nginx
```

## 许可证

Copyright © 2024 Smart Substation Team

## 联系方式

- 项目负责人: [待定]
- 技术负责人: [待定]
