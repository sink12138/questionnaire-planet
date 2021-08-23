<template>
  <div class="statistics">
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
</template>

<script>
export default {
  data() {
    return {
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
  },
  methods: {
  }
}
</script>

<style>
.statistics {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
</style>