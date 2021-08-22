<template>
  <div id="register">
    <el-page-header @back="goBack" content="欢迎来到问卷星球">
    </el-page-header>

    <el-form style="display:block; margin:3px 300px" :inline="false" :rules="rules" ref="formData" :model="formData">
      <el-form-item label="用户名" :label-width="formLabelWidth" prop="username">
        <el-input v-model="formData.username" autocomplete="off" style="width: 300px" placeholder="请输入用户名" v-focus></el-input>
      </el-form-item>
      <el-form-item label="密码" :label-width="formLabelWidth" prop="password">
        <el-input v-model="formData.password" autocomplete="off" show-password style="width: 300px" placeholder="请输入密码"></el-input>
      </el-form-item>
      <el-form-item label="确认密码" :label-width="formLabelWidth" prop="checkpassword">
        <el-input v-model="formData.checkpassword" autocomplete="off" show-password style="width: 300px" placeholder="请再次输入密码"></el-input>
      </el-form-item>
      <el-form-item label="电子邮箱" :label-width="formLabelWidth" prop="email">
        <el-input v-model="formData.email" autocomplete="off" style="width: 300px" placeholder="请输入您的电子邮箱"></el-input>
      </el-form-item>
    </el-form>
    <el-button type="primary" @click="register" style="display:block;margin:5px 5px 5px 750px">注册</el-button>
  </div>
</template>

<script>
  export default {
    data() {
      var checkEmail = (rule, value, callback) => {
        if (!value) {
          return callback(new Error("邮箱不能为空"));
        } else {
          callback();
        }
      };
      var validatePass = (rule, value, callback) => {
        if (value === "") {
          callback(new Error("请输入密码"));
        } else {
          if (value.length < 6 || value.length > 20) {
            callback(new Error("请输入六至二十位"));
          }
          var regx = /^(?!([a-zA-Z]+|\d+)$)[a-zA-Z\d]{6,20}$/;
          if (!this.formData.password.match(regx)) {
            callback(new Error("请同时包含字母数字"));
          }
          if (this.formData.checkpassword !== '') {
            this.$refs.formData.validateField('checkpassword');
          }
          callback();
        }
      };
      var validatePass2 = (rule, value, callback) => {
        if (value === "") {
          callback(new Error("请再次输入密码"));
        } else if (value !== this.formData.password) {
          callback(new Error("两次输入密码不一致!"));
        } else {
          callback();
        }
      };
      var checkUsername = (rule, value, callback) => {
        if (!value) {
          return callback(new Error("用户名不能为空"));
        } else {
          if (value.length > 20) {
            callback(new Error("用户名过长"));
          }
          var regx = /^(?!([a-zA-Z]+|\d+)$)[a-zA-Z\d]*$/;
          if (!this.formData.username.match(regx)) {
            callback(new Error("请同时且仅包含字母数字"));
          }
          callback();
        }
      };
      return {
        formData: {
          username:"",
          password:"",
          email:"",
          checkpassword:""
        },
        formLabelWidth: '100px',
        rules: {
          email: [{ validator: checkEmail, trigger: "blur" }],
          password: [{ validator: validatePass, trigger: "blur" }],
          checkpassword: [{ validator: validatePass2, trigger: "blur" }],
          username: [{ validator: checkUsername, trigger: "blur" }],
        },
      };
    },
    methods: {
      register: function(){
        this.$refs.formData.validate((valid) => {
          if (valid) {
            var registerdata = {
              username: this.formData.username,
              email: this.formData.email,
              password: this.formData.password,
            };

            this.$axios({
              method: "post",
              url: "http://139.224.50.146/apis/register",
              data: JSON.stringify(registerdata),
            })
              .then((res) => {
                console.log(res);
                console.log(registerdata);
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
                  this.$router.push('/')
                }
              })
              .catch((error) => {
                console.log(error);
              });
          }
          else {
            console.log("error submit!!");
            return false;
          }
        });
        
      },
      goBack: function(){
        this.$router.push('/')
      }
    },
    directives: {
      focus: {
        inserted: function(el) {
          el.querySelector('input').focus();
        }
      }
    }
  }
</script>

<style>
  .el-input {
    width: 300px
  }
</style>