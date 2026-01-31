<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
import { useOfflineStore } from '@/stores/offline'

// 初始化离线检测
const offlineStore = useOfflineStore()
offlineStore.initNetworkListener()
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f5f5;
}

#app {
  height: 100%;
}
</style>
