<template>
  <el-container>
    <el-header>
      <p>Exam</p>
    </el-header>

    <div class="timer">
      <p>截止时间：{{ deadlline }}</p>
      <p>网页打开时间：{{ nowtime }}</p>
      <p>剩余时间：{{ lefttime }}s --------- {{ day }}天{{ hour }}天{{ minute }}天{{ second }}天</p>
    </div>

    <div>
      <el-button type="primary" @click="submit">提交按钮(被点了{{ counter }}次)</el-button>
    </div>

    <div>

    </div>
  </el-container>
  
</template>

<script>
export default {
  data() {
    return {
      deadlline: "2021-08-30 14:00:00",
      nowtime: "",
      lefttime: 0,
      day: 0,
      hour: 0,
      minute: 0,
      second: 0,
      counter: 0,
    }
  },
  created: function() {
    this.$axios({
      method: "get",
      url: "http://139.224.50.146/apis/time",
    }).then((res) => {
      if (res.data.success == true) {
        this.nowtime = new Date(res.data.time).getTime() / 1000;
        console.log(this.nowtime)
      } else {
        this.$message.error(res.data.message);
      }
      console.log(res);
    });


  },
  mounted: function(){
    this.lefttime = (new Date(this.deadlline).getTime() / 1000) - this.nowtime;

    this.timer = setInterval(()=>{
        this.lefttimes--

        this.day = Math.floor(this.lefttime / (60 * 60 * 24));
        this.hour = Math.floor(this.lefttime / (60 * 60)) - 24 * this.day;
        this.minute = Math.floor(this.lefttime / 60) - 24 * 60 * this.day - 60 * this.hour;
        this.second = Math.floor(this.lefttime) - 24 * 60 * 60 * this.day - 60 * 60 * this.hour - 60 * this.minute;

        if(this.lefttimes == 0){
          this.submit();
          clearInterval(this.timer);
        }
      },1000)
  },
  methods: {
    submit() {
      this.counter++;
    },
  },
}
</script>

<style>

</style>