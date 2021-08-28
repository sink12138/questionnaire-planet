<template>
  <div class="statistics">
    <Header class="header"></Header>
    <div class="editor">
      <Button class="new" icon="md-arrow-round-back">所有问卷</Button>
      <el-button 
      :style="{'background-color': setColor('data')}" 
      icon="el-icon-tickets" 
      @click="changeShow('data')">
        查看数据
      </el-button>
      <el-button 
      :style="{'background-color': setColor('sum')}" 
      icon="el-icon-s-data" 
      @click="changeShow('sum')">
        选项分析
      </el-button>
      <el-button 
      :style="{'background-color': setColor('xy')}" 
      icon="el-icon-rank" 
      @click="changeShow('xy')">
        交叉分析
      </el-button>
      <el-button 
      icon="el-icon-download" 
      @click="handleDownload()">
        下载数据
      </el-button>
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
          <div>{{this.sum(countList)}}</div>
          <RadioGroup v-model="chartShow" type="button">
              <Radio label="pie">Pie</Radio>
              <Radio label="bar">Bar</Radio>
              <Radio label="line">Line</Radio>
          </RadioGroup>
          <div class="chart">
            <canvas v-show="this.chartShow === 'pie'" id="myPieChart"></canvas>
            <canvas v-show="this.chartShow === 'bar'" id="myBarChart" style="height: 300px"></canvas>
            <canvas v-show="this.chartShow === 'line'" id="myLineChart" style="height: 300px"></canvas>
          </div>
          <div class="sum-table" style="width: 480px">
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
              <el-table-column width="120">
                <template slot-scope="scope">
                  <el-button @click="loadData(scope.row)">查看分析</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
        <div class="cross-over" v-show="this.show === 'xy'">
          <el-select v-model="x" placeholder="x">
            <el-option
            v-for="(item, index) in questionList"
            :key="index"
            :label="item"
            :value="index">
            </el-option>
          </el-select>
          <el-select v-model="y" placeholder="y">
            <el-option
            v-for="(item, index) in questionList"
            :key="index"
            :label="item"
            :value="index">
            </el-option>
          </el-select>
          <el-button @click="cross()">开始分析</el-button>
          <el-table
          :data="xyData"
          border
          :header-cell-style="{'text-align':'center',background:'#eee',color:'#606266','border-color':'#bbb'}">
            <el-table-column
            prop="choiceX"
            label="选项">
            </el-table-column>
            <el-table-column
            v-for="(item, index) in choicesy"
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
      chartShow: "pie",
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
        }
      ],
      x: 0,
      y: 0,
      choicesy:['y1','y2','y3'],
      xyData: [
        {
          choiceX: 'x1',
          "xy": ['n11','n12','n13']
        },
        {
          choiceX: 'x2',
          "xy": ['n21','n22','n23']
        }
      ],
      answerList: [],
      countList: [1,2,3],
      stem: '',
      type: '',
      avg: '',
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
    var el = document.getElementById('myPieChart');
    this.canvas = el
    this.loadData(this.sumData[0])
  },
  methods: {
    changeShow(key) {
      this.show = key
    },
    setColor(key) {
      if (key == this.show) return '#ddd'
      else return '#fff'
    },
    sum(list) {
      var s = 0;
      var i = 0;
      for (i in list) {
        s += list[i]
      }
      console.log(s)
      return s
    },
    cross() {
      console.log(this.x,this.y)
      this.$axios({
        method: "get",
        url: "http://139.224.50.146:80/apis/cross",
        data: JSON.stringify({
          templateId: parseInt(this.templateId),
          indexX: parseInt(this.x),
          indexY: parseInt(this.y),
        }),
      }).then((response) => {
        console.log(response);
        if (response.data.success == true) {
          this.choicesy = response.data.choicesY;
          this.xyData = response.data.xyData;
        } else {
          console.log(response.data.message);
        }
      }).catch((error) => {
        console.log(error)
      });
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
    loadChart() {
      console.log(this.sumData)
      var ctx1 = document.getElementById('myPieChart');
      this.myPieChart = new Chart(ctx1,{
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
      var ctx2 = document.getElementById('myBarChart');
      this.myBarChart = new Chart(ctx2,{
        type: 'bar',
        data: {
            labels: [],
            datasets: [{
              label: "柱状图",
              backgroundColor: "rgb(0, 150, 199)",
              data: []
            }]
        }
      })
      var ctx3 = document.getElementById('myLineChart');
      this.myLineChart = new Chart(ctx3,{
        type: 'line',
        data: {
            labels: [],
            datasets: [{
              label: "折线图",
              data: []
            }]
        }
      })
    },
    updateChart() {
      console.log('uuuppp')
      this.myPieChart.data.labels = this.answerList
      this.myPieChart.data.datasets[0].data = this.countList
      this.myPieChart.update()
      this.myBarChart.data.labels = this.answerList
      this.myBarChart.data.datasets[0].data = this.countList
      this.myBarChart.update()
      this.myLineChart.data.labels = this.answerList
      this.myLineChart.data.datasets[0].data = this.countList
      this.myLineChart.update()
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
  padding: 36px;
}
.data-sum {
  padding: 36px;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
}
.cross-over {
  padding: 36px;
}
.chart {
  height: 400px;
  width: 400px;
}
.editor .el-button {
  border: #fff;
  color: #3f3f3f;
  font-size: 16px;
  margin: 0;
  padding-top: 21px;
  padding-bottom: 20px;
}
.ivu-btn {
  height: 56px;
  width: 100%;
  background: rgb(0, 183, 255);
  color: #ffffff;
  font-size: 15px;
  border: #fff;
  margin: 0;
}
</style>