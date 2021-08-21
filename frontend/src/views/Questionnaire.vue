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
                    <router-link to="/vote/new">
                        <el-button>开始</el-button>
                    </router-link>
              </div>
          </div>
          <div class="type create_by_apply">
              <div class="inner">
                    <div class="type_show">
                        <question-pic></question-pic>
                    </div>
                    <h2 class="title">创建报名问卷</h2>
                    <h3 class="subtitle">创建限制选项选择人数的问卷</h3>
                    <router-link to="/apply/new">
                        <el-button>开始</el-button>
                    </router-link>
              </div>
          </div>
          <div class="type create_by_exam">
              <div class="inner">
                    <div class="type_show">
                        <question-pic></question-pic>
                    </div>
                    <h2 class="title">创建考试问卷</h2>
                    <h3 class="subtitle">创建规定时间自动提交的问卷</h3>
                    <router-link to="/exam/new">
                        <el-button>开始</el-button>
                    </router-link>
              </div>
          </div>
      </div>
    <router-view></router-view>
  </div>
</template>

<script>
import svg from "../components/svg-questionnaire.vue"
export default {
    components: {
        'question-pic': svg
    }
}
</script>

<style scoped>
.banner {
    position: relative;
    height: 400px;
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
    margin-top: 0px;
}
.title {
    margin-top: 10px;
    margin-bottom: 0;
}
.subtitle {
    margin-top: 0;
    margin-bottom: 38px;
}
.el-button {
    height: 50px;
    width: 120px;
    color: #ffffffdd;
    font-size: 20px;
}
.el-button:hover {
    border-color: #000000;
    color: #000000;
}
.create_by_normal,
.create_by_normal .el-button {
    background-color: rgba(46, 140, 219, 0.94);
}
.create_by_vote,
.create_by_vote .el-button {
    background-color: rgba(71, 157, 230, 0.94);
}
.create_by_apply,
.create_by_apply .el-button {
    background-color: rgba(144, 214, 126, 0.94);
}
.create_by_exam,
.create_by_exam .el-button {
    background-color: rgba(46, 219, 161, 0.94);
}
</style>