<template>
  <div class="statistics" style="height: 100%">
    <el-container>
      <el-aside width="200px">
      <div class="editor">
        <router-link to="/history">
          <div class="logo">
            <Logo></Logo>
            <div class="web-title">问卷星球</div>
          </div>
        </router-link>
        <div class="info">问卷数据统计</div>
        <el-button @click="changeShow('data')">查看数据</el-button>
        <el-button @click="changeShow('sum')">选项分析</el-button>
        <el-button @click="handleDownload()">下载数据</el-button>
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
            <el-table-column label="回答时间">
              <template slot-scope="scope">
                <span>{{ scope.row['answerTime'] }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="data-sum" v-show="this.show === 'sum'">
          <div class="chart">
            <canvas id="myChart"></canvas>
          </div>
          <el-table
          :data="sumData"
          style="width: 20%"
          max-height="600">
            <el-table-column
            fixed
            type="index"
            width="80">
            </el-table-column>
            <el-table-column
            prop="stem"
            label="题目题干">
            </el-table-column>
            <el-table-column>
              <template slot-scope="scope">
                <el-button @click="loadData(scope.row)">查看分析</el-button>
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
      questionList: [],
      answerData: [],
      sumData: [],
      answerTimes: [],
      answerList: [],
      countList: [],
      stem: '',
      type: '',
      avg: '',
      myChart: null,
      canvas: null,
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
        this.questionList = response.data.stems;
        this.answerData = response.data.answers;
        this.answerTimes = response.data.answerTimes;
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
  watch: {
    canvas: function() {
      console.log('load chart')
      this.loadChart()
    },
    answerList: {
      handler: function() {
        this.updateChart()
      },
      deep: true
    },
    countList: {
      handler: function() {
        this.updateChart()
      },
      deep: true
    }
  },
  mounted() {
    var el = document.getElementById('myChart');
    this.canvas = el
  },
  methods: {
    changeShow(key) {
      this.show = key
    },
    loadData(item) {
      this.stem = item['stem']
      this.type = item['type']
      this.answerList = item['answers']
      this.countList = item['counts']
      if (this.type == 'grade') {
        this.avg = this.item['avg']
      }
      this.updateChart()
    },
    loadChart: function() {
      console.log(this.sumData)
      var ctx1 = document.getElementById('myChart');
      this.myChart = new Chart(ctx1, {
        type: 'pie',
        data: {
          labels: [],
          datasets: [{
            label: '',
            data: [],
            backgroundColor: [
              'rgba(2, 62, 138, 1)',
              'rgba(0, 150, 199, 1)',
              'rgba(72, 202, 228, 1)',
              'rgba(144, 224, 239, 1)',
              'rgba(173, 232, 244, 1)',
              'rgba(202, 240, 248, 1)',
              'rgba(68, 108, 179, 1)',
              'rgba(52, 152, 219, 1)',
              'rgba(89, 171, 227, 1)',
              'rgba(137, 196, 244, 1)'
            ],
            hoverOffset: 4,
            borderWidth: 0
          }]
        }
      })
    },
    updateChart: function() {
      if (this.type == 'grade') {
        console.log('gggg')
        this.myChart.data.type = 'bar'
      } else {
        this.myChart.data.type = 'bar'
      }
      this.myChart.data.labels = this.answerList
      this.myChart.data.datasets[0].data = this.countList
      this.myChart.update()
    },
    handleDownload:function() {
      this.$axios({
        method: 'get',
        url: 'http://139.224.50.146:80/apis/excel',
        params: { templateId: this.templateId },
        responseType: 'blob',
      }).then(res => {
        console.log(res)
        const filename = decodeURI(res.headers['content-disposition'].split(';')[1].split('=')[1]);
        console.log(filename);
        this.download(res.data, filename)
      }).catch(err => console.log(err))
    },
    download (data, filename) {
      if (! data) {
        return
      }
      let url = window.URL.createObjectURL(new Blob([data],{ type:'application/force-download;charset=utf-8'}))
      let link = document.createElement('a')
      link.style.display = 'none'
      link.href = url
      link.download = filename
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    }
  }
}
</script>

<style scoped>
.el-container {
  height: 100%;
}
.all-data {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
}
.data-sum {
  display: flex;
  flex-direction: row;
  align-items: center;
}
.chart {
  height: 500px;
  width: 500px;
  position: relative;
  top: 100px;
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