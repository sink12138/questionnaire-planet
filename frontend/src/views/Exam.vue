<template>
  <el-container>
    <el-header>
      <p>Exam</p>
    </el-header>

    <div class="timer">
      <span>{{ times }}s</span>
    </div>

    <div>
      <el-button type="primary" @click="submit">提交按钮(被点了{{ counter }}次)</el-button>
      <el-button type="primary" @click="restart">点我重新计时！</el-button>
    </div>
  </el-container>
  
</template>

<script>
export default {
  data() {
    return {
      counter: 0,
      times: 5,
    }
  },
  mounted: function(){
    this.timer = setInterval(()=>{
        this.times--
        if(this.times===0){
          this.submit();
          clearInterval(this.timer);
        }
      },1000)
  },
  methods: {
    submit() {
      this.counter++;
    },
    restart() {
      this.times = 5;
      this.timer = setInterval(()=>{
        this.times--
        if(this.times===0){
          this.submit();
          clearInterval(this.timer);
        }
      },1000)
    }
  },
}
</script>

<style>

</style>