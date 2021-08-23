<template>
  <span>Exam</span>
</template>

<script>
export default {
  data() {
    return {
      
    }
  },
  /*页面打开3秒后则执行test方法，输出2333。可用于定时触发提交问卷事件。*/
  mounted: function(){
    this.timer = setTimeout(this.test, 3000)
  },
  methods: {
    test: function(){
      console.log("2333")
    }
  },
  beforeDestroy() {
    clearTimeout(this.timer);
  }
}
</script>

<style>

</style>