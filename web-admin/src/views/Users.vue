<template>
  <div class="users-page">
    <div class="page-header">
      <a-input-search
        v-model:value="searchText"
        placeholder="搜索用户"
        style="width: 300px"
        @search="handleSearch"
      />
      <a-button type="primary" @click="showAddModal">
        <template #icon><PlusOutlined /></template>
        新增用户
      </a-button>
    </div>

    <a-table
      :columns="columns"
      :data-source="dataSource"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
      row-key="userId"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'avatar'">
          <a-avatar :src="record.avatar">{{ record.realName?.charAt(0) }}</a-avatar>
        </template>
        <template v-else-if="column.key === 'role'">
          <a-tag color="blue">{{ getRoleText(record.roleId) }}</a-tag>
        </template>
        <template v-else-if="column.key === 'status'">
          <a-badge :status="record.status === 1 ? 'success' : 'default'" :text="record.status === 1 ? '启用' : '禁用'" />
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a @click="handleEdit(record)">编辑</a>
            <a @click="handleResetPassword(record)">重置密码</a>
            <a-popconfirm title="确定要删除吗？" @confirm="handleDelete(record)">
              <a style="color: #ff4d4f">删除</a>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <!-- 新增/编辑弹窗 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      @ok="handleSubmit"
      width="600px"
    >
      <a-form ref="formRef" :model="form" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
        <a-form-item label="用户名" name="username" :rules="[{ required: true, message: '请输入用户名' }]">
          <a-input v-model:value="form.username" placeholder="请输入用户名" :disabled="isEdit" />
        </a-form-item>
        <a-form-item label="真实姓名" name="realName" :rules="[{ required: true, message: '请输入真实姓名' }]">
          <a-input v-model:value="form.realName" placeholder="请输入真实姓名" />
        </a-form-item>
        <a-form-item label="密码" name="password" :rules="isEdit ? [] : [{ required: true, message: '请输入密码' }]">
          <a-input-password v-model:value="form.password" :placeholder="isEdit ? '不填则不修改' : '请输入密码'" />
        </a-form-item>
        <a-form-item label="手机号" name="phone">
          <a-input v-model:value="form.phone" placeholder="请输入手机号" />
        </a-form-item>
        <a-form-item label="邮箱" name="email">
          <a-input v-model:value="form.email" placeholder="请输入邮箱" />
        </a-form-item>
        <a-form-item label="角色" name="roleId" :rules="[{ required: true, message: '请选择角色' }]">
          <a-select v-model:value="form.roleId" placeholder="请选择角色">
            <a-select-option :value="1">系统管理员</a-select-option>
            <a-select-option :value="2">班长</a-select-option>
            <a-select-option :value="3">巡检员</a-select-option>
            <a-select-option :value="4">访客</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="form.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 重置密码弹窗 -->
    <a-modal v-model:open="resetPwdVisible" title="重置密码" @ok="handleConfirmReset">
      <a-form>
        <a-form-item label="新密码">
          <a-input-password v-model:value="newPassword" placeholder="请输入新密码" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'

const columns = [
  { title: '头像', dataIndex: 'avatar', key: 'avatar', width: 80 },
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '真实姓名', dataIndex: 'realName', key: 'realName' },
  { title: '手机号', dataIndex: 'phone', key: 'phone' },
  { title: '邮箱', dataIndex: 'email', key: 'email' },
  { title: '角色', dataIndex: 'roleId', key: 'role' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '最后登录', dataIndex: 'lastLoginTime', key: 'lastLoginTime' },
  { title: '操作', key: 'action', width: 200 }
]

const searchText = ref('')
const dataSource = ref([])
const loading = ref(false)
const pagination = ref({ current: 1, pageSize: 10, total: 0 })
const modalVisible = ref(false)
const modalTitle = ref('新增用户')
const formRef = ref(null)
const isEdit = ref(false)
const resetPwdVisible = ref(false)
const currentResetUser = ref(null)
const newPassword = ref('')

const form = ref({
  userId: null,
  username: '',
  realName: '',
  password: '',
  phone: '',
  email: '',
  roleId: null,
  status: 1
})

const getRoleText = (roleId) => {
  const map = { 1: '系统管理员', 2: '班长', 3: '巡检员', 4: '访客' }
  return map[roleId] || '未知'
}

const handleSearch = () => {}

const handleTableChange = (pag) => {
  pagination.value.current = pag.current
}

const showAddModal = () => {
  modalTitle.value = '新增用户'
  isEdit.value = false
  resetForm()
  modalVisible.value = true
}

const handleEdit = (record) => {
  modalTitle.value = '编辑用户'
  isEdit.value = true
  form.value = { ...record }
  modalVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    message.success('操作成功')
    modalVisible.value = false
  } catch (error) {
    console.error('验证失败:', error)
  }
}

const resetForm = () => {
  form.value = {
    userId: null,
    username: '',
    realName: '',
    password: '',
    phone: '',
    email: '',
    roleId: null,
    status: 1
  }
}

const handleResetPassword = (record) => {
  currentResetUser.value = record
  newPassword.value = ''
  resetPwdVisible.value = true
}

const handleConfirmReset = () => {
  if (!newPassword.value) {
    message.warning('请输入新密码')
    return
  }
  message.success('密码重置成功')
  resetPwdVisible.value = false
}

const handleDelete = (record) => {
  message.success('删除成功')
}
</script>

<style scoped>
.users-page {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}
</style>
