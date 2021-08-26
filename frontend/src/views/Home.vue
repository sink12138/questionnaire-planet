<template>
  <div class="home">
    <Header></Header>
    <div class="block">
      <el-carousel height="510px" :interval="5000" arrow="hover">
        <el-carousel-item class="carousel">
          <div class="title_1">创建编辑多种问卷</div>
          <div class="title_2">自由创建普通、投票、报名等多类型问卷</div>
        </el-carousel-item>
        <el-carousel-item class="carousel">
          <div class="title_1">一键开启问卷收集</div>
          <div class="title_2">简约好用，一键开启问卷发布与收集</div>
        </el-carousel-item>
        <el-carousel-item class="carousel">
          <div class="title_1">数据在线统计分析</div>
          <div class="title_2">回收数据在线统计分析，图表显示直观清晰</div>
        </el-carousel-item>
        <el-carousel-item class="carousel">
          <div class="title_1">简单方便管理问卷</div>
          <div class="title_2">想不出来怎么说，反正挺简单的</div>
        </el-carousel-item>
      </el-carousel>
    </div>
    <div class="">
      <el-button>进入个人主页</el-button>
      <el-button>开始创建问卷</el-button>
    </div>
  </div>
</template>

<script>
import Header from "../components/Header.vue"
export default {
  components: {
    "Header": Header
  }
}
</script>

<style scoped>
.block {
  background-image: url("../assets/Home_bg.jpg");
  background-size: 100%;
  background-repeat: no-repeat;
}
.carousel {
  color: #ffffff;
}
.title_1 {
  font-size: 48px;
}
.title_2 {
  font-size: 24px;
}
</style>