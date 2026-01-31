# 智能变电站巡检系统 - 前端架构设计文档

## 一、前端技术栈

### 1.1 移动端H5 (Vue3 + Vant)
- **框架**: Vue 3.4+ (Composition API)
- **UI组件库**: Vant 4.x
- **状态管理**: Pinia 2.x
- **路由**: Vue Router 4.x
- **HTTP请求**: Axios
- **本地存储**: IndexedDB + LocalStorage
- **Service Worker**: Workbox 7.x
- **二维码扫描**: VueQrcodeReader / html5-qrcode
- **图片处理**: Compressor.js (图片压缩)
- **构建工具**: Vite 5.x
- **TypeScript**: 5.x
- **CSS预处理**: Sass/SCSS

### 1.2 Web端 (Vue3 + Element Plus)
- **框架**: Vue 3.4+ (Composition API)
- **UI组件库**: Element Plus 2.x
- **状态管理**: Pinia 2.x
- **路由**: Vue Router 4.x
- **HTTP请求**: Axios
- **图表**: ECharts 5.x
- **富文本编辑器**: WangEditor / TinyMCE
- **Excel处理**: XLSX
- **构建工具**: Vite 5.x
- **TypeScript**: 5.x
- **CSS预处理**: Sass/SCSS

---

## 二、项目目录结构

### 2.1 移动端H5目录结构

```
smart-substation-h5/
├── public/
│   ├── icons/                   # 图标
│   └── sw.js                    # Service Worker文件
├── src/
│   ├── assets/                  # 静态资源
│   │   ├── images/
│   │   ├── styles/
│   │   │   ├── variables.scss   # 样式变量
│   │   │   ├── mixins.scss      # 样式混入
│   │   │   └── common.scss      # 公共样式
│   │   └── fonts/
│   ├── components/              # 公共组件
│   │   ├── common/              # 通用组件
│   │   │   ├── PageHeader.vue   # 页面头部
│   │   │   ├── PageFooter.vue   # 页面底部
│   │   │   ├── Empty.vue        # 空状态
│   │   │   └── Loading.vue      # 加载中
│   │   ├── business/            # 业务组件
│   │   │   ├── DeviceCard.vue   # 设备卡片
│   │   │   ├── TaskCard.vue     # 任务卡片
│   │   │   ├── DefectCard.vue   # 缺陷卡片
│   │   │   ├── PhotoUploader.vue # 照片上传
│   │   │   ├── QrScanner.vue    # 二维码扫描
│   │   │   └── OfflineBanner.vue # 离线提示横幅
│   │   └── form/                # 表单组件
│   │       ├── InspectionForm.vue      # 巡检表单
│   │       ├── DefectForm.vue          # 缺陷表单
│   │       └── RecordFormItem.vue      # 记录表单项
│   ├── views/                   # 页面视图
│   │   ├── auth/                # 认证相关
│   │   │   └── Login.vue        # 登录页
│   │   ├── home/                # 首页
│   │   │   └── Index.vue        # 首页
│   │   ├── task/                # 任务管理
│   │   │   ├── TaskList.vue     # 任务列表
│   │   │   ├── TaskDetail.vue   # 任务详情
│   │   │   └── TaskInspection.vue # 任务巡检
│   │   ├── device/              # 设备管理
│   │   │   ├── DeviceList.vue   # 设备列表
│   │   │   └── DeviceDetail.vue # 设备详情
│   │   ├── record/              # 巡检记录
│   │   │   └── RecordList.vue   # 记录列表
│   │   ├── defect/              # 缺陷管理
│   │   │   ├── DefectList.vue   # 缺陷列表
│   │   │   └── DefectDetail.vue # 缺陷详情
│   │   ├── report/              # 报告管理
│   │   │   ├── ReportList.vue   # 报告列表
│   │   │   └── ReportDetail.vue # 报告详情
│   │   └── profile/             # 个人中心
│   │       ├── Profile.vue      # 个人中心
│   │       └── Settings.vue     # 设置
│   ├── stores/                  # Pinia状态管理
│   │   ├── user.ts              # 用户状态
│   │   ├── task.ts              # 任务状态
│   │   ├── offline.ts           # 离线状态
│   │   └── app.ts               # 应用状态
│   ├── services/                # API服务
│   │   ├── api.ts               # API基础配置
│   │   ├── auth.service.ts      # 认证服务
│   │   ├── task.service.ts      # 任务服务
│   │   ├── device.service.ts    # 设备服务
│   │   ├── record.service.ts    # 记录服务
│   │   ├── defect.service.ts    # 缺陷服务
│   │   ├── file.service.ts      # 文件服务
│   │   └── sync.service.ts      # 同步服务
│   ├── utils/                   # 工具函数
│   │   ├── request.ts           # 请求封装
│   │   ├── storage.ts           # 存储封装
│   │   ├── auth.ts              # 认证工具
│   │   ├── validate.ts          # 验证工具
│   │   ├── format.ts            # 格式化工具
│   │   ├── date.ts              # 日期工具
│   │   ├── image.ts             # 图片处理
│   │   └── network.ts           # 网络状态
│   ├── db/                      # IndexedDB封装
│   │   ├── index.ts             # DB初始化
│   │   ├── stores/
│   │   │   ├── task.store.ts    # 任务存储
│   │   │   ├── record.store.ts  # 记录存储
│   │   │   ├── device.store.ts  # 设备存储
│   │   │   ├── file.store.ts    # 文件存储
│   │   │   └── sync.store.ts    # 同步存储
│   │   └── sync.ts              # 同步逻辑
│   ├── composables/             # 组合式API
│   │   ├── useAuth.ts           # 认证钩子
│   │   ├── useTask.ts           # 任务钩子
│   │   ├── useOffline.ts        # 离线钩子
│   │   ├── useQrScanner.ts      # 扫码钩子
│   │   ├── useUpload.ts         # 上传钩子
│   │   └── useNetwork.ts        # 网络钩子
│   ├── router/                  # 路由配置
│   │   ├── index.ts             # 路由主文件
│   │   └── routes.ts            # 路由定义
│   ├── types/                   # TypeScript类型定义
│   │   ├── api.d.ts             # API类型
│   │   ├── models.d.ts          # 数据模型
│   │   └── components.d.ts      # 组件类型
│   ├── constants/               # 常量定义
│   │   ├── index.ts             # 通用常量
│   │   ├── enums.ts             # 枚举定义
│   │   └── config.ts            # 配置常量
│   ├── App.vue                  # 根组件
│   └── main.ts                  # 入口文件
├── index.html
├── vite.config.ts               # Vite配置
├── tsconfig.json                # TypeScript配置
└── package.json
```

### 2.2 Web端目录结构

```
smart-substation-web/
├── public/
│   ├── icons/
│   └── favicon.ico
├── src/
│   ├── assets/                  # 静态资源
│   │   ├── images/
│   │   ├── styles/
│   │   │   ├── variables.scss   # 样式变量
│   │   │   ├── mixins.scss      # 样式混入
│   │   │   └── common.scss      # 公共样式
│   │   └── fonts/
│   ├── components/              # 公共组件
│   │   ├── common/              # 通用组件
│   │   │   ├── AppHeader.vue    # 顶部导航
│   │   │   ├── AppAside.vue     # 侧边栏
│   │   │   ├── AppBreadcrumb.vue # 面包屑
│   │   │   └── AppFooter.vue    # 页脚
│   │   ├── business/            # 业务组件
│   │   │   ├── StationTree.vue  # 变电站树
│   │   │   ├── DeviceTable.vue  # 设备表格
│   │   │   ├── TaskTimeline.vue # 任务时间线
│   │   │   ├── StatisticsCard.vue # 统计卡片
│   │   │   └── ChartContainer.vue # 图表容器
│   │   └── form/                # 表单组件
│   │       ├── UserForm.vue     # 用户表单
│   │       ├── StationForm.vue  # 变电站表单
│   │       ├── DeviceForm.vue   # 设备表单
│   │       ├── PlanForm.vue     # 计划表单
│   │       └── TaskForm.vue     # 任务表单
│   ├── views/                   # 页面视图
│   │   ├── auth/                # 认证
│   │   │   └── Login.vue        # 登录页
│   │   ├── dashboard/           # 首页仪表板
│   │   │   └── Index.vue        # 仪表板
│   │   ├── system/              # 系统管理
│   │   │   ├── user/            # 用户管理
│   │   │   │   ├── Index.vue    # 用户列表
│   │   │   │   └── Form.vue     # 用户表单
│   │   │   ├── role/            # 角色管理
│   │   │   │   └── Index.vue    # 角色列表
│   │   │   └── config/          # 配置管理
│   │   │       └── Index.vue    # 配置列表
│   │   ├── station/             # 变电站管理
│   │   │   ├── Index.vue        # 变电站列表
│   │   │   └── Form.vue         # 变电站表单
│   │   ├── device/              # 设备管理
│   │   │   ├── Index.vue        # 设备列表
│   │   │   ├── Form.vue         # 设备表单
│   │   │   ├── Import.vue       # 设备导入
│   │   │   └── QrCode.vue       # 二维码生成
│   │   ├── template/            # 巡检模板
│   │   │   ├── Index.vue        # 模板列表
│   │   │   └── Form.vue         # 模板表单
│   │   ├── plan/                # 巡检计划
│   │   │   ├── Index.vue        # 计划列表
│   │   │   └── Form.vue         # 计划表单
│   │   ├── task/                # 巡检任务
│   │   │   ├── Index.vue        # 任务列表
│   │   │   ├── Detail.vue       # 任务详情
│   │   │   └── Record.vue       # 巡检记录
│   │   ├── record/              # 巡检记录
│   │   │   └── Index.vue        # 记录列表
│   │   ├── defect/              # 缺陷管理
│   │   │   ├── Index.vue        # 缺陷列表
│   │   │   ├── Detail.vue       # 缺陷详情
│   │   │   └── Statistics.vue   # 缺陷统计
│   │   ├── report/              # 巡检报告
│   │   │   ├── Index.vue        # 报告列表
│   │   │   ├── Detail.vue       # 报告详情
│   │   │   └── Review.vue       # 报告审核
│   │   └── statistics/          # 统计分析
│   │       ├── Index.vue        # 综合统计
│   │       ├── Task.vue         # 任务统计
│   │       ├── Defect.vue       # 缺陷统计
│   │       └── Device.vue       # 设备统计
│   ├── stores/                  # Pinia状态管理
│   │   ├── user.ts              # 用户状态
│   │   ├── app.ts               # 应用状态
│   │   └── dictionary.ts        # 字典状态
│   ├── services/                # API服务
│   │   ├── api.ts               # API基础配置
│   │   ├── auth.service.ts      # 认证服务
│   │   ├── user.service.ts      # 用户服务
│   │   ├── station.service.ts   # 变电站服务
│   │   ├── device.service.ts    # 设备服务
│   │   ├── template.service.ts  # 模板服务
│   │   ├── plan.service.ts      # 计划服务
│   │   ├── task.service.ts      # 任务服务
│   │   ├── record.service.ts    # 记录服务
│   │   ├── defect.service.ts    # 缺陷服务
│   │   ├── report.service.ts    # 报告服务
│   │   ├── file.service.ts      # 文件服务
│   │   ├── statistics.service.ts # 统计服务
│   │   └── system.service.ts    # 系统服务
│   ├── utils/                   # 工具函数
│   │   ├── request.ts           # 请求封装
│   │   ├── storage.ts           # 存储封装
│   │   ├── auth.ts              # 认证工具
│   │   ├── validate.ts          # 验证工具
│   │   ├── format.ts            # 格式化工具
│   │   ├── date.ts              # 日期工具
│   │   ├── download.ts          # 下载工具
│   │   └── permission.ts        # 权限工具
│   ├── router/                  # 路由配置
│   │   ├── index.ts             # 路由主文件
│   │   └── routes.ts            # 路由定义
│   ├── directives/              # 自定义指令
│   │   ├── permission.ts        # 权限指令
│   │   └── loading.ts           # 加载指令
│   ├── types/                   # TypeScript类型定义
│   │   ├── api.d.ts             # API类型
│   │   ├── models.d.ts          # 数据模型
│   │   └── components.d.ts      # 组件类型
│   ├── constants/               # 常量定义
│   │   ├── index.ts             # 通用常量
│   │   ├── enums.ts             # 枚举定义
│   │   └── config.ts            # 配置常量
│   ├── App.vue                  # 根组件
│   └── main.ts                  # 入口文件
├── index.html
├── vite.config.ts               # Vite配置
├── tsconfig.json                # TypeScript配置
└── package.json
```

---

## 三、路由设计

### 3.1 移动端H5路由

```typescript
// src/router/routes.ts
export const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/components/common/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/Index.vue'),
        meta: { title: '首页', icon: 'home-o', keepAlive: true }
      },
      {
        path: 'task',
        name: 'TaskList',
        component: () => import('@/views/task/TaskList.vue'),
        meta: { title: '任务列表', icon: 'todo-list-o', keepAlive: true }
      },
      {
        path: 'task/:id',
        name: 'TaskDetail',
        component: () => import('@/views/task/TaskDetail.vue'),
        meta: { title: '任务详情', keepAlive: false }
      },
      {
        path: 'task/:id/inspection',
        name: 'TaskInspection',
        component: () => import('@/views/task/TaskInspection.vue'),
        meta: { title: '巡检', keepAlive: false }
      },
      {
        path: 'device',
        name: 'DeviceList',
        component: () => import('@/views/device/DeviceList.vue'),
        meta: { title: '设备列表', icon: 'apps-o', keepAlive: true }
      },
      {
        path: 'device/:id',
        name: 'DeviceDetail',
        component: () => import('@/views/device/DeviceDetail.vue'),
        meta: { title: '设备详情', keepAlive: false }
      },
      {
        path: 'record',
        name: 'RecordList',
        component: () => import('@/views/record/RecordList.vue'),
        meta: { title: '巡检记录', icon: 'records', keepAlive: true }
      },
      {
        path: 'defect',
        name: 'DefectList',
        component: () => import('@/views/defect/DefectList.vue'),
        meta: { title: '缺陷列表', icon: 'warning-o', keepAlive: true }
      },
      {
        path: 'defect/:id',
        name: 'DefectDetail',
        component: () => import('@/views/defect/DefectDetail.vue'),
        meta: { title: '缺陷详情', keepAlive: false }
      },
      {
        path: 'report',
        name: 'ReportList',
        component: () => import('@/views/report/ReportList.vue'),
        meta: { title: '报告列表', icon: 'description', keepAlive: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/Profile.vue'),
        meta: { title: '我的', icon: 'user-o', keepAlive: true }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/profile/Settings.vue'),
        meta: { title: '设置', keepAlive: false }
      }
    ]
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/common/404.vue'),
    meta: { title: '页面不存在' }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]
```

### 3.2 Web端路由

```typescript
// src/router/routes.ts
export const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/components/common/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Index.vue'),
        meta: { title: '首页', icon: 'Dashboard', affix: true }
      },
      // 系统管理
      {
        path: 'system',
        name: 'System',
        meta: { title: '系统管理', icon: 'Setting' },
        children: [
          {
            path: 'user',
            name: 'SystemUser',
            component: () => import('@/views/system/user/Index.vue'),
            meta: { title: '用户管理', icon: 'User', permission: 'system:user:list' }
          },
          {
            path: 'role',
            name: 'SystemRole',
            component: () => import('@/views/system/role/Index.vue'),
            meta: { title: '角色管理', icon: 'UserFilled', permission: 'system:role:list' }
          },
          {
            path: 'config',
            name: 'SystemConfig',
            component: () => import('@/views/system/config/Index.vue'),
            meta: { title: '配置管理', icon: 'Tools', permission: 'system:config:list' }
          }
        ]
      },
      // 变电站管理
      {
        path: 'station',
        name: 'Station',
        component: () => import('@/views/station/Index.vue'),
        meta: { title: '变电站管理', icon: 'OfficeBuilding', permission: 'station:list' }
      },
      // 设备管理
      {
        path: 'device',
        name: 'Device',
        meta: { title: '设备管理', icon: 'Cpu', permission: 'device:list' },
        children: [
          {
            path: '',
            name: 'DeviceList',
            component: () => import('@/views/device/Index.vue'),
            meta: { title: '设备列表' }
          },
          {
            path: 'import',
            name: 'DeviceImport',
            component: () => import('@/views/device/Import.vue'),
            meta: { title: '设备导入', hidden: true }
          }
        ]
      },
      // 巡检模板
      {
        path: 'template',
        name: 'Template',
        component: () => import('@/views/template/Index.vue'),
        meta: { title: '巡检模板', icon: 'Document', permission: 'template:list' }
      },
      // 巡检计划
      {
        path: 'plan',
        name: 'Plan',
        component: () => import('@/views/plan/Index.vue'),
        meta: { title: '巡检计划', icon: 'Calendar', permission: 'plan:list' }
      },
      // 巡检任务
      {
        path: 'task',
        name: 'Task',
        meta: { title: '巡检任务', icon: 'List', permission: 'task:list' },
        children: [
          {
            path: '',
            name: 'TaskList',
            component: () => import('@/views/task/Index.vue'),
            meta: { title: '任务列表' }
          },
          {
            path: ':id',
            name: 'TaskDetail',
            component: () => import('@/views/task/Detail.vue'),
            meta: { title: '任务详情', hidden: true }
          },
          {
            path: ':id/record',
            name: 'TaskRecord',
            component: () => import('@/views/task/Record.vue'),
            meta: { title: '巡检记录', hidden: true }
          }
        ]
      },
      // 巡检记录
      {
        path: 'record',
        name: 'Record',
        component: () => import('@/views/record/Index.vue'),
        meta: { title: '巡检记录', icon: 'DocumentChecked', permission: 'record:list' }
      },
      // 缺陷管理
      {
        path: 'defect',
        name: 'Defect',
        meta: { title: '缺陷管理', icon: 'Warning', permission: 'defect:list' },
        children: [
          {
            path: '',
            name: 'DefectList',
            component: () => import('@/views/defect/Index.vue'),
            meta: { title: '缺陷列表' }
          },
          {
            path: ':id',
            name: 'DefectDetail',
            component: () => import('@/views/defect/Detail.vue'),
            meta: { title: '缺陷详情', hidden: true }
          },
          {
            path: 'statistics',
            name: 'DefectStatistics',
            component: () => import('@/views/defect/Statistics.vue'),
            meta: { title: '缺陷统计', hidden: true }
          }
        ]
      },
      // 巡检报告
      {
        path: 'report',
        name: 'Report',
        meta: { title: '巡检报告', icon: 'Files', permission: 'report:list' },
        children: [
          {
            path: '',
            name: 'ReportList',
            component: () => import('@/views/report/Index.vue'),
            meta: { title: '报告列表' }
          },
          {
            path: ':id',
            name: 'ReportDetail',
            component: () => import('@/views/report/Detail.vue'),
            meta: { title: '报告详情', hidden: true }
          },
          {
            path: ':id/review',
            name: 'ReportReview',
            component: () => import('@/views/report/Review.vue'),
            meta: { title: '报告审核', hidden: true }
          }
        ]
      },
      // 统计分析
      {
        path: 'statistics',
        name: 'Statistics',
        meta: { title: '统计分析', icon: 'DataAnalysis', permission: 'statistics:view' },
        children: [
          {
            path: '',
            name: 'StatisticsOverview',
            component: () => import('@/views/statistics/Index.vue'),
            meta: { title: '综合统计' }
          },
          {
            path: 'task',
            name: 'StatisticsTask',
            component: () => import('@/views/statistics/Task.vue'),
            meta: { title: '任务统计', hidden: true }
          },
          {
            path: 'defect',
            name: 'StatisticsDefect',
            component: () => import('@/views/statistics/Defect.vue'),
            meta: { title: '缺陷统计', hidden: true }
          },
          {
            path: 'device',
            name: 'StatisticsDevice',
            component: () => import('@/views/statistics/Device.vue'),
            meta: { title: '设备统计', hidden: true }
          }
        ]
      }
    ]
  }
]
```

---

## 四、状态管理设计

### 4.1 用户状态 (stores/user.ts)

```typescript
import { defineStore } from 'pinia'
import { login, logout, getUserInfo } from '@/services/auth.service'
import { setToken, removeToken } from '@/utils/auth'

interface UserState {
  token: string
  userInfo: UserInfo | null
  permissions: string[]
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: '',
    userInfo: null,
    permissions: []
  }),

  getters: {
    isLogin: (state) => !!state.token,
    hasPermission: (state) => (permission: string) => {
      return state.permissions.includes('*:*:*') || state.permissions.includes(permission)
    }
  },

  actions: {
    async login(username: string, password: string) {
      try {
        const response = await login({ username, password })
        this.token = response.data.token
        this.userInfo = response.data.userInfo
        this.permissions = response.data.userInfo.permissions
        setToken(response.data.token)
        return response
      } catch (error) {
        throw error
      }
    },

    async logout() {
      try {
        await logout()
      } finally {
        this.token = ''
        this.userInfo = null
        this.permissions = []
        removeToken()
      }
    },

    async getUserInfo() {
      try {
        const response = await getUserInfo()
        this.userInfo = response.data
        this.permissions = response.data.permissions
        return response
      } catch (error) {
        throw error
      }
    }
  }
})
```

### 4.2 任务状态 (stores/task.ts)

```typescript
import { defineStore } from 'pinia'
import { getMyTasks } from '@/services/task.service'

interface TaskState {
  myTasks: MyTasksData
  currentTask: TaskDetail | null
  currentDevice: DeviceInfo | null
  loading: boolean
}

export const useTaskStore = defineStore('task', {
  state: (): TaskState => ({
    myTasks: {
      pending: [],
      inProgress: [],
      completed: []
    },
    currentTask: null,
    currentDevice: null,
    loading: false
  }),

  actions: {
    async fetchMyTasks(status?: string) {
      this.loading = true
      try {
        const response = await getMyTasks(status)
        this.myTasks = response.data
      } catch (error) {
        throw error
      } finally {
        this.loading = false
      }
    },

    setCurrentTask(task: TaskDetail) {
      this.currentTask = task
    },

    setCurrentDevice(device: DeviceInfo) {
      this.currentDevice = device
    },

    clearCurrentTask() {
      this.currentTask = null
      this.currentDevice = null
    }
  }
})
```

### 4.3 离线状态 (stores/offline.ts)

```typescript
import { defineStore } from 'pinia'

interface OfflineState {
  isOnline: boolean
  offlineMode: boolean
  pendingSyncCount: number
  lastSyncTime: number | null
}

export const useOfflineStore = defineStore('offline', {
  state: (): OfflineState => ({
    isOnline: navigator.onLine,
    offlineMode: false,
    pendingSyncCount: 0,
    lastSyncTime: null
  }),

  getters: {
    canSync: (state) => state.isOnline && state.pendingSyncCount > 0,
    syncStatus: (state) => {
      if (!state.isOnline) return 'offline'
      if (state.pendingSyncCount > 0) return 'pending'
      return 'synced'
    }
  },

  actions: {
    setOnlineStatus(status: boolean) {
      this.isOnline = status
      if (status && this.offlineMode) {
        // 恢复在线,尝试同步
        this.syncPendingData()
      }
    },

    setOfflineMode(mode: boolean) {
      this.offlineMode = mode
    },

    incrementPendingCount() {
      this.pendingSyncCount++
    },

    decrementPendingCount() {
      if (this.pendingSyncCount > 0) {
        this.pendingSyncCount--
      }
    },

    async syncPendingData() {
      if (!this.isOnline || this.pendingSyncCount === 0) {
        return
      }
      // 触发同步
    },

    updateLastSyncTime() {
      this.lastSyncTime = Date.now()
    }
  }
})
```

---

## 五、核心页面设计

### 5.1 移动端H5核心页面

#### 首页 (views/home/Index.vue)
```vue
<template>
  <div class="home-page">
    <!-- 头部 -->
    <van-nav-bar title="首页" />

    <!-- 用户信息卡片 -->
    <van-cell-group inset class="user-card">
      <van-cell center>
        <template #title>
          <span class="user-name">{{ userInfo?.realName }}</span>
        </template>
        <template #label>
          <span class="user-role">{{ userInfo?.roleName }}</span>
        </template>
        <template #icon>
          <van-image round width="50" height="50" :src="userInfo?.avatar" />
        </template>
      </van-cell>
    </van-cell-group>

    <!-- 统计卡片 -->
    <van-grid :column-num="2" border class="stats-grid">
      <van-grid-item text="待执行" :icon="todoIcon" :badge="taskStats.pending" />
      <van-grid-item text="进行中" :icon="progressIcon" :badge="taskStats.inProgress" />
      <van-grid-item text="今日完成" :icon="checkIcon" :badge="taskStats.todayCompleted" />
      <van-grid-item text="发现缺陷" :icon="warningIcon" :badge="taskStats.defects" />
    </van-grid>

    <!-- 快捷入口 -->
    <van-cell-group title="快捷操作" inset>
      <van-cell title="扫码巡检" is-link @click="handleScanQrCode">
        <template #icon>
          <van-icon name="scan" class="cell-icon" />
        </template>
      </van-cell>
      <van-cell title="我的任务" is-link to="/task">
        <template #icon>
          <van-icon name="todo-list-o" class="cell-icon" />
        </template>
      </van-cell>
      <van-cell title="设备查询" is-link to="/device">
        <template #icon>
          <van-icon name="apps-o" class="cell-icon" />
        </template>
      </van-cell>
    </van-cell-group>

    <!-- 最新任务 -->
    <van-cell-group title="最新任务" inset>
      <van-cell
        v-for="task in recentTasks"
        :key="task.taskId"
        :title="task.title"
        :label="task.plannedStartTime"
        is-link
        :to="`/task/${task.taskId}`"
      />
    </van-cell-group>

    <!-- 离线状态提示 -->
    <offline-banner v-if="!isOnline" />
  </div>
</template>
```

#### 任务巡检页面 (views/task/TaskInspection.vue)
```vue
<template>
  <div class="task-inspection-page">
    <van-nav-bar
      title="设备巡检"
      left-text="返回"
      left-arrow
      @click-left="onBack"
    />

    <!-- 设备信息 -->
    <van-cell-group inset class="device-info">
      <van-cell title="设备名称" :value="currentDevice?.deviceName" />
      <van-cell title="设备编码" :value="currentDevice?.deviceCode" />
      <van-cell title="位置" :value="currentDevice?.location" />
    </van-cell-group>

    <!-- 巡检表单 -->
    <van-form @submit="onSubmit">
      <van-cell-group inset title="巡检项目">
        <inspection-form-item
          v-for="item in templateItems"
          :key="item.itemId"
          :item="item"
          v-model="formData[item.itemId]"
        />
      </van-cell-group>

      <!-- 提交按钮 -->
      <div class="submit-bar">
        <van-button round block type="primary" native-type="submit">
          提交记录
        </van-button>
      </div>
    </van-form>

    <!-- 离线状态提示 -->
    <offline-banner v-if="!isOnline" text="当前离线模式,数据将在恢复网络后同步" />
  </div>
</template>
```

### 5.2 Web端核心页面

#### 仪表板页面 (views/dashboard/Index.vue)
```vue
<template>
  <div class="dashboard-page">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <statistics-card
          title="变电站总数"
          :value="overview.stationCount"
          icon="OfficeBuilding"
          color="#409EFF"
        />
      </el-col>
      <el-col :span="6">
        <statistics-card
          title="设备总数"
          :value="overview.deviceCount"
          icon="Cpu"
          color="#67C23A"
        />
      </el-col>
      <el-col :span="6">
        <statistics-card
          title="今日任务"
          :value="overview.todayTasks"
          icon="List"
          color="#E6A23C"
        />
      </el-col>
      <el-col :span="6">
        <statistics-card
          title="待处理缺陷"
          :value="overview.pendingDefects"
          icon="Warning"
          color="#F56C6C"
        />
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <chart-container title="任务趋势">
          <trend-chart :data="overview.taskTrend" />
        </chart-container>
      </el-col>
      <el-col :span="12">
        <chart-container title="缺陷趋势">
          <trend-chart :data="overview.defectTrend" />
        </chart-container>
      </el-col>
    </el-row>

    <!-- 列表区域 -->
    <el-row :gutter="20" class="list-row">
      <el-col :span="12">
        <el-card header="最新任务">
          <task-table :data="overview.recentTasks" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card header="高风险缺陷">
          <defect-table :data="overview.highRiskDefects" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
```

---

## 六、核心组件设计

### 6.1 离线横幅组件 (components/business/OfflineBanner.vue)

```vue
<template>
  <div class="offline-banner" :class="{ visible }">
    <van-icon name="warning-o" />
    <span>{{ text }}</span>
    <van-button v-if="canSync" size="mini" type="primary" @click="handleSync">
      立即同步
    </van-button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useOfflineStore } from '@/stores/offline'

const props = defineProps<{
  text?: string
}>()

const offlineStore = useOfflineStore()

const visible = computed(() => !offlineStore.isOnline || offlineStore.offlineMode)
const canSync = computed(() => offlineStore.canSync)

const handleSync = async () => {
  // 触发同步
}
</script>
```

### 6.2 照片上传组件 (components/business/PhotoUploader.vue)

```vue
<template>
  <div class="photo-uploader">
    <van-uploader
      v-model="fileList"
      :after-read="afterRead"
      :before-delete="beforeDelete"
      multiple
      :max-count="maxCount"
      :accept="accept"
      :upload-text="uploadText"
    />
    <div v-if="compressTip" class="compress-tip">
      图片将自动压缩以提高上传速度
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { compressImage } from '@/utils/image'

interface Props {
  modelValue: string[]
  maxCount?: number
  accept?: string
  uploadText?: string
  compressTip?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  maxCount: 9,
  accept: 'image/*',
  uploadText: '上传照片',
  compressTip: true
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string[]): void
}>()

const fileList = ref([])

const afterRead = async (file: any) => {
  // 压缩图片
  const compressed = await compressImage(file.file)
  // 处理上传或保存
}
</script>
```

### 6.3 二维码扫描组件 (components/business/QrScanner.vue)

```vue
<template>
  <van-popup v-model:show="visible" position="bottom" :style="{ height: '100%' }">
    <div class="qr-scanner">
      <van-nav-bar
        title="扫码"
        left-text="取消"
        @click-left="handleCancel"
      />
      <div class="scanner-container">
        <qrcode-stream
          @decode="onDecode"
          @init="onInit"
        />
        <div class="scan-guide"></div>
      </div>
      <div class="scan-tip">将二维码放入框内即可自动扫描</div>
    </div>
  </van-popup>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { QrcodeStream } from 'qrcode-reader-vue3'

const visible = ref(false)
const emit = defineEmits<{
  (e: 'scan', result: string): void
  (e: 'cancel'): void
}>()

const onDecode = (result: string) => {
  emit('scan', result)
  visible.value = false
}

const handleCancel = () => {
  emit('cancel')
  visible.value = false
}

defineExpose({
  open: () => { visible.value = true },
  close: () => { visible.value = false }
})
</script>
```

---

## 七、Composables设计

### 7.1 useAuth - 认证钩子

```typescript
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

export function useAuth() {
  const userStore = useUserStore()
  const router = useRouter()

  const isLogin = computed(() => userStore.isLogin)
  const userInfo = computed(() => userStore.userInfo)
  const permissions = computed(() => userStore.permissions)

  const hasPermission = (permission: string) => {
    return userStore.hasPermission(permission)
  }

  const logout = async () => {
    await userStore.logout()
    router.push('/login')
  }

  return {
    isLogin,
    userInfo,
    permissions,
    hasPermission,
    logout
  }
}
```

### 7.2 useOffline - 离线钩子

```typescript
import { onMounted, onUnmounted } from 'vue'
import { useOfflineStore } from '@/stores/offline'

export function useOffline() {
  const offlineStore = useOfflineStore()

  const updateOnlineStatus = () => {
    offlineStore.setOnlineStatus(navigator.onLine)
  }

  onMounted(() => {
    window.addEventListener('online', updateOnlineStatus)
    window.addEventListener('offline', updateOnlineStatus)
    updateOnlineStatus()
  })

  onUnmounted(() => {
    window.removeEventListener('online', updateOnlineStatus)
    window.removeEventListener('offline', updateOnlineStatus)
  })

  return {
    isOnline: computed(() => offlineStore.isOnline),
    offlineMode: computed(() => offlineStore.offlineMode),
    pendingSyncCount: computed(() => offlineStore.pendingSyncCount),
    syncStatus: computed(() => offlineStore.syncStatus)
  }
}
```

### 7.3 useQrScanner - 扫码钩子

```typescript
import { ref } from 'vue'

export function useQrScanner() {
  const scannerRef = ref<InstanceType<any>>()
  const scanning = ref(false)

  const openScanner = () => {
    scannerRef.value?.open()
    scanning.value = true
  }

  const closeScanner = () => {
    scannerRef.value?.close()
    scanning.value = false
  }

  const onScan = (callback: (result: string) => void) => {
    // 处理扫描结果
  }

  return {
    scannerRef,
    scanning,
    openScanner,
    closeScanner,
    onScan
  }
}
```

---

## 八、网络请求封装

### 8.1 Axios配置

```typescript
import axios, { AxiosRequestConfig, AxiosResponse } from 'axios'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { useUserStore } from '@/stores/user'
import { getToken } from '@/utils/auth'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 15000
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    config.headers['X-Client-Type'] = 'h5'
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data

    if (res.code !== 200) {
      showToast({
        type: 'fail',
        message: res.message || '请求失败'
      })

      // 401: Token过期
      if (res.code === 401) {
        const userStore = useUserStore()
        userStore.logout()
        window.location.href = '/login'
      }

      return Promise.reject(new Error(res.message || 'Error'))
    }

    return res
  },
  (error) => {
    closeToast()
    showToast({
      type: 'fail',
      message: error.message || '网络错误'
    })
    return Promise.reject(error)
  }
)

export default service
```

### 8.2 离线请求处理

```typescript
import service from './request'
import { saveOfflineRequest } from '@/db/sync.store'

export async function requestWithOffline(config: AxiosRequestConfig) {
  try {
    return await service(config)
  } catch (error) {
    // 网络错误,保存离线请求
    if (!navigator.onLine) {
      await saveOfflineRequest({
        ...config,
        timestamp: Date.now()
      })
      throw new Error('当前离线,请求已保存,将在恢复网络后自动提交')
    }
    throw error
  }
}
```
