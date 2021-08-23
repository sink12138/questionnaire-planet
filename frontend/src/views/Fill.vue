<template>
  <div id="quest" ref="quest">
    <el-dialog
      title="哎呀，问卷被加密了，请输入问卷密码！"
      :visible.sync="dialogFormVisible"
      style="text-align: left; width: 1050px; margin: auto"
      :close-on-click-modal="false"
      :before-close="goBack"
    >
      <el-form :model="password">
        <el-form-item
          label="问卷密码"
          :label-width="formLabelWidth"
          prop="password"
        >
          <el-input
            v-model="password"
            autocomplete="off"
            show-password
            style="width: 300px"
            placeholder="请输入问卷密码"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="goBack">返回首页</el-button>
        <el-button type="primary" @click="unlock">确认</el-button>
      </div>
    </el-dialog>

    <div class="head">
      <h1>
        {{ title }}
      </h1>
      <h3>
        {{ description }}
      </h3>
      <h3>问卷剩余{{ remain }}份</h3>
    </div>
    <div class="question" v-if="submitted == false">
      <el-form
        :model="answers"
        :rules="rules"
        ref="ruleForm"
        label-width="100px"
        class="ruleForm"
      >
        <div v-for="(item, index_question) in questions" :key="index_question">
          <el-divider content-position="left" style="margin-top: 15px"
            >第{{ index_question + 1 }}题</el-divider
          >
          <div class="question-title">
            <div class="stem">{{ item.stem }}</div>
            <div class="description">{{ item.description }}</div>
          </div>

          <div class="question-content">
            <div v-if="item.type == 'choice'">
              <el-form-item
                label="选项"
                :rules="{
                  required: item.required,
                }"
              >
                <el-radio-group
                  v-model="answers[index_question]"
                  v-for="(i, index) in item.choices"
                  :key="index"
                  @change="changeValue"
                >
                  <el-radio class="option" :label="index">{{ i }}</el-radio>
                </el-radio-group></el-form-item
              >
            </div>
            <div class="multi" v-if="item.type == 'multi-choice'">
              至少选择{{ item.min }}项
              <el-form-item
                label="选项"
                :rules="{
                  required: item.required,
                }"
              >
                <el-checkbox-group
                  v-model="multi"
                  v-for="(i, index) in item.choices"
                  :min="0"
                  :max="item.max"
                  :key="index"
                  @change="multiChangeValue(index_question)"
                >
                  <el-checkbox class="option" :label="index" border>{{
                    i
                  }}</el-checkbox>
                </el-checkbox-group></el-form-item
              >
            </div>
            <div v-if="item.type == 'filling'">
              <el-form-item
                label="请输入"
                :rules="{
                  required: item.required,
                }"
              >
                <el-input
                  type="textarea"
                  class="input"
                  :rows="item.height"
                  :style="{ '--width': item.width }"
                  placeholder="请输入内容"
                  v-model="answers[index_question]"
                >
                </el-input
              ></el-form-item>
            </div>
            <div v-if="item.type == 'grade'">
              <el-form-item
                label="选项"
                :rules="{
                  required: item.required,
                }"
              >
                <el-radio-group
                  v-model="answers[index_question]"
                  v-for="(i, index) in item.choices"
                  :key="index"
                  @change="changeValue"
                >
                  <el-radio class="option" :label="index"
                    >{{ i }}({{ item.scores[index] }})</el-radio
                  >
                </el-radio-group></el-form-item
              >
            </div>
            <div v-if="item.type == 'dropdown'">
              <el-form-item
                label="选项"
                :rules="{
                  required: item.required,
                }"
              >
                <el-select
                  v-model="answers[index_question]"
                  clearable
                  placeholder="请选择"
                >
                  <el-option
                    v-for="(i, index) in item.choices"
                    :key="index"
                    :label="i"
                    :value="index"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </div>
            <div class="multi" v-if="item.type == 'vote'">
              至少选择{{ item.min }}项
              <el-form-item
                label="选项"
                :rules="{
                  required: item.required,
                }"
              >
                <el-checkbox-group
                  v-model="multi"
                  v-for="(i, index) in item.choices"
                  :min="0"
                  :max="item.max"
                  :key="index"
                  @change="multiChangeValue(index_question)"
                >
                  <el-checkbox class="option" :label="index" border>{{
                    i
                  }}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
            </div>
          </div>
        </div>
      </el-form>
    </div>
    <div v-if="submitted == true">
      <h3>{{ conclusion }}</h3>
      
    </div>
    <div class="submit">
      <el-button @click="submit()" v-if="submitted == false"
        >提交问卷</el-button
      >
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      templateId: 0,
      submitted: false,
      locked: false,
      title: "问卷标题",
      type: "normal",
      description: "问卷描述",
      conclusion: "谢谢",
      remain: "无限制",
      password: "",
      dialogFormVisible: false,
      formLabelWidth: "100px",
      results: [{stem:"题干",answers:['A','B'],counts:[12,25]}],
      questions: [
        {
          type: "choice",
          stem: "这题什么意思？",
          description: "题目描述，题目描述，描述一下题目",
          required: true,
          choices: ["生异形", "生瓜蛋子", "What's up?", "萨日朗"],
        },
        {
          type: "choice",
          stem: "你的姓名？",
          description: "",
          required: true,
          choices: ["华强", "大鹏", "有一个人"],
        },
        {
          type: "multi-choice",
          stem: "哪些词形容你合适？",
          description: "请用恰当的词来形容你",
          required: true,
          choices: ["沉鱼落雁", "玉树临风", "惊天动地"],
          max: 2,
          min: 0,
        },
        {
          type: "filling",
          stem: "华强买的瓜多少钱一斤？",
          description: "华强买瓜多少钱一斤？",
          required: true,
          height: 3,
          width: "600px",
        },
        {
          type: "grade",
          stem: "瓜店老板态度怎样？",
          description: "给态度打分",
          required: true,
          choices: ["good", "very good", "very very good"],
          scores: [10, 50, 100],
        },
        {
          type: "dropdown",
          stem: "瓜是什么做的?",
          description: "",
          required: false,
          choices: ["(C2H5O)n", "Au", "Fe"],
        },
      ],
      multi: [],
      answers: [],
    };
  },
  created: function () {
    this.templateId = this.$route.query.templateId;
    if (this.templateId == undefined) this.templateId = 0;
    console.log(this.templateId);
    this.$axios({
      method: "get",
      url: "http://139.224.50.146:80/apis/attempt",
      params: { templateId: this.templateId },
    })
      .then((response) => {
        console.log(response);
        if (response.data.success == true) {
          this.locked = response.data.locked;
          if (this.locked == true) {
            console.log(22);
            this.dialogFormVisible = true;
          } else {
            console.log(33);
            this.$axios({
              method: "get",
              url: "http://139.224.50.146:80/apis/details",
              params: { templateId: this.templateId },
            })
              .then((response) => {
                console.log(response);
                if (response.data.success == true) {
                  this.title = response.data.title;
                  this.type = response.data.type;
                  this.description = response.data.description;
                  this.questions = response.data.questions;
                  this.dialogFormVisible = false;
                  if (response.data.remain != undefined) {
                    this.remain = response.data.remain;
                  }
                } else {
                  console.log(response.data.message);
                }
              })
              .catch((err) => console.log(err));
          }
        } else {
          console.log(response.data.message);
        }
      })
      .catch((err) => console.log(err));
  },
  mounted: function () {},
  methods: {
    step: function (i) {
      return "step" + i;
    },
    goBack() {
      this.$router.push("/");
    },
    unlock() {
      this.$axios({
        method: "get",
        url: "http://139.224.50.146:80/apis/details",
        params: { templateId: this.templateId, password: this.password },
      })
        .then((response) => {
          console.log(response);
          if (response.data.success == true) {
            if (response.data.password == this.password) {
              this.title = response.data.title;
              this.type = response.data.type;
              this.description = response.data.description;
              this.questions = response.data.questions;
              this.dialogFormVisible = false;
            } else {
              alert("问卷密码错误！");
            }
          } else {
            console.log(response.data.message);
          }
        })
        .catch((err) => console.log(err));
    },
    exportQuest() {
      this.$PDFSave(this.$refs.quest, this.title);
    },
    changeValue() {
      console.log(this.answers);
    },
    multiChangeValue(index) {
      this.answers[index] = this.multi;
      console.log(this.multi);
    },
    submit() {
      this.$confirm("是否提交问卷?", "提示", {
        confirmButtonText: "提交",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$refs["ruleForm"].validate((valid) => {
            if (valid) {
              let submitData = JSON.stringify({
                templateId: parseInt(this.templateId),
                password: this.password,
                answers: this.answers,
              });
              console.log(submitData);
              this.$axios({
                method: "post",
                url: "http://139.224.50.146:80/apis/answer",
                data: submitData,
              }).then(
                (response) => {
                  console.log(response);
                  if (response.data.success == true) {
                    this.submitted = true;
                    if (response.data.conclusion == undefined) {
                      this.conclusion = "感谢您的提交!";
                    } else {
                      this.conclusion = response.data.conclusion;
                    }
                    this.results = response.data.results;
                  } else {
                    this.$message({
                      message: response.data.message,
                      type: "info",
                    });
                  }
                },
                (err) => {
                  alert(err);
                }
              );
            } else {
              console.log("error submit!!");
              return false;
            }
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消提交",
          });
        });
    },
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
  width: var(--width);
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