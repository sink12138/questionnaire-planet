<template>
  <div id="register">
    <el-page-header @back="goBack" content="欢迎来到问卷星球">
    </el-page-header>

    <el-form style="display:block; margin:3px 300px" inline="false">
      <el-form-item label="用户名" :label-width="formLabelWidth">
        <el-input v-model="fromData.username" autocomplete="off" style="width: 300px" placeholder="请输入用户名"></el-input>
      </el-form-item>
      <el-form-item label="密码" :label-width="formLabelWidth">
        <el-input v-model="fromData.password" autocomplete="off" show-password style="width: 300px" placeholder="请输入密码"></el-input>
      </el-form-item>
      <el-form-item label="确认密码" :label-width="formLabelWidth">
        <el-input v-model="password1" autocomplete="off" show-password style="width: 300px" placeholder="请再次输入密码"></el-input>
      </el-form-item>
      <el-form-item label="电子邮箱" :label-width="formLabelWidth">
        <el-input v-model="fromData.email" autocomplete="off" style="width: 300px" placeholder="请输入您的电子邮箱"></el-input>
      </el-form-item>
    </el-form>
    <el-button type="primary" @click="register" style="display:block;margin:5px 5px 5px 750px">注册</el-button>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        fromData: {
          username:"",
          password:"",
          email:""
        },
        password1:"",
        formLabelWidth: '100px'
      }
    },
    methods: {
      register: function(){
        if(this.fromData.password != this.password1) {
          alert("两次密码输入不一致")
        }
        else {
          this.$axios({
            method: "post",
            url: "http://139.224.50.146/apis/register",
            data: JSON.stringify(this.fromData),
          })
            .then((res) => {
              console.log(res);
              if (res.data.success == false) {
                this.$message({
                  showClose: true,
                  message: res.data.message,
                });
              } else {
                this.$message({
                  showClose: true,
                  message: "注册完毕，请查看邮箱验证账号",
                  type: "success",
                });
              }
            })
            .catch((error) => {
              console.log(error);
            });
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