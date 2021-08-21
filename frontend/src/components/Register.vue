<template>
  <div id="register">
    <label>用户名<input type="text" v-model="username"></label>
    <label>密码<input type="password" v-model="password"></label>
    <label>确认密码<input type="password" v-model="password1"></label>
    <label>电子邮箱<input type="text" v-model="email"></label>
    <input type="button" value="注册" @click="register">
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
      }
    }
  }
</script>