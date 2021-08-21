<template>
  <div class="home">
    <el-container>
      <el-header>
        <router-link to="/">
            <div class="logo">
              <Logo></Logo>
              <div class="web-title">问卷星球</div>
            </div>
          </router-link>
        <el-button type="success" @click="dialogFormVisible = true">登录/注册</el-button>

        <el-dialog title="欢迎来到问卷星球！" :visible.sync="dialogFormVisible" style="text-align:left; width:1050px; margin:auto">
          <el-form>
            <el-form-item label="用户名" :label-width="formLabelWidth">
              <el-input v-model="username" autocomplete="off" style="width: 300px"></el-input>
            </el-form-item>
            <el-form-item label="密码" :label-width="formLabelWidth">
              <el-input v-model="password" autocomplete="off" show-password style="width: 300px"></el-input>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="dialogFormVisible = false">取 消</el-button>
            <el-button type="primary" @click="dialogFormVisible = false; login">登 录</el-button>
            <el-link href="register" type="info" style="margin:5px 5px 5px 320px"><sup>还没有账号？点此处注册账号</sup></el-link>
          </div>
        </el-dialog>
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
import logo from "../components/svg-logo.vue"
export default {
  components: {
        'Logo': logo
    },
  data() {
    return {
      username:"",
      password:"",
      dialogFormVisible: false,
      formLabelWidth: '100px'
    }
  },
  methods: {
    login: function() {
      console.log(2)
      return this.username, this.password
    }
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