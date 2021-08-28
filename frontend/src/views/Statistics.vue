<template>
  <div class="statistics">
    <Header class="header"></Header>
    <div class="editor">
      <Button class="new" icon="md-arrow-round-back">所有问卷</Button>
      <el-button icon="el-icon-s-order" @click="changeShow('data')">查看数据</el-button>
      <el-button icon="el-icon-s-data" @click="changeShow('sum')">选项分析</el-button>
      <el-button icon="el-icon-s-data" @click="changeShow('xy')">交叉分析</el-button>
      <el-button icon="el-icon-download" @click="handleDownload()">下载数据</el-button>
    </div>
    <div class="fill">
      <div class="main">
        <div class="all-data" v-show="this.show === 'data'">
          <el-table
          :data="answerData"
          border
          :header-cell-style="{'text-align':'center',background:'#eee',color:'#606266','border-color':'#bbb'}"
          max-height="650px"
          >
            <el-table-column
            fixed
            label="回答序号"
            type="index"
            width="80">
            </el-table-column>
            <el-table-column label="题目">
              <el-table-column
              v-for="(item, index) in questionList"
              :key="index"
              :label="item">
                <template slot-scope="scope">
                  <span>{{ scope.row[item] }}</span>
                </template>
              </el-table-column>
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
          border
          :header-cell-style="{'text-align':'center',background:'#eee',color:'#606266','border-color':'#bbb'}"
          max-height="650px">
            <el-table-column
            label="题目序号"
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
        <div class="cross-over" v-show="this.show === 'xy'">
          <el-table
          :data="xyData">
            <el-table-column
            prop="choicex"
            label="x">
            </el-table-column>
            <el-table-column
            v-for="(item, index) in choicey"
            :key="index"
            :label="item">
              <template slot-scope="scope">
                <span>{{ scope.row['xy'][index] }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Header from "../components/Header.vue";
import Chart from 'chart.js/auto'
Chart.defaults.font.size = 18;
export default {
  components: {
    Header: Header
  },
  data() {
    return {
      show: "data",
      questionList: ['q1','q2','q3'],
      answerData: [
      {
        answerId: 12,
        'q1': 'a1',
        'q2': 'a2',
        'q3': 'a3',
        answerTime: 'time111'
      },
      {
        answerId: 13,
        'q1': 'a21',
        'q2': 'a22',
        'q3': 'a23',
        answerTime: 'time222'
      },
      ],
      sumData: [
        {
          stem: 'q1',
          answers: ['a1','a2','A3'],
          counts: [2,4,6]
        },
        {
          stem: 'q2',
          answers: ['a1','a2','A3'],
          counts: [6,9,9]
        },
        {
          stem: 'q1',
          answers: ['a1','a2','A3'],
          counts: [2,4,6]
        },
        {
          stem: 'q1',
          answers: ['a1','a2','A3'],
          counts: [2,4,6]
        },
        {
          stem: 'q1',
          answers: ['a1','a2','A3'],
          counts: [2,4,6]
        },
        {
          stem: 'q1',
          answers: ['a1','a2','A3'],
          counts: [2,4,6]
        },
        {
          stem: 'q1',
          answers: ['a1','a2','A3'],
          counts: [2,4,6]
        },
        {
          stem: 'q1',
          answers: ['a1','a2','A3'],
          counts: [2,4,6]
        },
        {
          stem: 'q1',
          answers: ['a1','a2','A3'],
          counts: [2,4,6]
        },
      ],
      choicey:['y1','y2','y3'],
      xyData: [
        {
          choicex: 'x1',
          "xy": ['n11','n12','n13']
        },
        {
          choicex: 'x2',
          "xy": ['n21','n22','n23']
        }
      ],
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
      this.loadData(this.sumData[0])
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
.statistics {
  display: grid;
  grid-template-columns: 120px auto;
  grid-template-rows: 60px auto;
  height: 100%;
}
.header {
  grid-column-start: 1;
  grid-column-end: 3;
  grid-row-start: 1;
  grid-row-end: 2;
}
.editor {
  grid-column-start: 1;
  grid-column-end: 2;
  grid-row-start: 2;
  grid-row-end: 3;
  display: flex;
  flex-direction: column;
  width: 120px;
  height: 100%;
}
.fill {
  grid-column-start: 2;
  grid-column-end: 3;
  grid-row-start: 2;
  grid-row-end: 3;
  text-align: left;
  overflow: hidden;
  background-image: url("../assets/Statistics_bg.jpg");
  background-size: 100%;
  background-repeat: no-repeat;
}
.main {
  height: 100%;
  width: 100%;
  background: #fff;
  opacity: 0.94;
}
.all-data {
  margin-left: 60px;
}
.data-sum {
  display: flex;
  flex-direction: row;
  align-items: center;
}
.chart {
  height: 400px;
  width: 400px;
}
.editor .el-button {
  border: #fff;
  font-size: 16px;
  margin: 0;
  padding-top: 20px;
  padding-bottom: 20px;
}
.ivu-btn {
  height: 50px;
  width: 100%;
  background: rgb(0, 183, 255);
  color: #ffffff;
  font-size: 15px;
  border: #fff;
  margin: 0;
}
</style>