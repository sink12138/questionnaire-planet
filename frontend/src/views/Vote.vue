<template>
  <div class="normal">
    <el-container>
      <el-aside width="200px">
        <div class="editor">
          <router-link to="/">
            <div class="logo">
              <Logo></Logo>
              <div class="web-title">问卷星球</div>
            </div>
          </router-link>
          <div class="info">拖拽题目以改变顺序</div>
          <div class="editor-add">
            <el-button @click="addQuestion">新增题目</el-button>
          </div>
          <div class="editor-save">
            <el-button @click="addSubmit()">保存问卷</el-button>
          </div>
          <div class="editor-reset">
            <el-button @click="resetForm('modelForm')">重置</el-button>
          </div>
          <div class="preview">
            <el-button @click="preview()">预览</el-button>
          </div>
          <div class="publish">
            <el-button @click="publishQuestion" type="primary"
              >发布问卷</el-button
            >
            <el-dialog
              :append-to-body="true"
              title="分享问卷"
              :visible.sync="dialogVisible"
              width="30%"
              :before-close="handleClose"
              center
            >
              <div class="share">
                <div>
                  <vue-qr
                    ref="Qrcode"
                    :text="qrData.text"
                    :logoSrc="qrData.logo"
                  >
                  </vue-qr>
                </div>
                <div>
                  <el-button
                    style="margin: 10px"
                    class="tag-copy"
                    @click="copyShareLink"
                    :data-clipboard-text="qrData.text"
                  >
                    复制链接
                  </el-button>
                  <a
                    style="margin: 10px"
                    :href="exportLink"
                    @click="downloadImg"
                    :download="downloadFilename"
                  >
                    <el-button>下载二维码</el-button>
                  </a>
                </div>
              </div>
            </el-dialog>
          </div>
        </div>
      </el-aside>
      <el-main>
        <el-form
          ref="modelForm"
          :rule="rules"
          :model="modelForm"
          label-position="right"
          label-width="150px"
        >
          <div class="basic">
            <!-- 问卷题目 -->
            <el-form-item
              label="问卷题目"
              :rules="{
                required: true,
              }"
            >
              <el-input
                v-model="modelForm.title"
                style="width: 258px"
                clearable
                placeholder="请填写问卷题目"
              />
            </el-form-item>
            <!-- 问卷描述 -->
            <el-form-item label="问卷描述">
              <el-input
                v-model="modelForm.description"
                style="width: 258px"
                type="textarea"
                :autosize="{ minRows: 2, maxRows: 6 }"
                placeholder="请填写问卷描述"
              />
            </el-form-item>
            <!-- 结束语 -->
            <el-form-item label="结束语">
              <el-input
                v-model="modelForm.conclusion"
                style="width: 258px"
                clearable
                placeholder="答卷后展示"
              />
            </el-form-item>
            <!-- 问卷密码 -->
            <el-form-item label="问卷密码">
              <el-input
                v-model="modelForm.password"
                style="width: 258px"
                clearable
                placeholder="可为空"
              />
            </el-form-item>
            <!-- 问卷限额 -->
            <el-form-item
              label="问卷限额"
              :rules="{
                type: 'number',
                message: '请输入数字',
                trigger: 'blur',
              }"
            >
              <el-input
                v-model="modelForm.quota"
                style="width: 258px"
                clearable
                placeholder="若无限额请输入0"
              />
            </el-form-item>
          </div>
          <div>
            <el-collapse v-model="activeNames">
              <vuedraggable
                v-model="modelForm.questions"
                class="wrapper"
                @end="end"
              >
                <el-collapse-item
                  v-for="(item, index) in modelForm.questions"
                  :key="index"
                  :name="index"
                  class="questions"
                >
                  <template slot="title">
                    <div class="question-title">
                      第{{ index + 1 }}题,题目:{{ item.questionName }}
                    </div>
                  </template>
                  <!-- 问题类型 -->
                  <el-form-item
                    :prop="`questions.${index}.type`"
                    :label="`问题${index + 1}类型`"
                    :rules="{
                      required: true,
                      message: '请选择问题类型',
                      trigger: 'change',
                    }"
                  >
                    <el-radio-group v-model="item.type" class="question-type">
                      <el-radio label="0" border>单选题</el-radio>
                      <el-radio label="1" border>多选题</el-radio>
                      <el-radio label="2" border>填空题</el-radio>
                      <el-radio label="3" border>评分题</el-radio>
                      <el-radio label="4" border>下拉题</el-radio>
                      <el-radio label="5" border>投票题</el-radio>
                    </el-radio-group>
                  </el-form-item>
                  <!-- 是否必填 -->
                  <el-form-item
                    :prop="`questions.${index}.required`"
                    :label="`是否必填`"
                    :rules="{
                      required: true,
                      message: '请选择是否必填',
                      trigger: 'change',
                    }"
                  >
                    <el-radio-group v-model="item.required">
                      <el-radio label="true">是</el-radio>
                      <el-radio label="false">否</el-radio>
                    </el-radio-group>
                  </el-form-item>
                  <!-- 问题题目 -->
                  <el-form-item
                    :prop="`questions.${index}.questionName`"
                    label="问题"
                    :rules="{
                      required: true,
                      message: '请填写问题',
                      trigger: 'change',
                    }"
                  >
                    <el-input
                      v-model.trim="item.questionName"
                      style="width: 258px"
                      clearable
                      placeholder="请填写问题"
                    />
                  </el-form-item>
                  <!-- 问题描述 -->
                  <el-form-item
                    :prop="`questions.${index}.questionSummary`"
                    label="问题描述"
                  >
                    <el-input
                      v-model.trim="item.questionSummary"
                      style="width: 258px"
                      clearable
                      placeholder="请填写问题描述"
                    />
                  </el-form-item>
                  <el-row>
                    <!-- 最小选项 -->
                    <el-col :span="10">
                      <el-form-item
                        v-if="item.type == 1 || item.type == 5"
                        :prop="`questions.${index}.min`"
                        label="最小选项"
                        :rules="[
                          {
                            required: true,
                            message: '请填写最小选项个数',
                            trigger: 'blur',
                          },
                          { validator: isNum, trigger: 'blur' },
                        ]"
                      >
                        <el-input
                          v-model.trim="item.min"
                          style="width: 125px"
                          clearable
                          placeholder="请填写最小选项个数"
                        />
                      </el-form-item>
                    </el-col>
                    <!-- 最大选项 -->
                    <el-col :span="10">
                      <el-form-item
                        v-if="item.type == 1 || item.type == 5"
                        :prop="`questions.${index}.max`"
                        label="最大选项"
                        :rules="[
                          {
                            required: true,
                            message: '请填写最大选项个数',
                            trigger: 'blur',
                          },
                          { validator: isNum, trigger: 'blur' },
                        ]"
                      >
                        <el-input
                          v-model.trim="item.max"
                          style="width: 125px"
                          clearable
                          placeholder="请填写最大选项个数"
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row>
                    <!-- 高度 -->
                    <el-col :span="10">
                      <el-form-item
                        v-if="item.type == 2"
                        :prop="`questions.${index}.height`"
                        label="填空框高度（行）"
                        :rules="[
                          {
                            required: true,
                            message: '请填写填空框高度',
                            trigger: 'blur',
                          },
                          { validator: isNum, trigger: 'blur' },
                        ]"
                      >
                        <el-input
                          v-model.trim="item.height"
                          style="width: 125px"
                          clearable
                          placeholder="请填写填空框高度"
                        />
                      </el-form-item>
                    </el-col>
                    <!-- 宽度 -->
                    <el-col :span="10">
                      <el-form-item
                        v-if="item.type == 2"
                        :prop="`questions.${index}.width`"
                        label="宽度（px）"
                        :rules="[
                          {
                            required: true,
                            message: '请填写填空框宽度',
                            trigger: 'blur',
                          },
                          { validator: isNum, trigger: 'blur' },
                        ]"
                      >
                        <el-input
                          v-model.trim="item.width"
                          style="width: 125px"
                          clearable
                          placeholder="请填写填空框宽度"
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <!-- 答案 -->
                  <el-row v-if="item.type != 2">
                    <el-form-item
                      v-for="(opt, idx) in item.answers"
                      :key="idx"
                      :label="`选项${idx + 1}`"
                      :prop="`questions.${index}.answers.${idx}.value`"
                      :rules="[
                        {
                          required: true,
                          message: '请输入选项',
                          trigger: 'blur',
                        },
                      ]"
                    >
                      <el-input
                        v-model.trim="opt.value"
                        style="width: 200px"
                        clearable
                        placeholder="请输入选项"
                      />
                      <el-button
                        style="margin-left: 20px"
                        @click.prevent="removeDomain(index, idx)"
                        >删除</el-button
                      >
                    </el-form-item>
                  </el-row>
                  <el-row v-if="item.type == 3">
                    <el-form-item
                      v-for="(opt, idx) in item.answers"
                      :key="idx"
                      :label="`第${idx + 1}项评分`"
                      :prop="`questions.${index}.answers.${idx}.scores`"
                      :rules="[
                        {
                          required: true,
                          message: '请输入评分',
                          trigger: 'blur',
                        },
                        {
                          validator: isNum,
                          trigger: 'blur',
                        },
                      ]"
                    >
                      <el-input
                        v-model.trim="opt.scores"
                        style="width: 120px; margin-left: 10px"
                        clearable
                        placeholder="请输入评分"
                      />
                    </el-form-item>
                  </el-row>
                  <el-form-item label="编辑题目">
                    <el-button
                      icon="el-icon-circle-plus"
                      v-show="item.type != 2"
                      @click="addDomain(index)"
                      >新增选项</el-button
                    >
                    <el-button
                      icon="el-icon-s-order"
                      @click="copyQuestion(index)"
                      >复制题目</el-button
                    >
                    <el-button
                      icon="el-icon-delete-solid"
                      @click="removeQuestion(index)"
                      >删除题目</el-button
                    >
                  </el-form-item>
                </el-collapse-item>
              </vuedraggable>
            </el-collapse>
          </div>
        </el-form>
      </el-main>
    </el-container>
  </div>
</template>


<script>
import logo from "../components/svg-logo.vue";
import vuedraggable from "vuedraggable";
import VueQr from "vue-qr";
import Clipboard from "clipboard";
export default {
  name: "HelloWorld",
  components: {
    vuedraggable,
    VueQr,
    Logo: logo,
  },
  data() {
    return {
      activeNames: [0, 1],
      template: {},
      rules: {},
      templateId: 0,
      modelForm: {
        title: "新的问卷",
        description: "",
        conclusion: "",
        password: "",
        quota: 0,
        questions: [
          {
            type: "0",
            required: "",
            questionName: "",
            questionSummary: "",
            max: 2,
            min: 1,
            height: 1,
            width: 100,
            answers: [
              { value: "", scores: 0 },
              { value: "", scores: 0 },
            ],
          },
          {
            type: "0",
            required: "",
            questionName: "",
            questionSummary: "",
            max: 2,
            min: 1,
            height: 1,
            width: 100,
            answers: [
              { value: "", scores: 0 },
              { value: "", scores: 0 },
            ],
          },
        ],
      },
      qrData: {
        text: window.location.host + "/fill?templateId=" + this.templateId,
        logo: require("../assets/logo.png"),
      },
      exportLink: "",
      downloadFilename: "",
      dialogVisible: false,
    };
  },
  methods: {
    isNum: (rule, value, callback) => {
      const age = /^[0-9]*$/;
      if (!age.test(value)) {
        callback(new Error("请输入数字"));
      } else if (parseInt(value) < 1) {
        callback(new Error("请输入大于等于一的数字"));
      } else {
        callback();
      }
    },
    end() {
      this.$refs.modelForm.clearValidate();
    },
    removeDomain(index, idx) {
      // 删除选项
      if (this.modelForm.questions[index].answers.length > 2) {
        this.modelForm.questions[index].answers.splice(idx, 1);
      } else {
        this.$message("至少需要两个选项");
      }
    },
    removeQuestion(index) {
      //删除题目
      this.modelForm.questions.splice(index, 1);
      this.activeNames.pop();
    },
    copyQuestion(index) {
      //复制题目
      this.template.type = this.modelForm.questions[index].type;
      this.template.required = this.modelForm.questions[index].required;
      this.template.questionName = this.modelForm.questions[index].questionName;
      this.template.questionSummary =
        this.modelForm.questions[index].questionSummary;
      this.template.max = this.modelForm.questions[index].max;
      this.template.min = this.modelForm.questions[index].min;
      this.template.height = this.modelForm.questions[index].height;
      this.template.width = this.modelForm.questions[index].width;
      this.template.answers = this.modelForm.questions[index].answers;
      this.modelForm.questions.splice(index, 0, this.template);
      this.activeNames.push(this.modelForm.questions.length - 1);
      console.log(this.modelForm.questions);
    },
    addDomain(index) {
      // 新增选项
      this.modelForm.questions[index].answers.push({ value: "" });
    },
    addQuestion() {
      // 新增题目
      this.modelForm.questions.push({
        type: "0",
        required: "",
        questionName: "",
        questionSummary: "",
        max: 2,
        min: 1,
        height: 1,
        width: 100,
        answers: [
          { value: "", scores: 0 },
          { value: "", scores: 0 },
        ],
      });
      this.activeNames.push(this.modelForm.questions.length - 1);
    },
    resetForm(formName) {
      // 重置
      this.$refs[formName].resetFields();
    },
    addSubmit() {
      this.$refs.modelForm.validate((valid) => {
        if (valid) {
          console.log("保存中");
          console.log(this.modelForm.questions);
          var templateQuestions = [];
          var quest = new Map();
          var question = new Map();
          var x = {};
          var i = 0;
          var j = 0;
          for (i in this.modelForm.questions) {
            quest = new Map();
            question = this.modelForm.questions[i];
            console.log(question);
            quest.stem = question.questionName;
            quest.description = question.questionSummary;
            if (question.required == "false") {
              quest.required = false;
            } else {
              quest.required = true;
            }
            quest.choices = [];
            switch (question.type) {
              case "0":
                quest.type = "choice";
                for (j in question.answers) {
                  x = question.answers[j];
                  quest.choices.push(x.value);
                }
                break;
              case "1":
                quest.type = "multi-choice";
                quest.max = parseInt(question.max);
                quest.min = parseInt(question.min);
                for (j in question.answers) {
                  x = question.answers[j];
                  quest.choices.push(x.value);
                }
                break;
              case "2":
                quest.type = "filling";
                quest.height = question.height;
                quest.width = question.width;
                break;
              case "3":
                quest.type = "grade";
                quest.scores = [];
                for (j in question.answers) {
                  x = question.answers[j];
                  quest.choices.push(x.value);
                  quest.scores.push(x.scores);
                }
                break;
              case "4":
                quest.type = "dropdown";
                for (j in question.answers) {
                  x = question.answers[j];
                  quest.choices.push(x.value);
                }
                break;
              case "5":
                quest.type = "vote";
                quest.max = parseInt(question.max);
                quest.min = parseInt(question.min);
                for (j in question.answers) {
                  x = question.answers[j];
                  quest.choices.push(x.value);
                }
                break;
            }
            console.log(quest);
            templateQuestions.push(quest);
            console.log(templateQuestions);
          }
          this.$axios({
            method: "post",
            url: "http://139.224.50.146:80/apis/submit",
            data: JSON.stringify({
              templateId: parseInt(this.templateId),
              title: this.modelForm.title,
              description: this.modelForm.description,
              conclusion: this.modelForm.conclusion,
              password: this.modelForm.password,
              quota: parseInt(this.modelForm.quota),
              type: "vote",
              questions: templateQuestions,
            }),
          }).then(
            (response) => {
              console.log(response);
              if (response.data.success == true) {
                this.templateId = response.data.templateId;
                this.$message({
                  message: "问卷保存成功！",
                  type: "success",
                });
              } else {
                this.$message({
                  message: response.data.message,
                });
              }
            },
            (err) => {
              alert(err);
            }
          );
          console.log("保存成功!");
        }
      });
    },
    preview() {
      this.addSubmit();
      this.$router.push("/preview?templateId=" + this.templateId);
    },
    publishQuestion() {
      this.$refs.modelForm.validate((valid) => {
        if (valid) {
          console.log("保存中");
          console.log(this.modelForm.questions);
          var templateQuestions = [];
          var quest = new Map();
          var question = new Map();
          var x = {};
          var i = 0;
          var j = 0;
          for (i in this.modelForm.questions) {
            quest = new Map();
            question = this.modelForm.questions[i];
            console.log(question);
            quest.stem = question.questionName;
            quest.description = question.questionSummary;
            if (question.required == "false") {
              quest.required = false;
            } else {
              quest.required = true;
            }
            quest.choices = [];
            switch (question.type) {
              case "0":
                quest.type = "choice";
                for (j in question.answers) {
                  x = question.answers[j];
                  quest.choices.push(x.value);
                }
                break;
              case "1":
                quest.type = "multi-choice";
                quest.max = parseInt(question.max);
                quest.min = parseInt(question.min);
                for (j in question.answers) {
                  x = question.answers[j];
                  quest.choices.push(x.value);
                }
                break;
              case "2":
                quest.type = "filling";
                quest.height = question.height;
                quest.width = question.width;
                break;
              case "3":
                quest.type = "grade";
                quest.scores = [];
                for (j in question.answers) {
                  x = question.answers[j];
                  quest.choices.push(x.value);
                  quest.scores.push(x.scores);
                }
                break;
              case "4":
                quest.type = "dropdown";
                for (j in question.answers) {
                  x = question.answers[j];
                  quest.choices.push(x.value);
                }
                break;
              case "5":
                quest.type = "vote";
                quest.max = parseInt(question.max);
                quest.min = parseInt(question.min);
                for (j in question.answers) {
                  x = question.answers[j];
                  quest.choices.push(x.value);
                }
                break;
            }
            console.log(quest);
            templateQuestions.push(quest);
            console.log(templateQuestions);
          }
          this.$axios({
            method: "post",
            url: "http://139.224.50.146:80/apis/submit",
            data: JSON.stringify({
              templateId: this.templateId,
              title: this.modelForm.title,
              description: this.modelForm.description,
              conclusion: this.modelForm.conclusion,
              password: this.modelForm.password,
              quota: parseInt(this.modelForm.quota),
              type: "vote",
              questions: templateQuestions,
            }),
          }).then(
            (response) => {
              console.log(response);
              if (response.data.success == true) {
                this.templateId = response.data.templateId;
                this.$message({
                  message: "问卷保存成功！",
                  type: "success",
                });
                this.$axios({
                  method: "post",
                  url: "http://139.224.50.146:80/apis/release",
                  data: JSON.stringify({
                    templateId: parseInt(this.templateId),
                  }),
                }).then(
                  (response) => {
                    console.log(response);
                    if (response.data.success == true) {
                      this.$message({
                        message: "问卷发布成功！",
                        type: "success",
                      });
                      this.dialogVisible = true;
                    } else {
                      this.$message({
                        message: response.data.message,
                      });
                    }
                  },
                  (err) => {
                    alert(err);
                  }
                );
                console.log("发布成功!");
              } else {
                this.$message({
                  message: response.data.message,
                });
              }
            },
            (err) => {
              alert(err);
            }
          );
          console.log("保存成功!");
        }
      });
    },
    async copyShareLink() {
      let clipboard = new Clipboard(".tag-copy");
      console.log(clipboard);
      await clipboard.on("success", () => {
        alert("Copy Success");
        clipboard.destroy();
      });
      clipboard.on("error", () => {
        alert("Copy error");
        clipboard.destroy();
      });
    },
    downloadImg() {
      let Qrcode = this.$refs.Qrcode;
      this.exportLink = Qrcode.$el.currentSrc;
      this.downloadFilename = "Questionnaire";
    },
  },
};
</script>

<style scoped>
.el-container {
  text-align: center;
}
.el-main {
  width: 1200px;
  text-align: left;
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
.basic {
  width: 1000px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.question-title {
  font-family: 仿宋;
  font-size: 20px;
  font-weight: bolder;
}
.question-type .el-radio {
  height: 35px;
  margin: 10px;
  padding: 9px 9px 6px 6px;
}
.questions {
  width: 600px;
  margin: 0 auto;
}
.share {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
</style>