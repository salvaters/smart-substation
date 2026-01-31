# 智能变电站巡检系统 - API接口设计文档

## 版本信息
- API版本: v1.0
- 基础路径: `/api/v1`
- 数据格式: JSON
- 字符编码: UTF-8

## 通用说明

### 请求头
```
Content-Type: application/json
Authorization: Bearer {token}
X-Client-Type: web|h5
X-Device-Id: {设备唯一标识}
```

### 统一响应结构
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1706600000000
}
```

### 响应码说明
| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

### 分页参数
```
pageNum: 页码(从1开始)
pageSize: 每页数量
orderBy: 排序字段
isAsc: 是否升序(true/false)
```

### 分页响应
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "pages": 10,
    "pageNum": 1,
    "pageSize": 10,
    "list": []
  }
}
```

---

## 1. 认证模块

### 1.1 用户登录
**接口描述**: 用户账号密码登录

**请求**
```
POST /api/v1/auth/login
```

**请求体**
```json
{
  "username": "admin",
  "password": "admin123",
  "captchaCode": "1234",
  "captchaKey": "uuid"
}
```

**响应**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "userInfo": {
      "userId": 1,
      "username": "admin",
      "realName": "系统管理员",
      "avatar": "http://xxx.com/avatar.jpg",
      "roleId": 1,
      "roleCode": "ADMIN",
      "roleName": "系统管理员",
      "permissions": ["*:*:*"]
    }
  }
}
```

### 1.2 刷新Token
**接口描述**: 刷新访问令牌

**请求**
```
POST /api/v1/auth/refresh
```

**请求体**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**响应**
```json
{
  "code": 200,
  "data": {
    "token": "new_token",
    "expiresIn": 7200
  }
}
```

### 1.3 用户登出
**接口描述**: 退出登录

**请求**
```
POST /api/v1/auth/logout
```

**响应**
```json
{
  "code": 200,
  "message": "退出成功"
}
```

### 1.4 获取当前用户信息
**接口描述**: 获取当前登录用户详细信息

**请求**
```
GET /api/v1/auth/userinfo
```

**响应**
```json
{
  "code": 200,
  "data": {
    "userId": 1,
    "username": "admin",
    "realName": "系统管理员",
    "phone": "13800138000",
    "email": "admin@example.com",
    "avatar": "http://xxx.com/avatar.jpg",
    "roleId": 1,
    "roleCode": "ADMIN",
    "roleName": "系统管理员",
    "deptId": 1,
    "deptName": "运维部",
    "permissions": ["system:user:list", "inspection:task:list"],
    "lastLoginTime": "2024-01-30 10:00:00"
  }
}
```

### 1.5 修改密码
**接口描述**: 用户修改自己的密码

**请求**
```
PUT /api/v1/auth/password
```

**请求体**
```json
{
  "oldPassword": "oldPass123",
  "newPassword": "newPass123"
}
```

**响应**
```json
{
  "code": 200,
  "message": "密码修改成功"
}
```

---

## 2. 用户管理模块

### 2.1 用户列表
**接口描述**: 分页查询用户列表

**请求**
```
GET /api/v1/system/users
```

**查询参数**
```
pageNum: 1
pageSize: 10
username: (可选) 用户名模糊查询
realName: (可选) 真实姓名模糊查询
phone: (可选) 手机号模糊查询
roleId: (可选) 角色ID
status: (可选) 状态
```

**响应**
```json
{
  "code": 200,
  "data": {
    "total": 100,
    "pages": 10,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "userId": 1,
        "username": "admin",
        "realName": "系统管理员",
        "phone": "13800138000",
        "email": "admin@example.com",
        "avatar": "http://xxx.com/avatar.jpg",
        "roleId": 1,
        "roleCode": "ADMIN",
        "roleName": "系统管理员",
        "deptId": 1,
        "deptName": "运维部",
        "status": 1,
        "createTime": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

### 2.2 创建用户
**接口描述**: 创建新用户

**请求**
```
POST /api/v1/system/users
```

**请求体**
```json
{
  "username": "testuser",
  "password": "test123",
  "realName": "测试用户",
  "phone": "13900139000",
  "email": "test@example.com",
  "roleId": 2,
  "deptId": 1,
  "remark": "测试"
}
```

**响应**
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "userId": 2
  }
}
```

### 2.3 更新用户
**接口描述**: 更新用户信息

**请求**
```
PUT /api/v1/system/users/{userId}
```

**请求体**
```json
{
  "realName": "测试用户2",
  "phone": "13900139001",
  "email": "test2@example.com",
  "roleId": 3,
  "status": 1,
  "remark": "更新"
}
```

**响应**
```json
{
  "code": 200,
  "message": "更新成功"
}
```

### 2.4 删除用户
**接口描述**: 删除用户(逻辑删除)

**请求**
```
DELETE /api/v1/system/users/{userId}
```

**响应**
```json
{
  "code": 200,
  "message": "删除成功"
}
```

### 2.5 重置密码
**接口描述**: 管理员重置用户密码

**请求**
```
PUT /api/v1/system/users/{userId}/password/reset
```

**请求体**
```json
{
  "newPassword": "123456"
}
```

**响应**
```json
{
  "code": 200,
  "message": "密码重置成功"
}
```

---

## 3. 变电站管理模块

### 3.1 变电站列表
**接口描述**: 分页查询变电站列表

**请求**
```
GET /api/v1/substation/list
```

**查询参数**
```
pageNum: 1
pageSize: 10
stationCode: (可选) 变电站编码
stationName: (可选) 变电站名称模糊查询
stationType: (可选) 变电站类型
status: (可选) 状态
```

**响应**
```json
{
  "code": 200,
  "data": {
    "total": 50,
    "pages": 5,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "stationId": 1,
        "stationCode": "BD001",
        "stationName": "某某220kV变电站",
        "stationType": "220kV",
        "address": "某某市某某区某某路",
        "longitude": 116.404269,
        "latitude": 39.915168,
        "capacity": "2×150MVA",
        "voltageLevel": "220kV/110kV/10kV",
        "operator": "某某电力公司",
        "responsiblePerson": "张三",
        "responsiblePhone": "13800138000",
        "status": 1,
        "deviceCount": 120,
        "createTime": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

### 3.2 创建变电站
**接口描述**: 创建新变电站

**请求**
```
POST /api/v1/substation
```

**请求体**
```json
{
  "stationCode": "BD001",
  "stationName": "某某220kV变电站",
  "stationType": "220kV",
  "address": "某某市某某区某某路",
  "longitude": 116.404269,
  "latitude": 39.915168,
  "capacity": "2×150MVA",
  "voltageLevel": "220kV/110kV/10kV",
  "operator": "某某电力公司",
  "responsiblePerson": "张三",
  "responsiblePhone": "13800138000",
  "status": 1,
  "remark": "备注"
}
```

**响应**
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "stationId": 1
  }
}
```

### 3.3 更新变电站
**接口描述**: 更新变电站信息

**请求**
```
PUT /api/v1/substation/{stationId}
```

**请求体**
```json
{
  "stationName": "某某220kV变电站(更新)",
  "address": "新地址",
  "status": 1
}
```

**响应**
```json
{
  "code": 200,
  "message": "更新成功"
}
```

### 3.4 删除变电站
**接口描述**: 删除变电站

**请求**
```
DELETE /api/v1/substation/{stationId}
```

**响应**
```json
{
  "code": 200,
  "message": "删除成功"
}
```

### 3.5 变电站详情
**接口描述**: 获取变电站详细信息

**请求**
```
GET /api/v1/substation/{stationId}
```

**响应**
```json
{
  "code": 200,
  "data": {
    "stationId": 1,
    "stationCode": "BD001",
    "stationName": "某某220kV变电站",
    "stationType": "220kV",
    "address": "某某市某某区某某路",
    "longitude": 116.404269,
    "latitude": 39.915168,
    "capacity": "2×150MVA",
    "voltageLevel": "220kV/110kV/10kV",
    "operator": "某某电力公司",
    "responsiblePerson": "张三",
    "responsiblePhone": "13800138000",
    "status": 1,
    "createTime": "2024-01-01 10:00:00",
    "updateTime": "2024-01-30 10:00:00",
    "remark": "备注",
    "deviceCount": 120,
    "normalDeviceCount": 115,
    "abnormalDeviceCount": 5
  }
}
```

---

## 4. 设备管理模块

### 4.1 设备列表
**接口描述**: 分页查询设备列表

**请求**
```
GET /api/v1/device/list
```

**查询参数**
```
pageNum: 1
pageSize: 10
deviceCode: (可选) 设备编码
deviceName: (可选) 设备名称模糊查询
categoryId: (可选) 设备分类ID
stationId: (可选) 变电站ID
status: (可选) 状态
```

**响应**
```json
{
  "code": 200,
  "data": {
    "total": 500,
    "pages": 50,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "deviceId": 1,
        "deviceCode": "SB001",
        "deviceName": "1#主变压器",
        "deviceModel": "SFZ11-150000/220",
        "manufacturer": "某某电气",
        "categoryId": 3,
        "categoryName": "变压器",
        "stationId": 1,
        "stationName": "某某220kV变电站",
        "location": "主变压器室",
        "qrCode": "QR202401300001",
        "qrCodeUrl": "http://xxx.com/qrcode/xxx.png",
        "ratedVoltage": "220kV",
        "ratedCurrent": "393A",
        "ratedCapacity": "150MVA",
        "commissioningDate": "2020-01-01",
        "warrantyExpireDate": "2030-01-01",
        "status": 1,
        "version": 1,
        "lastSyncTime": "2024-01-30 10:00:00",
        "createTime": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

### 4.2 创建设备
**接口描述**: 创建新设备

**请求**
```
POST /api/v1/device
```

**请求体**
```json
{
  "deviceCode": "SB001",
  "deviceName": "1#主变压器",
  "deviceModel": "SFZ11-150000/220",
  "manufacturer": "某某电气",
  "categoryId": 3,
  "stationId": 1,
  "location": "主变压器室",
  "qrCode": "QR202401300001",
  "ratedVoltage": "220kV",
  "ratedCurrent": "393A",
  "ratedCapacity": "150MVA",
  "commissioningDate": "2020-01-01",
  "warrantyExpireDate": "2030-01-01",
  "status": 1,
  "remark": "备注"
}
```

**响应**
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "deviceId": 1,
    "qrCodeUrl": "http://xxx.com/qrcode/xxx.png"
  }
}
```

### 4.3 更新设备
**接口描述**: 更新设备信息

**请求**
```
PUT /api/v1/device/{deviceId}
```

**请求体**
```json
{
  "deviceName": "1#主变压器(更新)",
  "location": "新位置",
  "status": 1
}
```

**响应**
```json
{
  "code": 200,
  "message": "更新成功"
}
```

### 4.4 删除设备
**接口描述**: 删除设备

**请求**
```
DELETE /api/v1/device/{deviceId}
```

**响应**
```json
{
  "code": 200,
  "message": "删除成功"
}
```

### 4.5 设备详情
**接口描述**: 获取设备详细信息

**请求**
```
GET /api/v1/device/{deviceId}
```

**响应**
```json
{
  "code": 200,
  "data": {
    "deviceId": 1,
    "deviceCode": "SB001",
    "deviceName": "1#主变压器",
    "deviceModel": "SFZ11-150000/220",
    "manufacturer": "某某电气",
    "categoryId": 3,
    "categoryName": "变压器",
    "stationId": 1,
    "stationName": "某某220kV变电站",
    "location": "主变压器室",
    "qrCode": "QR202401300001",
    "qrCodeUrl": "http://xxx.com/qrcode/xxx.png",
    "ratedVoltage": "220kV",
    "ratedCurrent": "393A",
    "ratedCapacity": "150MVA",
    "commissioningDate": "2020-01-01",
    "warrantyExpireDate": "2030-01-01",
    "status": 1,
    "version": 1,
    "createTime": "2024-01-01 10:00:00",
    "updateTime": "2024-01-30 10:00:00",
    "remark": "备注",
    "recentRecords": [
      {
        "recordId": 1,
        "checkTime": "2024-01-30 10:00:00",
        "checkResult": "normal",
        "inspectorName": "张三"
      }
    ],
    "recentDefects": [
      {
        "defectId": 1,
        "defectType": "general",
        "defectDescription": "表面有轻微油污",
        "discoverTime": "2024-01-25 10:00:00",
        "status": "pending"
      }
    ]
  }
}
```

### 4.6 批量导入设备
**接口描述**: Excel批量导入设备

**请求**
```
POST /api/v1/device/import
Content-Type: multipart/form-data
```

**表单参数**
```
file: Excel文件
stationId: 所属变电站ID
```

**响应**
```json
{
  "code": 200,
  "message": "导入成功",
  "data": {
    "successCount": 95,
    "failCount": 5,
    "failList": [
      {
        "row": 3,
        "reason": "设备编码已存在"
      }
    ]
  }
}
```

### 4.7 批量生成二维码
**接口描述**: 为设备批量生成二维码

**请求**
```
POST /api/v1/device/qrcode/batch
```

**请求体**
```json
{
  "deviceIds": [1, 2, 3, 4, 5]
}
```

**响应**
```json
{
  "code": 200,
  "message": "生成成功",
  "data": {
    "downloadUrl": "http://xxx.com/download/qrcode_batch_20240130.zip"
  }
}
```

### 4.8 扫码查询设备
**接口描述**: 扫描二维码查询设备信息

**请求**
```
GET /api/v1/device/qrcode/{qrCode}
```

**响应**
```json
{
  "code": 200,
  "data": {
    "deviceId": 1,
    "deviceCode": "SB001",
    "deviceName": "1#主变压器",
    "deviceModel": "SFZ11-150000/220",
    "location": "主变压器室",
    "stationName": "某某220kV变电站",
    "status": 1,
    "recentInspectionTime": "2024-01-28 10:00:00",
    "recentDefectCount": 1
  }
}
```

### 4.9 设备分类树
**接口描述**: 获取设备分类树形结构

**请求**
```
GET /api/v1/device/category/tree
```

**响应**
```json
{
  "code": 200,
  "data": [
    {
      "categoryId": 1,
      "categoryCode": "PRIMARY",
      "categoryName": "一次设备",
      "children": [
        {
          "categoryId": 3,
          "categoryCode": "PRIMARY_TRANSFORMER",
          "categoryName": "变压器",
          "children": []
        },
        {
          "categoryId": 4,
          "categoryCode": "PRIMARY_SWITCH",
          "categoryName": "开关设备",
          "children": []
        }
      ]
    },
    {
      "categoryId": 2,
      "categoryCode": "SECONDARY",
      "categoryName": "二次设备",
      "children": [
        {
          "categoryId": 6,
          "categoryCode": "SECONDARY_PROTECTION",
          "categoryName": "继电保护",
          "children": []
        }
      ]
    }
  ]
}
```

---

## 5. 巡检模板管理模块

### 5.1 模板列表
**接口描述**: 分页查询巡检模板列表

**请求**
```
GET /api/v1/inspection/template/list
```

**查询参数**
```
pageNum: 1
pageSize: 10
templateCode: (可选) 模板编码
templateName: (可选) 模板名称模糊查询
categoryId: (可选) 适用设备分类
inspectionType: (可选) 巡检类型
status: (可选) 状态
```

**响应**
```json
{
  "code": 200,
  "data": {
    "total": 20,
    "pages": 2,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "templateId": 1,
        "templateCode": "TPL001",
        "templateName": "变压器日巡模板",
        "categoryId": 3,
        "categoryName": "变压器",
        "inspectionType": "daily",
        "cycleDays": 1,
        "description": "变压器日常巡检模板",
        "itemCount": 15,
        "status": 1,
        "version": 1,
        "createTime": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

### 5.2 创建模板
**接口描述**: 创建新巡检模板

**请求**
```
POST /api/v1/inspection/template
```

**请求体**
```json
{
  "templateCode": "TPL001",
  "templateName": "变压器日巡模板",
  "categoryId": 3,
  "inspectionType": "daily",
  "cycleDays": 1,
  "description": "变压器日常巡检模板",
  "items": [
    {
      "itemCode": "ITEM001",
      "itemName": "油温检查",
      "itemType": "number",
      "checkMethod": "查看温度计读数",
      "acceptanceCriteria": "≤85℃",
      "defaultValue": "",
      "unit": "℃",
      "isRequired": 1,
      "sortOrder": 1
    },
    {
      "itemCode": "ITEM002",
      "itemName": "有无异响",
      "itemType": "select",
      "options": ["正常", "异常"],
      "isRequired": 1,
      "sortOrder": 2
    },
    {
      "itemCode": "ITEM003",
      "itemName": "外观照片",
      "itemType": "photo",
      "isRequired": 1,
      "sortOrder": 3
    }
  ]
}
```

**响应**
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "templateId": 1
  }
}
```

### 5.3 更新模板
**接口描述**: 更新巡检模板

**请求**
```
PUT /api/v1/inspection/template/{templateId}
```

**请求体**
```json
{
  "templateName": "变压器日巡模板(更新)",
  "inspectionType": "daily",
  "description": "更新描述",
  "items": [...]
}
```

**响应**
```json
{
  "code": 200,
  "message": "更新成功"
}
```

### 5.4 删除模板
**接口描述**: 删除巡检模板

**请求**
```
DELETE /api/v1/inspection/template/{templateId}
```

**响应**
```json
{
  "code": 200,
  "message": "删除成功"
}
```

### 5.5 模板详情
**接口描述**: 获取模板详细信息及检查项

**请求**
```
GET /api/v1/inspection/template/{templateId}
```

**响应**
```json
{
  "code": 200,
  "data": {
    "templateId": 1,
    "templateCode": "TPL001",
    "templateName": "变压器日巡模板",
    "categoryId": 3,
    "categoryName": "变压器",
    "inspectionType": "daily",
    "cycleDays": 1,
    "description": "变压器日常巡检模板",
    "status": 1,
    "version": 1,
    "items": [
      {
        "itemId": 1,
        "itemCode": "ITEM001",
        "itemName": "油温检查",
        "itemType": "number",
        "checkMethod": "查看温度计读数",
        "acceptanceCriteria": "≤85℃",
        "unit": "℃",
        "isRequired": 1,
        "sortOrder": 1
      }
    ]
  }
}
```

---

## 6. 巡检计划管理模块

### 6.1 计划列表
**接口描述**: 分页查询巡检计划列表

**请求**
```
GET /api/v1/inspection/plan/list
```

**查询参数**
```
pageNum: 1
pageSize: 10
planCode: (可选) 计划编码
planName: (可选) 计划名称模糊查询
stationId: (可选) 变电站ID
assigneeId: (可选) 执行人ID
status: (可选) 状态
```

**响应**
```json
{
  "code": 200,
  "data": {
    "total": 30,
    "pages": 3,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "planId": 1,
        "planCode": "PLAN001",
        "planName": "1#变电站日巡计划",
        "stationId": 1,
        "stationName": "某某220kV变电站",
        "templateId": 1,
        "templateName": "变压器日巡模板",
        "inspectionType": "daily",
        "cycleType": "daily",
        "assigneeId": 3,
        "assigneeName": "张三",
        "reviewerId": 2,
        "reviewerName": "李班长",
        "startDate": "2024-01-01",
        "endDate": "2024-12-31",
        "status": "active",
        "lastExecuteTime": "2024-01-30 08:00:00",
        "nextExecuteTime": "2024-01-31 08:00:00",
        "createTime": "2024-01-01 10:00:00"
      }
    ]
  }
}
```

### 6.2 创建计划
**接口描述**: 创建巡检计划

**请求**
```
POST /api/v1/inspection/plan
```

**请求体**
```json
{
  "planName": "1#变电站日巡计划",
  "stationId": 1,
  "templateId": 1,
  "inspectionType": "daily",
  "cycleType": "daily",
  "cycleValue": 1,
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "assigneeId": 3,
  "reviewerId": 2,
  "deviceIds": [1, 2, 3],
  "remark": "备注"
}
```

**响应**
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "planId": 1
  }
}
```

### 6.3 更新计划
**接口描述**: 更新巡检计划

**请求**
```
PUT /api/v1/inspection/plan/{planId}
```

**请求体**
```json
{
  "planName": "1#变电站日巡计划(更新)",
  "assigneeId": 4,
  "status": "active"
}
```

**响应**
```json
{
  "code": 200,
  "message": "更新成功"
}
```

### 6.4 删除计划
**接口描述**: 删除巡检计划

**请求**
```
DELETE /api/v1/inspection/plan/{planId}
```

**响应**
```json
{
  "code": 200,
  "message": "删除成功"
}
```

### 6.5 暂停/恢复计划
**接口描述**: 暂停或恢复计划执行

**请求**
```
PUT /api/v1/inspection/plan/{planId}/status
```

**请求体**
```json
{
  "status": "paused"
}
```

**响应**
```json
{
  "code": 200,
  "message": "操作成功"
}
```

---

## 7. 巡检任务管理模块

### 7.1 任务列表
**接口描述**: 分页查询巡检任务列表

**请求**
```
GET /api/v1/inspection/task/list
```

**查询参数**
```
pageNum: 1
pageSize: 10
taskCode: (可选) 任务编码
stationId: (可选) 变电站ID
assigneeId: (可选) 执行人ID
status: (可选) 状态
inspectionType: (可选) 巡检类型
startDateBegin: (可选) 计划开始时间-起
startDateEnd: (可选) 计划开始时间-止
```

**响应**
```json
{
  "code": 200,
  "data": {
    "total": 100,
    "pages": 10,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "taskId": 1,
        "taskCode": "TASK20240130001",
        "planId": 1,
        "stationId": 1,
        "stationName": "某某220kV变电站",
        "templateId": 1,
        "templateName": "变压器日巡模板",
        "inspectionType": "daily",
        "title": "2024-01-30日巡检任务",
        "assigneeId": 3,
        "assigneeName": "张三",
        "plannedStartTime": "2024-01-30 08:00:00",
        "plannedEndTime": "2024-01-30 18:00:00",
        "actualStartTime": "2024-01-30 08:30:00",
        "status": "in_progress",
        "progress": 60,
        "deviceCount": 10,
        "completedCount": 6,
        "defectCount": 1,
        "version": 1,
        "createTime": "2024-01-30 07:00:00"
      }
    ]
  }
}
```

### 7.2 任务详情
**接口描述**: 获取任务详细信息

**请求**
```
GET /api/v1/inspection/task/{taskId}
```

**响应**
```json
{
  "code": 200,
  "data": {
    "taskId": 1,
    "taskCode": "TASK20240130001",
    "planId": 1,
    "stationId": 1,
    "stationName": "某某220kV变电站",
    "templateId": 1,
    "templateName": "变压器日巡模板",
    "inspectionType": "daily",
    "title": "2024-01-30日巡检任务",
    "description": "检查所有变压器设备",
    "assigneeId": 3,
    "assigneeName": "张三",
    "plannedStartTime": "2024-01-30 08:00:00",
    "plannedEndTime": "2024-01-30 18:00:00",
    "actualStartTime": "2024-01-30 08:30:00",
    "status": "in_progress",
    "progress": 60,
    "deviceCount": 10,
    "completedCount": 6,
    "defectCount": 1,
    "devices": [
      {
        "deviceId": 1,
        "deviceCode": "SB001",
        "deviceName": "1#主变压器",
        "location": "主变压器室",
        "status": "completed",
        "sortOrder": 1
      }
    ],
    "createTime": "2024-01-30 07:00:00"
  }
}
```

### 7.3 创建任务
**接口描述**: 手动创建巡检任务

**请求**
```
POST /api/v1/inspection/task
```

**请求体**
```json
{
  "stationId": 1,
  "templateId": 1,
  "inspectionType": "special",
  "title": "专项巡检任务",
  "description": "雨后专项检查",
  "assigneeId": 3,
  "plannedStartTime": "2024-01-30 14:00:00",
  "plannedEndTime": "2024-01-30 18:00:00",
  "deviceIds": [1, 2, 3],
  "remark": "备注"
}
```

**响应**
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "taskId": 2,
    "taskCode": "TASK20240130002"
  }
}
```

### 7.4 开始任务
**接口描述**: 开始执行任务

**请求**
```
POST /api/v1/inspection/task/{taskId}/start
```

**响应**
```json
{
  "code": 200,
  "message": "任务已开始"
}
```

### 7.5 完成任务
**接口描述**: 完成任务

**请求**
```
POST /api/v1/inspection/task/{taskId}/complete
```

**请求体**
```json
{
  "summary": "任务完成总结",
  "description": "所有设备检查完成"
}
```

**响应**
```json
{
  "code": 200,
  "message": "任务已完成"
}
```

### 7.6 取消任务
**接口描述**: 取消任务

**请求**
```
POST /api/v1/inspection/task/{taskId}/cancel
```

**请求体**
```json
{
  "reason": "天气原因取消"
}
```

**响应**
```json
{
  "code": 200,
  "message": "任务已取消"
}
```

### 7.7 我的任务列表(H5专用)
**接口描述**: 获取当前用户的任务列表

**请求**
```
GET /api/v1/inspection/task/my
```

**查询参数**
```
status: (可选) 状态筛选
```

**响应**
```json
{
  "code": 200,
  "data": {
    "pending": [
      {
        "taskId": 2,
        "taskCode": "TASK20240130002",
        "title": "专项巡检任务",
        "stationName": "某某220kV变电站",
        "plannedStartTime": "2024-01-30 14:00:00",
        "deviceCount": 3
      }
    ],
    "inProgress": [
      {
        "taskId": 1,
        "taskCode": "TASK20240130001",
        "title": "2024-01-30日巡检任务",
        "stationName": "某某220kV变电站",
        "progress": 60,
        "deviceCount": 10,
        "completedCount": 6
      }
    ],
    "completed": []
  }
}
```

### 7.8 获取任务待检查设备列表
**接口描述**: 获取任务下的设备列表(H5扫码后用)

**请求**
```
GET /api/v1/inspection/task/{taskId}/devices
```

**响应**
```json
{
  "code": 200,
  "data": [
    {
      "deviceId": 1,
      "deviceCode": "SB001",
      "deviceName": "1#主变压器",
      "location": "主变压器室",
      "status": "pending",
      "sortOrder": 1
    }
  ]
}
```

---

## 8. 巡检记录模块

### 8.1 提交巡检记录
**接口描述**: 提交单个设备的巡检记录

**请求**
```
POST /api/v1/inspection/record
```

**请求体**
```json
{
  "taskId": 1,
  "deviceId": 1,
  "records": [
    {
      "itemId": 1,
      "itemType": "number",
      "checkValue": "65",
      "checkResult": "normal",
      "description": ""
    },
    {
      "itemId": 2,
      "itemType": "select",
      "checkValue": "正常",
      "checkResult": "normal",
      "description": ""
    },
    {
      "itemId": 3,
      "itemType": "photo",
      "photoUrls": ["http://xxx.com/photo1.jpg", "http://xxx.com/photo2.jpg"],
      "checkResult": "normal",
      "description": "外观正常"
    }
  ],
  "isOffline": 0
}
```

**响应**
```json
{
  "code": 200,
  "message": "提交成功",
  "data": {
    "recordIds": [1, 2, 3]
  }
}
```

### 8.2 批量提交巡检记录
**接口描述**: 批量提交多个设备的巡检记录

**请求**
```
POST /api/v1/inspection/record/batch
```

**请求体**
```json
{
  "records": [
    {
      "taskId": 1,
      "deviceId": 1,
      "items": [...]
    },
    {
      "taskId": 1,
      "deviceId": 2,
      "items": [...]
    }
  ]
}
```

**响应**
```json
{
  "code": 200,
  "message": "批量提交成功",
  "data": {
    "successCount": 2,
    "failCount": 0
  }
}
```

### 8.3 查询设备巡检记录
**接口描述**: 查询某个设备的巡检记录列表

**请求**
```
GET /api/v1/inspection/record/device/{deviceId}
```

**查询参数**
```
pageNum: 1
pageSize: 10
taskId: (可选) 任务ID
```

**响应**
```json
{
  "code": 200,
  "data": {
    "total": 50,
    "list": [
      {
        "recordId": 1,
        "recordCode": "REC20240130001",
        "taskId": 1,
        "taskCode": "TASK20240130001",
        "deviceId": 1,
        "deviceName": "1#主变压器",
        "checkResult": "normal",
        "inspectorId": 3,
        "inspectorName": "张三",
        "checkTime": "2024-01-30 10:00:00",
        "itemCount": 15,
        "abnormalCount": 0,
        "photoCount": 3
      }
    ]
  }
}
```

### 8.4 巡检记录详情
**接口描述**: 获取巡检记录详细信息

**请求**
```
GET /api/v1/inspection/record/{recordId}
```

**响应**
```json
{
  "code": 200,
  "data": {
    "recordId": 1,
    "recordCode": "REC20240130001",
    "taskId": 1,
    "deviceId": 1,
    "deviceCode": "SB001",
    "deviceName": "1#主变压器",
    "itemId": 1,
    "itemName": "油温检查",
    "itemType": "number",
    "checkValue": "65",
    "checkResult": "normal",
    "photoUrls": ["http://xxx.com/photo1.jpg"],
    "description": "温度正常",
    "inspectorId": 3,
    "inspectorName": "张三",
    "checkTime": "2024-01-30 10:00:00",
    "createTime": "2024-01-30 10:00:00"
  }
}
```

---

## 9. 缺陷管理模块

### 9.1 缺陷列表
**接口描述**: 分页查询缺陷列表

**请求**
```
GET /api/v1/defect/list
```

**查询参数**
```
pageNum: 1
pageSize: 10
defectCode: (可选) 缺陷编号
stationId: (可选) 变电站ID
deviceId: (可选) 设备ID
defectType: (可选) 缺陷类型
defectLevel: (可选) 缺陷等级
status: (可选) 状态
discoverTimeBegin: (可选) 发现时间-起
discoverTimeEnd: (可选) 发现时间-止
```

**响应**
```json
{
  "code": 200,
  "data": {
    "total": 80,
    "pages": 8,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "defectId": 1,
        "defectCode": "D20240130001",
        "stationId": 1,
        "stationName": "某某220kV变电站",
        "deviceId": 1,
        "deviceCode": "SB001",
        "deviceName": "1#主变压器",
        "defectType": "serious",
        "defectLevel": "B",
        "defectDescription": "油位过低,低于最低刻度线",
        "discovererId": 3,
        "discovererName": "张三",
        "discoverTime": "2024-01-30 10:00:00",
        "status": "pending",
        "photoUrls": ["http://xxx.com/photo1.jpg"],
        "createTime": "2024-01-30 10:00:00"
      }
    ]
  }
}
```

### 9.2 创建缺陷
**接口描述**: 创建缺陷记录

**请求**
```
POST /api/v1/defect
```

**请求体**
```json
{
  "stationId": 1,
  "deviceId": 1,
  "defectType": "serious",
  "defectLevel": "B",
  "defectDescription": "油位过低,低于最低刻度线",
  "photoUrls": ["http://xxx.com/photo1.jpg"],
  "recordId": 1
}
```

**响应**
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "defectId": 1,
    "defectCode": "D20240130001"
  }
}
```

### 9.3 缺陷详情
**接口描述**: 获取缺陷详细信息

**请求**
```
GET /api/v1/defect/{defectId}
```

**响应**
```json
{
  "code": 200,
  "data": {
    "defectId": 1,
    "defectCode": "D20240130001",
    "stationId": 1,
    "stationName": "某某220kV变电站",
    "deviceId": 1,
    "deviceCode": "SB001",
    "deviceName": "1#主变压器",
    "recordId": 1,
    "defectType": "serious",
    "defectLevel": "B",
    "defectDescription": "油位过低,低于最低刻度线",
    "photoUrls": ["http://xxx.com/photo1.jpg"],
    "discovererId": 3,
    "discovererName": "张三",
    "discoverTime": "2024-01-30 10:00:00",
    "status": "pending",
    "confirmedBy": null,
    "confirmedTime": null,
    "repairPlan": null,
    "repairPerson": null,
    "repairDeadline": null,
    "createTime": "2024-01-30 10:00:00"
  }
}
```

### 9.4 确认缺陷
**接口描述**: 班长确认缺陷

**请求**
```
PUT /api/v1/defect/{defectId}/confirm
```

**请求体**
```json
{
  "repairPlan": "安排补油处理",
  "repairPerson": "李四",
  "repairDeadline": "2024-02-02"
}
```

**响应**
```json
{
  "code": 200,
  "message": "确认成功"
}
```

### 9.5 更新缺陷处理状态
**接口描述**: 更新缺陷处理状态

**请求**
```
PUT /api/v1/defect/{defectId}/status
```

**请求体**
```json
{
  "status": "repairing",
  "repairDescription": "开始处理",
  "repairPhotos": ["http://xxx.com/repair1.jpg"]
}
```

**响应**
```json
{
  "code": 200,
  "message": "更新成功"
}
```

### 9.6 完成缺陷处理
**接口描述**: 完成缺陷处理

**请求**
```
PUT /api/v1/defect/{defectId}/complete
```

**请求体**
```json
{
  "repairDescription": "已完成补油,油位恢复正常",
  "repairPhotos": ["http://xxx.com/repair_after1.jpg"]
}
```

**响应**
```json
{
  "code": 200,
  "message": "处理完成"
}
```

### 9.7 缺陷统计
**接口描述**: 获取缺陷统计数据

**请求**
```
GET /api/v1/defect/statistics
```

**查询参数**
```
stationId: (可选) 变电站ID
startDate: (可选) 开始日期
endDate: (可选) 结束日期
```

**响应**
```json
{
  "code": 200,
  "data": {
    "totalCount": 100,
    "criticalCount": 5,
    "seriousCount": 20,
    "generalCount": 75,
    "pendingCount": 30,
    "confirmedCount": 40,
    "repairingCount": 20,
    "completedCount": 10,
    "trend": [
      {
        "date": "2024-01-24",
        "count": 10
      },
      {
        "date": "2024-01-25",
        "count": 15
      }
    ]
  }
}
```

---

## 10. 巡检报告模块

### 10.1 报告列表
**接口描述**: 分页查询报告列表

**请求**
```
GET /api/v1/inspection/report/list
```

**查询参数**
```
pageNum: 1
pageSize: 10
reportCode: (可选) 报告编号
reportType: (可选) 报告类型
stationId: (可选) 变电站ID
status: (可选) 状态
startTimeBegin: (可选) 开始时间-起
startTimeEnd: (可选) 开始时间-止
```

**响应**
```json
{
  "code": 200,
  "data": {
    "total": 50,
    "pages": 5,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "reportId": 1,
        "reportCode": "RPT20240130001",
        "reportType": "daily",
        "taskId": 1,
        "taskCode": "TASK20240130001",
        "stationId": 1,
        "stationName": "某某220kV变电站",
        "reportPeriod": "2024-01-30",
        "startTime": "2024-01-30 08:00:00",
        "endTime": "2024-01-30 18:00:00",
        "deviceCount": 50,
        "normalCount": 48,
        "abnormalCount": 2,
        "defectCount": 1,
        "creatorId": 3,
        "creatorName": "张三",
        "status": "submitted",
        "submitTime": "2024-01-30 18:30:00",
        "createTime": "2024-01-30 18:00:00"
      }
    ]
  }
}
```

### 10.2 生成报告
**接口描述**: 根据任务生成报告

**请求**
```
POST /api/v1/inspection/report
```

**请求体**
```json
{
  "taskId": 1,
  "reportType": "daily",
  "summary": "今日巡检完成,发现1处缺陷",
  "statistics": {
    "deviceCount": 50,
    "normalCount": 48,
    "abnormalCount": 2,
    "defectCount": 1
  }
}
```

**响应**
```json
{
  "code": 200,
  "message": "生成成功",
  "data": {
    "reportId": 1,
    "reportCode": "RPT20240130001"
  }
}
```

### 10.3 报告详情
**接口描述**: 获取报告详细信息

**请求**
```
GET /api/v1/inspection/report/{reportId}
```

**响应**
```json
{
  "code": 200,
  "data": {
    "reportId": 1,
    "reportCode": "RPT20240130001",
    "reportType": "daily",
    "taskId": 1,
    "taskCode": "TASK20240130001",
    "stationId": 1,
    "stationName": "某某220kV变电站",
    "reportPeriod": "2024-01-30",
    "startTime": "2024-01-30 08:00:00",
    "endTime": "2024-01-30 18:00:00",
    "summary": "今日巡检完成,发现1处缺陷",
    "statistics": {
      "deviceCount": 50,
      "normalCount": 48,
      "abnormalCount": 2,
      "defectCount": 1,
      "byCategory": {
        "变压器": 10,
        "开关设备": 20
      },
      "byResult": {
        "normal": 48,
        "abnormal": 2
      }
    },
    "deviceCount": 50,
    "normalCount": 48,
    "abnormalCount": 2,
    "defectCount": 1,
    "creatorId": 3,
    "creatorName": "张三",
    "status": "submitted",
    "submitTime": "2024-01-30 18:30:00",
    "approverId": null,
    "approverName": null,
    "approveTime": null,
    "approveComment": null,
    "fileUrl": null,
    "createTime": "2024-01-30 18:00:00",
    "defects": [
      {
        "defectCode": "D20240130001",
        "deviceName": "1#主变压器",
        "defectDescription": "油位过低"
      }
    ]
  }
}
```

### 10.4 提交报告
**接口描述**: 提交报告审核

**请求**
```
POST /api/v1/inspection/report/{reportId}/submit
```

**响应**
```json
{
  "code": 200,
  "message": "提交成功"
}
```

### 10.5 审核报告
**接口描述**: 班长审核报告

**请求**
```
POST /api/v1/inspection/report/{reportId}/approve
```

**请求体**
```json
{
  "action": "approve",
  "comment": "审核通过"
}
```

**响应**
```json
{
  "code": 200,
  "message": "审核完成"
}
```

### 10.6 导出报告
**接口描述**: 导出报告为PDF/Word

**请求**
```
GET /api/v1/inspection/report/{reportId}/export
```

**查询参数**
```
format: pdf | docx
```

**响应**
```json
{
  "code": 200,
  "data": {
    "downloadUrl": "http://xxx.com/download/report_20240130.pdf"
  }
}
```

---

## 11. 文件上传模块

### 11.1 上传图片
**接口描述**: 上传图片文件

**请求**
```
POST /api/v1/file/upload/image
Content-Type: multipart/form-data
```

**表单参数**
```
file: 图片文件
businessType: 业务类型
businessId: 业务ID(可选)
```

**响应**
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "fileId": 1,
    "fileCode": "FILE20240130001",
    "fileName": "photo.jpg",
    "fileType": "image",
    "fileExtension": "jpg",
    "fileSize": 1024000,
    "fileUrl": "http://xxx.com/files/2024/01/30/xxx.jpg",
    "thumbnailUrl": "http://xxx.com/files/2024/01/30/thumb_xxx.jpg"
  }
}
```

### 11.2 批量上传图片
**接口描述**: 批量上传图片文件

**请求**
```
POST /api/v1/file/upload/images
Content-Type: multipart/form-data
```

**表单参数**
```
files: 图片文件数组
businessType: 业务类型
businessId: 业务ID(可选)
```

**响应**
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "successCount": 3,
    "failCount": 0,
    "files": [
      {
        "fileId": 1,
        "fileUrl": "http://xxx.com/files/photo1.jpg"
      }
    ]
  }
}
```

### 11.3 上传文档
**接口描述**: 上传文档文件

**请求**
```
POST /api/v1/file/upload/document
Content-Type: multipart/form-data
```

**表单参数**
```
file: 文档文件
businessType: 业务类型
businessId: 业务ID(可选)
```

**响应**
```json
{
  "code": 200,
  "data": {
    "fileId": 2,
    "fileUrl": "http://xxx.com/files/document.pdf"
  }
}
```

### 11.4 删除文件
**接口描述**: 删除文件

**请求**
```
DELETE /api/v1/file/{fileId}
```

**响应**
```json
{
  "code": 200,
  "message": "删除成功"
}
```

---

## 12. 离线数据同步模块(H5专用)

### 12.1 获取离线基础数据
**接口描述**: 获取离线需要的基础数据(设备、模板等)

**请求**
```
GET /api/v1/sync/download/base
```

**查询参数**
```
lastSyncTime: 最后同步时间(时间戳,可选)
```

**响应**
```json
{
  "code": 200,
  "data": {
    "version": 1706600000000,
    "devices": {
      "updated": [
        {
          "deviceId": 1,
          "deviceCode": "SB001",
          "deviceName": "1#主变压器",
          "version": 1,
          "lastSyncTime": "2024-01-30 10:00:00"
        }
      ],
      "deleted": [2, 3]
    },
    "templates": {
      "updated": [...],
      "deleted": []
    },
    "stations": {
      "updated": [...],
      "deleted": []
    },
    "categories": {
      "updated": [...],
      "deleted": []
    }
  }
}
```

### 12.2 获取离线任务数据
**接口描述**: 获取分配给当前用户的任务数据

**请求**
```
GET /api/v1/sync/download/tasks
```

**查询参数**
```
lastSyncTime: 最后同步时间(可选)
```

**响应**
```json
{
  "code": 200,
  "data": {
    "version": 1706600000000,
    "tasks": [
      {
        "taskId": 1,
        "taskCode": "TASK20240130001",
        "title": "日巡检任务",
        "stationId": 1,
        "stationName": "某某220kV变电站",
        "templateId": 1,
        "inspectionType": "daily",
        "plannedStartTime": "2024-01-30 08:00:00",
        "plannedEndTime": "2024-01-30 18:00:00",
        "devices": [
          {
            "deviceId": 1,
            "deviceCode": "SB001",
            "deviceName": "1#主变压器",
            "location": "主变压器室"
          }
        ],
        "templateItems": [
          {
            "itemId": 1,
            "itemName": "油温检查",
            "itemType": "number",
            "isRequired": 1
          }
        ],
        "version": 1
      }
    ]
  }
}
```

### 12.3 上传离线数据
**接口描述**: 上传离线创建/修改的数据

**请求**
```
POST /api/v1/sync/upload
```

**请求体**
```json
{
  "tasks": {
    "created": [
      {
        "taskCode": "OFFLINE_TASK001",
        "stationId": 1,
        "title": "离线创建的任务",
        "offlineCreateTime": "2024-01-30 10:00:00"
      }
    ],
    "updated": []
  },
  "records": {
    "created": [
      {
        "taskId": 1,
        "deviceId": 1,
        "itemId": 1,
        "checkValue": "65",
        "checkResult": "normal",
        "offlineCreateTime": "2024-01-30 10:00:00"
      }
    ],
    "updated": []
  },
  "defects": {
    "created": [...],
    "updated": []
  }
}
```

**响应**
```json
{
  "code": 200,
  "message": "同步成功",
  "data": {
    "successCount": 10,
    "failCount": 0,
    "results": {
      "tasks": {
        "success": [1, 2],
        "failed": []
      },
      "records": {
        "success": [3, 4, 5],
        "failed": []
      }
    }
  }
}
```

### 12.4 上传离线文件
**接口描述**: 上传离线期间缓存的文件

**请求**
```
POST /api/v1/sync/upload/files
Content-Type: multipart/form-data
```

**表单参数**
```
files: 文件数组
businessType: 业务类型
businessId: 业务ID
offlineCreateTime: 离线创建时间
```

**响应**
```json
{
  "code": 200,
  "data": {
    "successCount": 5,
    "files": [
      {
        "localId": "local_001",
        "fileId": 1,
        "fileUrl": "http://xxx.com/files/photo1.jpg"
      }
    ]
  }
}
```

### 12.5 获取同步日志
**接口描述**: 获取同步日志列表

**请求**
```
GET /api/v1/sync/logs
```

**查询参数**
```
pageNum: 1
pageSize: 10
syncType: (可选) upload/download
status: (可选) success/failed/partial
```

**响应**
```json
{
  "code": 200,
  "data": {
    "total": 20,
    "list": [
      {
        "logId": 1,
        "syncType": "upload",
        "dataType": "record",
        "dataCount": 10,
        "successCount": 10,
        "failCount": 0,
        "status": "success",
        "syncStartTime": "2024-01-30 10:00:00",
        "syncEndTime": "2024-01-30 10:00:05"
      }
    ]
  }
}
```

---

## 13. 统计分析模块

### 13.1 巡检统计数据
**接口描述**: 获取巡检统计数据

**请求**
```
GET /api/v1/statistics/inspection
```

**查询参数**
```
stationId: (可选) 变电站ID
startDate: (可选) 开始日期
endDate: (可选) 结束日期
```

**响应**
```json
{
  "code": 200,
  "data": {
    "totalTasks": 100,
    "completedTasks": 95,
    "pendingTasks": 3,
    "overdueTasks": 2,
    "totalDevices": 500,
    "inspectedDevices": 450,
    "completionRate": 90,
    "trend": [
      {
        "date": "2024-01-24",
        "taskCount": 10,
        "completionRate": 95
      }
    ]
  }
}
```

### 13.2 缺陷统计数据
**接口描述**: 获取缺陷统计数据

**请求**
```
GET /api/v1/statistics/defect
```

**查询参数**
```
stationId: (可选) 变电站ID
startDate: (可选) 开始日期
endDate: (可选) 结束日期
```

**响应**
```json
{
  "code": 200,
  "data": {
    "totalCount": 80,
    "criticalCount": 5,
    "seriousCount": 20,
    "generalCount": 55,
    "pendingCount": 30,
    "processingCount": 30,
    "completedCount": 20,
    "completionRate": 25,
    "topDevices": [
      {
        "deviceName": "1#主变压器",
        "defectCount": 10
      }
    ],
    "trend": [
      {
        "date": "2024-01-24",
        "count": 8
      }
    ]
  }
}
```

### 13.3 设备状态统计
**接口描述**: 获取设备状态统计

**请求**
```
GET /api/v1/statistics/device
```

**查询参数**
```
stationId: (可选) 变电站ID
```

**响应**
```json
{
  "code": 200,
  "data": {
    "totalCount": 500,
    "normalCount": 470,
    "faultCount": 15,
    "maintenanceCount": 10,
    "disabledCount": 5,
    "byCategory": [
      {
        "categoryName": "变压器",
        "totalCount": 50,
        "normalCount": 48,
        "faultCount": 2
      }
    ],
    "byStation": [
      {
        "stationName": "某某220kV变电站",
        "totalCount": 120,
        "normalCount": 115,
        "faultCount": 5
      }
    ]
  }
}
```

### 13.4 用户工作量统计
**接口描述**: 获取用户工作量统计

**请求**
```
GET /api/v1/statistics/user/workload
```

**查询参数**
```
userId: (可选) 用户ID
startDate: (可选) 开始日期
endDate: (可选) 结束日期
```

**响应**
```json
{
  "code": 200,
  "data": {
    "totalTasks": 50,
    "completedTasks": 48,
    "totalDevices": 500,
    "inspectedDevices": 480,
    "defectsDiscovered": 15,
    "workingHours": 200,
    "ranking": {
      "taskRank": 1,
      "deviceRank": 2,
      "defectRank": 3
    }
  }
}
```

---

## 14. 系统配置模块

### 14.1 获取系统配置
**接口描述**: 获取系统配置列表

**请求**
```
GET /api/v1/system/config/list
```

**查询参数**
```
groupName: (可选) 配置分组
```

**响应**
```json
{
  "code": 200,
  "data": [
    {
      "configId": 1,
      "configKey": "file.upload.maxSize",
      "configValue": "10485760",
      "configType": "number",
      "description": "文件上传最大大小(字节)",
      "groupName": "file"
    }
  ]
}
```

### 14.2 更新系统配置
**接口描述**: 更新系统配置

**请求**
```
PUT /api/v1/system/config
```

**请求体**
```json
{
  "configs": [
    {
      "configKey": "file.upload.maxSize",
      "configValue": "20971520"
    }
  ]
}
```

**响应**
```json
{
  "code": 200,
  "message": "更新成功"
}
```

---

## 15. 首页数据接口

### 15.1 获取首页概览数据
**接口描述**: 获取首页统计数据

**请求**
```
GET /api/v1/dashboard/overview
```

**响应**
```json
{
  "code": 200,
  "data": {
    "stationCount": 5,
    "deviceCount": 500,
    "taskCount": 100,
    "defectCount": 80,
    "todayTasks": 10,
    "pendingTasks": 3,
    "inProgressTasks": 5,
    "overdueTasks": 2,
    "pendingDefects": 30,
    "criticalDefects": 5,
    "recentTasks": [
      {
        "taskId": 1,
        "taskCode": "TASK20240130001",
        "title": "日巡检任务",
        "status": "in_progress",
        "assigneeName": "张三"
      }
    ],
    "recentDefects": [
      {
        "defectId": 1,
        "defectCode": "D20240130001",
        "defectType": "serious",
        "deviceName": "1#主变压器",
        "discoverTime": "2024-01-30 10:00:00"
      }
    ],
    "taskTrend": [
      {
        "date": "2024-01-24",
        "count": 10
      }
    ],
    "defectTrend": [
      {
        "date": "2024-01-24",
        "count": 5
      }
    ]
  }
}
```

### 15.2 获取待办事项
**接口描述**: 获取当前用户待办事项

**请求**
```
GET /api/v1/dashboard/todo
```

**响应**
```json
{
  "code": 200,
  "data": {
    "pendingTasks": [
      {
        "taskId": 2,
        "title": "专项巡检任务",
        "plannedStartTime": "2024-01-30 14:00:00",
        "stationName": "某某220kV变电站"
      }
    ],
    "pendingApprovals": [
      {
        "reportId": 1,
        "reportCode": "RPT20240130001",
        "reportType": "daily",
        "creatorName": "张三",
        "submitTime": "2024-01-30 18:30:00"
      }
    ],
    "pendingDefects": [
      {
        "defectId": 1,
        "defectCode": "D20240130001",
        "deviceName": "1#主变压器",
        "defectLevel": "B"
      }
    ]
  }
}
```
