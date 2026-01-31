<template>
  <a-layout class="admin-layout">
    <!-- 侧边栏 -->
    <a-layout-sider
      v-model:collapsed="collapsed"
      :trigger="null"
      collapsible
      theme="dark"
      width="240"
    >
      <div class="logo">
        <h2 v-if="!collapsed">变电站巡检</h2>
        <h2 v-else>巡检</h2>
      </div>

      <a-menu
        v-model:selectedKeys="selectedKeys"
        v-model:openKeys="openKeys"
        mode="inline"
        theme="dark"
        @click="handleMenuClick"
      >
        <a-menu-item key="/admin/dashboard">
          <template #icon><DashboardOutlined /></template>
          数据概览
        </a-menu-item>

        <a-menu-item key="/admin/stations">
          <template #icon><BuildOutlined /></template>
          变电站管理
        </a-menu-item>

        <a-menu-item key="/admin/devices">
          <template #icon><AppstoreOutlined /></template>
          设备管理
        </a-menu-item>

        <a-menu-item key="/admin/templates">
          <template #icon><FileTextOutlined /></template>
          巡检模板
        </a-menu-item>

        <a-menu-item key="/admin/plans">
          <template #icon><CalendarOutlined /></template>
          巡检计划
        </a-menu-item>

        <a-menu-item key="/admin/tasks">
          <template #icon><CheckSquareOutlined /></template>
          巡检任务
        </a-menu-item>

        <a-menu-item key="/admin/defects">
          <template #icon><AlertOutlined /></template>
          缺陷管理
        </a-menu-item>

        <a-menu-item key="/admin/reports">
          <template #icon><FileOutlined /></template>
          巡检报告
        </a-menu-item>

        <a-menu-item key="/admin/users">
          <template #icon><TeamOutlined /></template>
          用户管理
        </a-menu-item>

        <a-menu-item key="/admin/settings">
          <template #icon><SettingOutlined /></template>
          系统设置
        </a-menu-item>
      </a-menu>
    </a-layout-sider>

    <a-layout>
      <!-- 顶部导航 -->
      <a-layout-header class="header">
        <div class="header-left">
          <MenuUnfoldOutlined
            v-if="collapsed"
            class="trigger"
            @click="collapsed = !collapsed"
          />
          <MenuFoldOutlined
            v-else
            class="trigger"
            @click="collapsed = !collapsed"
          />
        </div>

        <div class="header-right">
          <a-space>
            <a-badge :count="notificationCount" :offset="[-5, 5]">
              <BellOutlined style="font-size: 18px" />
            </a-badge>

            <a-dropdown>
              <a-space class="user-info">
                <a-avatar :src="userStore.userInfo?.avatar" />
                <span>{{ userStore.userInfo?.realName }}</span>
              </a-space>
              <template #overlay>
                <a-menu>
                  <a-menu-item key="profile">
                    <UserOutlined />
                    个人信息
                  </a-menu-item>
                  <a-menu-item key="password">
                    <LockOutlined />
                    修改密码
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item key="logout" @click="handleLogout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </a-space>
        </div>
      </a-layout-header>

      <!-- 内容区域 -->
      <a-layout-content class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { ref, watch, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Modal, message } from 'ant-design-vue'
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  DashboardOutlined,
  BuildOutlined,
  AppstoreOutlined,
  FileTextOutlined,
  CalendarOutlined,
  CheckSquareOutlined,
  AlertOutlined,
  FileOutlined,
  TeamOutlined,
  SettingOutlined,
  BellOutlined,
  UserOutlined,
  LockOutlined,
  LogoutOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const collapsed = ref(false)
const selectedKeys = ref([route.path])
const openKeys = ref([])
const notificationCount = ref(0)

const handleMenuClick = ({ key }) => {
  router.push(key)
}

const handleLogout = () => {
  Modal.confirm({
    title: '退出登录',
    content: '确定要退出登录吗？',
    onOk: () => {
      userStore.logout()
      message.success('已退出登录')
      router.replace('/login')
    }
  })
}

watch(
  () => route.path,
  (path) => {
    selectedKeys.value = [path]
  }
)
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.logo h2 {
  margin: 0;
  font-size: 18px;
}

.header {
  background: #fff;
  padding: 0 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.trigger {
  font-size: 18px;
  cursor: pointer;
  transition: color 0.3s;
}

.trigger:hover {
  color: #1890ff;
}

.user-info {
  cursor: pointer;
}

.content {
  margin: 24px;
  padding: 24px;
  background: #fff;
  border-radius: 4px;
  min-height: 400px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
