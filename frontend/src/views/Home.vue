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
      </el-header>
      <el-container>
        <el-aside style="width: 200px">
          <el-menu
            default-active="this.$route.path"
            class="el-menu-vertical-demo"
            @open="handleOpen"
            @close="handleClose"
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
import logo from "../components/svg-logo.vue"
export default {
  components: {
    'Logo': logo
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
          this.$message({
            message: "登录成功！",
            type: "success",
          });
          this.dialogFormVisible = false;
        } else {
          alert("用户名或密码错误！");
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
      this.$message({
        message: "退出登录成功！",
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
.butt {
  position: absolute;
  float: right;
  right: 10px;
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