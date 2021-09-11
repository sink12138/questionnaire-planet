<template>
  <div id="personal">
    <el-card>
      <el-form class="form" :rules="rules" ref="formData" :model="formData">
        <el-button style="align-self:flex-start" @click="goBack()">返回主页</el-button>
        <div style="font-size: 20px">修改个人信息</div>
        <el-form-item label="电子邮箱" prop="email">
          <el-input v-model="formData.email" autocomplete="off" placeholder="请输入您的电子邮箱" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="用户昵称" prop="username">
          <el-input v-model="formData.username" autocomplete="off" placeholder="请输入用户名" v-focus></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="formData.password" autocomplete="off" show-password placeholder="请输入密码"></el-input>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="modify">修改信息</el-button>
    </el-card>
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
        callback();
      }
    };
    return{
      formData: {
        email:"",
        username:"",
        password:""
      },
      rules: {
        email: [{ validator: checkEmail, trigger: "blur" }],
        password: [{ validator: validatePass, trigger: "blur" }],
        checkpassword: [{ validator: validatePass2, trigger: "blur" }],
        username: [{ validator: checkUsername, trigger: "blur" }],
      },
    }
  },
  mounted: function(){
    this.$axios({
        method: "get",
        url: "http://139.224.50.146/apis/home",
      }).then((res) => {
        console.log(res);
        this.formData.email = res.data.email;
        this.formData.username = res.data.username;
        this.formData.password = res.data.password;
      });
  },
  methods: {
    goBack: function(){
      this.$router.push('/')
    },
    modify: function(){
      var modifydata = {
          username: this.formData.username,
          password: this.formData.password,
        };

        this.$axios({
          method: "post",
          url: "http://139.224.50.146/apis/home/modify",
          data: JSON.stringify(modifydata),
        })
          .then((res) => {
            console.log(res);
            if (res.data.success == false) {
              this.$notify({
                title: "提示",
                message: res.data.message,
                type: "info"
              });
            } else {
              this.$notify({
                title: "提示",
                message: "信息修改成功",
                type: "success"
              });
            }
          })
          .catch((error) => {
            console.log(error);
          });
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
#personal {
  height: 100%;
  background-image: url("../assets/Main_bg.jpg");
  background-size: 100%;
  background-repeat: no-repeat;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
.form {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
.el-card {
  width: 600px;
  height: 500px;
  opacity: 0.9;
}
.el-input {
  width: 300px
}
</style>