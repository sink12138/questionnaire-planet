<template>
    <div id="quest" ref="quest">
    <div class="head">
        <h1>
        {{ title }}
        </h1>
        <h3>
        {{ description }}
        </h3>
    </div>
    <div class="question">
        <div v-for="(item, index_question) in questions" :key="index_question">
        <el-divider content-position="left" style="margin-top: 15px">第{{ index_question + 1 }}题</el-divider>
        <div class="question-title">
            <div class="stem">{{ item.stem }}</div>
            <div class="description">{{ item.description }}</div>
        </div>
        <div class="question-content">
            <div v-if="item.type == 'choice'">
            <el-radio-group v-model="answers[index_question]" v-for="(i, index) in item.choices" :key="index" @change="changeValue">
                <el-radio class="option" :label="index">{{ i }}</el-radio>
            </el-radio-group>
            </div>
            <div class="multi" v-if="item.type == 'multi-choice'">
            <el-checkbox-group
            v-model="multi" 
            v-for="(i, index) in item.choices"
            :min = "item.min"
            :max = "item.max"
            :key="index" 
            @change="multiChangeValue(index_question)">
                <el-checkbox class="option" :label="index" border>{{ i }}</el-checkbox>
            </el-checkbox-group>
            </div>
            <div v-if="item.type == 'filling'">
            <el-input
            type="textarea"
            class="input"
            :rows="item.height"
            :style="{'--width': item.width}"
            placeholder="请输入内容"
            v-model="answers[index_question]">
            </el-input>
            </div>
            <div v-if="item.type == 'grade'">
            <el-radio-group v-model="answers[index_question]" v-for="(i, index) in item.choices" :key="index" @change="changeValue">
                <el-radio class="option" :label="index">{{ i }}({{item.scores[ index ]}})</el-radio>
            </el-radio-group>
            </div>
            <div v-if="item.type == 'dropdown'">
            <el-select v-model="answers[index_question]" clearable placeholder="请选择">
                <el-option
                v-for="(i, index) in item.choices"
                :key="index"
                :label="i"
                :value="index">
                </el-option>
            </el-select>
            </div>
        </div>
        </div>
    </div>
    <div class="submit">
        <el-button @click="submit">提交问卷</el-button>
    </div>
    </div>
</template>

<script>
export default {
  data() {
    return {
      templateId: 0,
      locked: false,
      title: "问卷标题",
      type: "normal",
      description: "问卷描述",
      password: "",
      questions: [
        {
          type: "choice",
          stem: "这题什么意思？",
          description: "题目描述，题目描述，描述一下题目",
          required: true,
          choices: [
            "生异形","生瓜蛋子","What's up?","萨日朗"
          ],
        },
        {
          type: "choice",
          stem: "你的姓名？",
          description: "",
          required: true,
          choices: [
            "华强","大鹏","有一个人"
          ],
        },
        {
          type: "multi-choice",
          stem: "哪些词形容你合适？",
          description: "请用恰当的词来形容你",
          required: true,
          choices: [
            "沉鱼落雁","玉树临风","惊天动地"
          ],
          max: 2,
          min: 0,
        },
        {
          type: "filling",
          stem: "华强买的瓜多少钱一斤？",
          description: "华强买瓜多少钱一斤？",
          required: true,
          height: 3,
          width: '600px',
        },
        {
          type: "grade",
          stem: "瓜店老板态度怎样？",
          description: "给态度打分",
          required: true,
          choices: [
            "good","very good","very very good"
          ],
          scores: [
            10, 50, 100
          ]
        },
        {
          type: "dropdown",
          stem: "瓜是什么做的?",
          description: "",
          required: true,
          choices: [
            "(C2H5O)n","Au","Fe"
          ]
        }
      ],
      multi: [],
      answers: []
    };
  },
  created: function () {
    this.templateId = this.$route.query.templateId;
    if (this.templateId == undefined) this.templateId = 0;
    console.log(this.templateId);
    this.$axios({
      method: "get",
      url: "http://139.224.50.146:80/apis/locked",
      params: { templateId: this.templateId },
    })
      .then((response) => {
        console.log(response);
        if (response.data.success == true) {
          this.locked = response.data.locked;
        } else {
          console.log(response.data.message);
        }
      })
      .catch((err) => console.log(err));
    this.$axios({
      method: "get",
      url: "http://139.224.50.146:80/apis/details",
      params: { templateId: this.templateId, password: "" },
    })
      .then((response) => {
        console.log(response);
        if (response.data.success == true) {
          this.title = response.data.title;
          this.type = response.data.type;
          this.description = response.data.description;
          this.password = response.data.password;
          this.questions = response.data.questions;
        } else {
          console.log(response.data.message);
        }
      })
      .catch((err) => console.log(err));
  },
  methods: {
    exportQuest() {
      this.$PDFSave(this.$refs.quest, this.title);
    },
    changeValue() {
      console.log(this.answers)
    },
    multiChangeValue(index) {
      this.answers[index] = this.multi
      console.log(this.multi)
    },
    submit() {
        this.$confirm('是否提交问卷?', '提示', {
          confirmButtonText: '提交',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let submitData = JSON.stringify({
            templateId: parseInt(this.templateId),
            password: this.password,
            answers: this.answers
          })
          console.log(submitData)
          this.$axios({
              method: "post",
              url: "http://139.224.50.146:80/apis/answer",
              data: submitData
          }).then( 
            (response) => {
              console.log(response);
              if (response.data.success == true) {
                this.$message({
                  message: "提交成功！",
                  type: "success",
                });
              } else {
                this.$message({
                  message: response.data.message,
                  type: "info"
                });
              }
            },
            (err) => {
              alert(err);
            }
          )
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消提交'
          });          
        });
    }
  },
};
</script>

<style scoped>
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
  font-size: 18px;
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
.export {
  position: fixed;
  bottom: 60px;
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
.question {
  margin: 0 auto;
  width: 800px;
  display: flex;
  flex-direction: column;
  text-align: left;
}
.stem,
.description {
  margin: 20px;
  font-family: 微软雅黑;
}
.stem {
  font-size: 26px;
  font-weight: bolder;
}
.description {
  font-size: 18px;
}
.question-content {
  margin: 25px;
}
.question-content .input {
  width: var(--width)
}
.multi {
  display: flex;
  flex-direction: row;
}
.option {
  margin-left: 10px;
  margin-right: 30px;
}
</style>