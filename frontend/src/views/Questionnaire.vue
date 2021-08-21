<template>
  <div id="Questionnaire">
      <div class="banner">
          <div class="type create_by_normal">
              <div class="inner">
                    <div class="type_show">
                        <question-pic></question-pic>
                    </div>
                    <h2 class="title">创建普通问卷</h2>
                    <h3 class="subtitle">创建包含单选、多选等题型的问卷</h3>
                    <router-link to="/normal/new">
                        <el-button>开始</el-button>
                    </router-link>
              </div>
          </div>
          <div class="type create_by_vote">
              <div class="inner">
                    <div class="type_show">
                        <question-pic></question-pic>
                    </div>
                    <h2 class="title">创建投票问卷</h2>
                    <h3 class="subtitle">创建包含投票功能的问卷</h3>
                    <router-link to="/vote/new">开始</router-link>
              </div>
          </div>
          <div class="type create_by_apply">
              <div class="inner">
                    <div class="type_show">
                        <question-pic></question-pic>
                    </div>
                    <h2 class="title">创建报名问卷</h2>
                    <h3 class="subtitle">创建限制选项选择人数的问卷</h3>
                    <router-link to="/apply/new">开始</router-link>
              </div>
          </div>
          <div class="type create_by_exam">
              <div class="inner">
                    <div class="type_show">
                        <question-pic></question-pic>
                    </div>
                    <h2 class="title">创建考试问卷</h2>
                    <h3 class="subtitle">创建规定时间自动提交的问卷</h3>
                    <router-link to="/exam/new">开始</router-link>
              </div>
          </div>
      </div>
    <router-view></router-view>
  </div>
</template>

<script>
import svg from "../components/svg.vue"
export default {
    components: {
        'question-pic': svg
    }
}
</script>

<style>
.banner {
    position: relative;
    height: 510px;
    overflow: hidden;
    zoom: 1;
}
.type {
    float: left;
    height: 100%;
    width: 25%;
    position: relative;
    text-align: center;
    display: flex;
    justify-content: center;
    align-items: center;
}
.type_show {
    margin-top: -100px;
}
.title {
    margin-top: 10px;
    margin-bottom: 0;
}
.subtitle {
    margin-top: 0;
    margin-bottom: 38px;
}
.create_by_normal{
    background-color: rgba(46, 140, 219, 0.94);
}
.create_by_vote{
    background-color: rgba(71, 157, 230, 0.94);
}
.create_by_apply{
    background-color: rgba(144, 214, 126, 0.94);
}
.create_by_exam{
    background-color: rgba(46, 219, 161, 0.94);
}
</style>