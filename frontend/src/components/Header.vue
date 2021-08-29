<template>
  <div class="header">
    <router-link to="/">
      <div class="logo">
        <Logo></Logo>
        <div class="web-title">问卷星球</div>
      </div>
    </router-link>
    <router-link to="/questionnaire">
      <div class="router">
        创建问卷
      </div>
    </router-link>
    <router-link to="/history">
      <div class="router">
        我的问卷
      </div>
    </router-link>
    <div class="butt">
      <div v-if="this.$store.state.isLogin == false">
        <el-dropdown>
          <el-button class="user" icon="el-icon-user" style="font-size:30px; border:none">
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <router-link to="/">
              <el-dropdown-item>主页</el-dropdown-item>
            </router-link>
            <el-dropdown-item @click.native="dialogFormVisible1 = true">登录/注册</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
      <div v-else>
        <el-dropdown>
          <el-button class="user" icon="el-icon-user" style="font-size:30px; border:none">
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <router-link to="/">
              <el-dropdown-item>主页</el-dropdown-item>
            </router-link>
            <router-link to="/personal">
              <el-dropdown-item>个人信息</el-dropdown-item>
            </router-link>
            <el-dropdown-item @click.native="logout()">登出</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
    <el-dialog title="欢迎来到问卷星球！" :visible.sync="dialogFormVisible1" style="text-align:left; width:1050px; margin:auto">
      <el-form :model="formData" :rules="rules" ref="formData">
        <el-form-item label="电子邮箱" :label-width="formLabelWidth" prop="email">
          <el-input v-model="formData.email" autocomplete="off" style="width: 300px" placeholder="请输入您的电子邮箱" v-focus @keyup.enter.native="login"></el-input>
        </el-form-item>
        <el-form-item label="密码" :label-width="formLabelWidth" prop="password">
          <el-input v-model="formData.password" autocomplete="off" show-password style="width: 300px" placeholder="请输入密码" @keyup.enter.native="login"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible1 = false">取 消</el-button>
        <el-button type="primary" @click="login">登 录</el-button>
        <el-link @click.native="dialogFormVisible2 = true" type="info" style="margin:5px 5px 5px 320px"><sup>还没有账号？点此处注册账号</sup></el-link>
      </div>
    </el-dialog>
    <el-dialog title="欢迎来到问卷星球！" :visible.sync="dialogFormVisible2" style="text-align:left; width:1050px; margin:auto">
      <el-form :model="registerData" :rules="rules" ref="registerData">
        <el-form-item label="电子邮箱" :label-width="formLabelWidth" prop="email">
          <el-input v-model="registerData.email" autocomplete="off" style="width: 300px" placeholder="请输入您的电子邮箱" v-focus @keyup.enter.native="register"></el-input>
        </el-form-item>
        <el-form-item label="用户昵称" :label-width="formLabelWidth" prop="username">
          <el-input v-model="registerData.username" autocomplete="off" style="width: 300px" placeholder="请输入您的用户昵称" @keyup.enter.native="register"></el-input>
        </el-form-item>
        <el-form-item label="密码" :label-width="formLabelWidth" prop="password">
          <el-input v-model="registerData.password" autocomplete="off" show-password style="width: 300px" placeholder="请输入密码" @keyup.enter.native="register"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" :label-width="formLabelWidth" prop="checkpassword">
          <el-input v-model="registerData.checkpassword" autocomplete="off" show-password style="width: 300px" placeholder="请再次输入密码" @keyup.enter.native="register"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible2 = false">取 消</el-button>
        <el-button type="primary" @click="register">注 册</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import logo from "./svg_logo.vue"
export default {
  components: {
    "Logo": logo
  },
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
          if (!value.match(regx)) {
            callback(new Error("请同时包含字母数字"));
          }
          callback();
      }
    };
    var validatePass2 = (rule, value, callback) => {
        if (value === "") {
          callback(new Error("请再次输入密码"));
        } else if (value !== this.registerData.password) {
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
    return {
      formData: {
        email:"",
        password:""
      },
      registerData: {
        username:"",
        password:"",
        email:"",
        checkpassword:""
      },
      rules: {
        email: [{ validator: checkEmail, trigger: "blur" }],
        password: [{ validator: validatePass, trigger: "blur" }],
        checkpassword: [{ validator: validatePass2, trigger: "blur" }],
        username: [{ validator: checkUsername, trigger: "blur" }],
      },
      dialogFormVisible1: false,
      dialogFormVisible2: false,
      formLabelWidth: '100px'
    }
  },
  created(){
    var login = sessionStorage.getItem("isLogin")
    if (login == undefined)
      sessionStorage.setItem("isLogin", false);
    if (login == "true") {
      this.$store.commit("login");
    } else if (login == "false") {
      this.$store.commit("logout");
    }
  },
  methods: {
    login: function() {
      this.$axios({
        method: "post",
        url: "http://139.224.50.146/apis/login",
        data: JSON.stringify(this.formData),
      }).then((res) => {
        console.log(this.formData);
        if (res.data.success == true) {
          sessionStorage.setItem("isLogin", true);
          this.$store.commit("login");
          this.$notify({
            title: "提示",
            message: "登录成功！",
            type: "success",
          });
          this.dialogFormVisible1 = false;
        } else {
          this.$notify({
            title: "提示",
            message: "用户名或密码错误",
            type: "error",
          });
        }
        console.log(res);
      });
    },
    logout: function(){
      sessionStorage.setItem("isLogin", false);
      this.$axios({
        method: "post",
        url: "http://139.224.50.146/apis/logout",
      }).then((res) => {
        console.log(res);
      });
      console.log("logout submit!");
      this.$store.commit("logout");
      this.$notify({
        title: "提示",
        message: "退出登录成功",
        type: "success",
      });
    },
    register: function(){
      this.$refs.registerData.validate((valid) => {
        if (valid) {
          var registerdata = {
            username: this.registerData.username,
            email: this.registerData.email,
            password: this.registerData.password,
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
                this.$notify({
                  title: "提示",
                  message: res.data.message,
                  type: "info",
                });
              } else {
                this.$notify({
                  title: "提示",
                  message: "注册完毕，请查看邮箱验证账号",
                  type: "success",
                });
                this.dialogFormVisible2 = false;
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
    topersonal: function(){
      this.$router.push("/personal")
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

<style scoped>
.header {
  background: #ffffff;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  height: 60px;
}
.logo {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  margin-left: 100px;
}
.router {
  font-size: 18px;
  font-weight: lighter;
  margin-left: 30px;
  margin-right: 30px;
}
.butt {
  position: absolute;
  float: right;
  right: 10px;
}
.web-title {
  margin-left: 15px;
  margin-right: 30px;
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
</style>