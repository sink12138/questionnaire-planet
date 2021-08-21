<template>
  <div id="register">
    <el-page-header @back="goBack" content="欢迎来到问卷星球">
    </el-page-header>

    <el-form style="display:block; margin:3px 300px" inline="false">
      <el-form-item label="用户名" :label-width="formLabelWidth">
        <el-input v-model="username" autocomplete="off" style="width: 300px" placeholder="请输入用户名"></el-input>
      </el-form-item>
      <el-form-item label="密码" :label-width="formLabelWidth">
        <el-input v-model="password" autocomplete="off" show-password style="width: 300px" placeholder="请输入密码"></el-input>
      </el-form-item>
      <el-form-item label="确认密码" :label-width="formLabelWidth">
        <el-input v-model="password1" autocomplete="off" show-password style="width: 300px" placeholder="请再次输入密码"></el-input>
      </el-form-item>
      <el-form-item label="电子邮箱" :label-width="formLabelWidth">
        <el-input v-model="email" autocomplete="off" style="width: 300px" placeholder="请输入您的电子邮箱"></el-input>
      </el-form-item>
    </el-form>
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
        email:"",
        formLabelWidth: '100px'
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