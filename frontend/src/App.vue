<script setup>
import { computed } from 'vue'
import { RouterView, useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getRealName, clearLoginState } from '@/utils/auth'

const route = useRoute()
const router = useRouter()

// 登录、注册页不显示导航栏
const showNav = computed(() => !['/login', '/register'].includes(route.path))
// 登录后跳转页面时重新读取姓名
const realName = computed(() => {
  route.fullPath
  return getRealName()
})

function handleLogout() {
  clearLoginState()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<template>
  <el-container class="layout">
    <el-header v-if="showNav" class="header">
      <div class="header-inner">
        <div class="brand">🏫 校园活动报名系统</div>
        <el-menu mode="horizontal" router :default-active="route.path" class="menu" :ellipsis="false">
          <el-menu-item index="/activity/list">活动列表</el-menu-item>
          <el-menu-item index="/my/activity">我的报名</el-menu-item>
        </el-menu>
        <div class="user-area">
          <span class="welcome">你好，{{ realName }}</span>
          <el-button text type="danger" @click="handleLogout">退出登录</el-button>
        </div>
      </div>
    </el-header>

    <el-main class="main">
      <RouterView />
    </el-main>
  </el-container>
</template>

<style scoped>
.layout {
  min-height: 100vh;
  background: #f5f7fa;
}

.header {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0;
}

.header-inner {
  display: flex;
  align-items: center;
  max-width: 1100px;
  margin: 0 auto;
  height: 60px;
}

.brand {
  font-size: 18px;
  font-weight: 600;
  color: #409eff;
  white-space: nowrap;
}

.menu {
  flex: 1;
  border-bottom: none;
  margin-left: 40px;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
}

.welcome {
  color: #606266;
  font-size: 14px;
}

.main {
  max-width: 1100px;
  width: 100%;
  margin: 0 auto;
}
</style>
