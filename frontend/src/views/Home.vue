<template>
  <div class="home">
    <el-container>
      <el-header>
        <div class="header">
          <router-link to="/">
            <div class="logo">
              <Logo></Logo>
              <div class="web-title">问卷星球</div>
            </div>
          </router-link>
        </div>
      </el-header>
      <el-container>
        <el-aside style="width: 200px">
          <el-menu
            default-active="this.$route.path"
            class="el-menu-vertical-demo"
            @open="handleOpen"
            @close="handleClose"
            router>
            <el-menu-item index="/questionnaire">
              <i class="el-icon-circle-plus"></i>
              <span slot="title">新建问卷</span>
            </el-menu-item>
            <el-menu-item index="/history">
              <i class="el-icon-s-order"></i>
              <span slot="title">历史问卷</span>
            </el-menu-item>
            <el-menu-item index="/recycle">
              <i class="el-icon-delete-solid"></i>
              <span slot="title">回收站</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import logo from "../components/logo.vue"
export default {
    components: {
        'Logo': logo
    }
}
</script>

<style scoped>
.el-header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
}
.logo {
  display: inline-flex;
  justify-content: center;
  align-items: center;
}
.web-title {
  margin-left: 15px;
  font-family: 仿宋;
  font-weight: 800;
  font-size: 30px;
  position: relative;
}
.router-link-active {
  text-decoration: none;
}
a {
  text-decoration: none;
  color: #000;
}
a:hover {
  color: rgba(46, 140, 219, 0.94);
}
.el-menu {
  margin-top: 20px;
  width: 195px;
}
.el-main {
  padding: 0;
}
</style>