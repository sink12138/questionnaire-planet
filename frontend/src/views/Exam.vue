<template>
  <el-container>
    <el-header>
      <p>Exam</p>
    </el-header>
    <div class="timer">
      <el-button v-if="show" @click="start" >{{ info }}</el-button>
      <el-button v-if="!show">{{ times }}s</el-button>
    </div>
  </el-container>
  
</template>

<script>
export default {
  data() {
    return {
      times: 5,
      show: true,
      info: "点我开始计时！"
    }
  },
  mounted: function(){

  },
  methods: {
    start() {
      this.show = false
      this.timer = setInterval(()=>{
        this.times--
        if(this.times===0){
          this.show = true
          this.info = "时间到，再点我重新计时！"
          this.times = 5
          clearInterval(this.timer)
        }
      },1000)
    }
  },
}
</script>

<style>

</style>