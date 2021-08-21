<template>
  <div id="login">
    <el-page-header @back="goBack" content="欢迎来到问卷星球">
    </el-page-header>

    <label>用户名<el-input v-model="username" placeholder="请输入用户名" autofocus="true" style="margin:5px 20px">></el-input></label><br><br>
    <label>密码<el-input v-model="password" placeholder="请输入密码" show-password style="margin:5px 25px"></el-input></label><br><br>
    <el-button type="primary" @click="login" style="display:block;margin:5px 5px 5px 750px">登录</el-button>
    <el-link href="register" type="info" style="margin:5px 5px 5px 320px"><sup>还没有账号？点此处注册账号</sup></el-link>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        username:"",
        password:""
      }
    },
    methods: {
      login: function(){
        return this.username, this.password
      },
      goBack: function(){
        this.$router.push('/')
      }
    }
  }
</script>

<style>
  .el-input {
    width: 300px
  }
</style>