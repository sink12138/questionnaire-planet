<template>
  <div id="register">
    <el-page-header @back="goBack" content="欢迎来到问卷星球">
    </el-page-header>

    <label>用户名<el-input v-model="username" placeholder="请输入用户名" autofocus="true" style="margin:5px 20px">></el-input></label><br><br>
    <label>密码<el-input v-model="password" placeholder="请输入密码" show-password style="margin:5px 25px"></el-input></label><br><br>
    <label>确认密码<el-input v-model="password1" placeholder="请再次输入密码" show-password style="margin:5px 25px"></el-input></label><br><br>
    <label>电子邮箱<el-input v-model="password" placeholder="请输入您的电子邮箱" style="margin:5px 25px"></el-input></label><br><br>
    <el-button type="primary" @click="register" style="display:block;margin:5px 5px 5px 750px">注册</el-button>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        username:"",
        password:"",
        password1:"",
        email:""
      }
    },
    methods: {
      register: function(){
        if(this.password != this.password1) {
          alert("两次密码输入不一致")
        }
        else {
          return this.username, this.password, this.email
        }
      },
      goBack: function(){
        this.$router.push('/login')
      }
    }
  }
</script>

<style>
  .el-input {
    width: 300px
  }
</style>