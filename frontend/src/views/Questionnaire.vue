<template>
    <div class="questionnaire">
        <Header></Header>
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
            <div class="type create_by_epide">
                <div class="inner">
                        <div class="type_show">
                            <question-pic></question-pic>
                        </div>
                        <h2 class="title">创建疫情打卡问卷</h2>
                        <h3 class="subtitle">搜集学生身体情况，同时能定位其所处位置</h3>
                        <router-link to="/epidemic/new">
                            <el-button>开始</el-button>
                        </router-link>
                </div>
            </div>
        </div>
        <router-view></router-view>
    </div>
</template>

<script>
import Header from "../components/Header.vue"
import svg from "../components/svg_questionnaire.vue"
export default {
    components: {
        Header: Header,
        'question-pic': svg,
    },
}
</script>

<style scoped>
.questionnaire {
    background-image: url("../assets/Main_bg.jpg");
    background-size: 100%;
    background-repeat: no-repeat;
    height: 100%;
}
.banner {
    position: relative;
    height: 420px;
    overflow: hidden;
    zoom: 1;
}
.type {
    float: left;
    height: 100%;
    width: 20%;
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
    background: rgba(255, 255, 255, 0.1);
    border-color: rgba(255, 255, 255, 0.66);
    color: rgba(255, 255, 255, 0.66);
    font-size: 20px;
}
.el-button:hover {
    background: rgba(184, 184, 184, 0.3);
    color: #ffffff;
}
.create_by_normal {
    background-color: rgba(46, 140, 219, 0.75);
}
.create_by_vote {
    background-color: rgba(71, 157, 230, 0.75);
}
.create_by_apply {
    background-color: rgba(46, 219, 219, 0.75);
}
.create_by_exam {
    background-color: rgba(46, 219, 161, 0.75);
}
.create_by_epide {
    background-color: rgba(144, 214, 126, 0.75);
}
</style>

<style lang="scss" scoped>
.warp {
    width: 130px * 4 - 10;
    height: 120px;
    margin: 0 auto;
    overflow: hidden;
    ul {
        list-style: none;
        padding: 0;
        margin: 0 auto;
        &.ul-item {
            display: flex;
            .li-item {
                width: 120px;
                height: 120px;
                margin-right: 10px;
                line-height: 120px;
                background-color: #999;
                color: #fff;
                text-align: center;
                font-size: 30px;
            }
        }
    }
}
</style>