<template>
  <div class="statistics">
    <el-container>
      <el-aside width="200px">
      <div class="editor">
        <router-link to="/">
          <div class="logo">
            <Logo></Logo>
            <div class="web-title">问卷星球</div>
          </div>
        </router-link>
        <div class="info">问卷数据统计</div>
        <el-button @click="changeShow('data')">查看数据</el-button>
        <el-button @click="changeShow('sum')">选项分析</el-button>
      </div>
    </el-aside>
      <el-main>
        <div class="all-data" v-show="this.show === 'data'">
          <el-table
          :data="answerData"
          style="width: 66%"
          max-height="600">
            <el-table-column
            fixed
            type="index"
            width="80">
            </el-table-column>
            <el-table-column
            v-for="(item, index) in questionList"
            :key="index"
            :label="item">
              <template slot-scope="scope">
                <span>{{ scope.row[item] }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import logo from "../components/svg-logo.vue";
import Chart from 'chart.js/auto'
Chart.defaults.font.size = 18;
export default {
  components: {
    'Logo': logo
  },
  data() {
    return {
      show: "data",
      questionList: [
        "题目1","题目2","题目3"
      ],
      answerData: [
        {
          "题目1": 'hello',
          "题目2": 'how',
          "题目3": 'are'
        },
        {
          "题目1": 'hello',
          "题目2": 'how',
          "题目3": 'are'
        },
        {
          "题目1": 'hello',
          "题目2": 'how',
          "题目3": 'are'
        },
        {
          "题目1": 'hello',
          "题目2": 'how',
          "题目3": 'are'
        },
        {
          "题目1": 'hello',
          "题目2": 'how',
          "题目3": 'are'
        },
        {
          "题目1": 'hello',
          "题目2": 'how',
          "题目3": 'are'
        },
        {
          "题目1": 'hello',
          "题目2": 'how',
          "题目3": 'are'
        },
        {
          "题目1": 'hello',
          "题目2": 'how',
          "题目3": 'are'
        },
        {
          "题目1": 'hello',
          "题目2": 'how',
          "题目3": 'are'
        },
        {
          "题目1": 'hello',
          "题目2": 'how',
          "题目3": 'are'
        },
        {
          "题目1": 'hello',
          "题目2": 'how',
          "题目3": 'are'
        },
        {
          "题目1": 'hello',
          "题目2": 'how',
          "题目3": 'are'
        },
        {
          "题目1": 'hello',
          "题目2": 'how',
          "题目3": 'are'
        },
        {
          "题目1": 'hello',
          "题目2": 'how',
          "题目3": 'are'
        },
        {
          "题目1": 'hello',
          "题目2": 'how',
          "题目3": 'are'
        },
        {
          "题目1": 'hello',
          "题目2": 'how',
          "题目3": 'are'
        },
        {
          "题目1": 'hello',
          "题目2": 'how',
          "题目3": 'are'
        },
        {
          "题目1": 'Hello',
          "题目2": 'How',
          "题目3": 'Are'
        }
      ],
      answerTimes: []
    }
  },
  created: function () {
    this.templateId = this.$route.query.templateId;
    if (this.templateId == undefined) this.templateId = 0;
    console.log(this.templateId);
    this.$axios({
      method: "get",
      url: "http://139.224.50.146:80/apis/data",
      params: { templateId: this.templateId },
    }).then((response) => {
      console.log(response);
      if (response.data.success == true) {
        this.answerData = response.data.answers;
        this.answerTimes = response.data.answerTimes
      } else {
        console.log(response.data.message);
      }
    }).catch((error) => {
      console.log(error)
    });
    this.$axios({
      method: "get",
      url: "http://139.224.50.146:80/apis/sum",
      params: { templateId: this.templateId },
    }).then((response) => {
      console.log(response);
      if (response.data.success == true) {
        this.sumData = response.data.results;
      } else {
        console.log(response.data.message);
      }
    }).catch((error) => {
      console.log(error)
    });
  },
  methods: {
    changeShow(key) {
      this.show = key
    },
    loadSumChart: function() {
      console.log(this.sumData)
      var ctx1 = document.getElementById('mySumChart');
      this.myChart = new Chart(ctx1, {
        type: 'pie',
        data: {

        }
      })
    }
  }
}
</script>

<style>
.all-data {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.editor {
  position: fixed;
  left: 0;
  top: 0;
  background-color: #f3f3f3;
  display: flex;
  width: 200px;
  height: 100%;
  flex-direction: column;
  align-items: center;
  font-family: 仿宋;
  font-size: 22px;
  font-weight: bolder;
}
.logo {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  margin-top: 40px;
  margin-bottom: 60px;
}
.web-title {
  margin-left: 15px;
  font-family: 仿宋;
  font-weight: 800;
  font-size: 26px;
  position: relative;
}
.router-link-active {
  text-decoration: none;
}
a {
  text-decoration: none;
  color: #000;
}
a:hover {
  color: rgba(46, 140, 219, 0.94);
}
.editor .el-button {
  font-family: 仿宋;
  height: 50px;
  width: 120px;
  color: #000000;
  font-size: 20px;
  font-weight: bolder;
  margin: 20px;
}
</style>