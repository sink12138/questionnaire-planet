<template>
  <div id="login">
    <label>用户名<input type="text" v-model="username"></label>
    <label>密码<input type="password" v-model="password"></label>
    <input type="button" value="登录" @click="login">
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
      }
    }
  }
</script>