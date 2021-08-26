<template>
  <div class="home">
    <el-container>
      <el-header>
        <Header></Header>
      </el-header>
      <el-container>
        <el-aside style="width: 200px">
          <el-menu
            default-active="this.$route.path"
            class="el-menu-vertical-demo"
            style="margin: 0"
            router>
            <el-menu-item index="/questionnaire">
              <i class="el-icon-circle-plus"></i>
              <span slot="title">新建问卷</span>
            </el-menu-item>
            <el-menu-item index="/history">
              <i class="el-icon-s-order"></i>
              <span slot="title">我的问卷</span>
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
import Header from "../components/Header.vue"
export default {
  components: {
    'Header': Header,
  }
}
</script>

<style scoped>
.el-header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding: 0;
}
.el-menu {
  margin-top: 20px;
  width: 195px;
}
.el-main {
  padding: 0;
}
</style>