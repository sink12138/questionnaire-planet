<template>
  <div class="normal">
    <el-container>
      <el-aside width="200px">
        <div class="editor">
          <router-link to="/questionnaire">
            <div class="logo">
              <Logo></Logo>
              <div class="web-title">问卷星球</div>
            </div>
          </router-link>
          <div class="info">拖拽题目以改变顺序</div>
          <el-menu @open="handleOpen" @close="handleClose">
            <el-submenu index="1">
              <template slot="title">
                <div class="editor-add"><el-button type="text">新增题目</el-button></div>
              </template>
              <el-menu-item-group>
                <el-menu-item index="1-1"
                  ><el-button @click="addQuestion(0)" type="text"
                    >单选题</el-button
                  ></el-menu-item
                >
                <el-menu-item index="1-2"
                  ><el-button @click="addQuestion(1)" type="text"
                    >多选题</el-button
                  ></el-menu-item
                >
                <el-menu-item index="1-3"
                  ><el-button @click="addQuestion(2)" type="text"
                    >填空题</el-button
                  ></el-menu-item
                >
                <el-menu-item index="1-4"
                  ><el-button @click="addQuestion(3)" type="text"
                    >评分题</el-button
                  ></el-menu-item
                >
                <el-menu-item index="1-5"
                  ><el-button @click="addQuestion(4)" type="text"
                    >下拉题</el-button
                  ></el-menu-item
                >
              </el-menu-item-group>
            </el-submenu>
            <el-menu-item index="2">
              <div class="editor-save">
                <el-button @click="addSubmit()" type="text">保存问卷</el-button>
              </div>
            </el-menu-item>
            <el-menu-item index="3" >
              <div class="editor-reset">
                <el-button @click="resetForm('modelForm')" type="text">重置</el-button>
              </div>
            </el-menu-item>
            <el-menu-item index="4">
              <div class="preview">
                <el-button @click="preview()" type="text">预览</el-button>
              </div>
            </el-menu-item>
            <el-menu-item index="5">
              <div class="publish">
                <el-button @click="publishQuestion" type="text"
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
            </el-menu-item>
          </el-menu>
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
            <!-- 显示题号 -->
            <el-form-item label="是否显示题号">
              <el-switch
                v-model="modelForm.showIndex"
                active-color="#13ce66"
                inactive-color="#ff4949"
              >
              </el-switch>
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
                placeholder="设置后需要密码才可回答"
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
                placeholder="收集指定数量后停止回收"
              />
            </el-form-item>
            <!-- 发布时间 -->
            <el-form-item label="自动发布时间">
              <el-date-picker
                v-model="modelForm.startTime"
                value-format="yyyy-MM-dd HH:mm:00"
                format="yyyy-MM-dd HH:mm"
                type="datetime"
                placeholder="选择日期时间"
                align="right"
                :picker-options="pickerOptions"
              >
              </el-date-picker>
            </el-form-item>
            <!-- 回收时间 -->
            <el-form-item label="自动回收时间">
              <el-date-picker
                v-model="modelForm.endTime"
                value-format="yyyy-MM-dd HH:mm:00"
                format="yyyy-MM-dd HH:mm"
                type="datetime"
                placeholder="选择日期时间"
                align="right"
                :picker-options="pickerOptions"
              >
              </el-date-picker>
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
                    <div class="question-index" v-show="modelForm.showIndex">
                      第{{ index + 1 }}题
                    </div>
                    <div class="question-title">
                      题目:{{ item.questionName }}
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
                    <el-switch
                      v-model="item.required"
                      active-color="#13ce66"
                      inactive-color="#ff4949"
                    >
                    </el-switch>
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
                      v-model="item.questionName"
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
                      v-model="item.questionSummary"
                      style="width: 258px"
                      clearable
                      placeholder="请填写问题描述"
                    />
                  </el-form-item>
                  <el-row>
                    <!-- 最小选项 -->
                    <el-col :span="10">
                      <el-form-item
                        v-if="item.type == 1"
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
                        v-if="item.type == 1"
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
                          v-model="item.max"
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
                          v-model="item.height"
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
                          v-model="item.width"
                          style="width: 125px"
                          clearable
                          placeholder="请填写填空框宽度"
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <!-- 答案 -->
                  <el-row v-if="item.type != 2 && item.type != 3">
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
                        v-model="opt.value"
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
                      v-for="(opt, idx) in item.grades"
                      :key="idx"
                      :label="`第${idx + 1}级评分`"
                      :prop="`questions.${index}.grades.${idx}`"
                      :rules="[
                        {
                          required: true,
                          message: '请输入评分',
                          trigger: 'blur',
                        },
                      ]"
                    >
                      <el-input
                        v-model="item.grades[idx]"
                        style="width: 120px; margin-left: 10px"
                        clearable
                        placeholder="请输入评分"
                      />
                    </el-form-item>
                  </el-row>
                  <el-form-item label="编辑题目">
                    <el-button
                      icon="el-icon-circle-plus"
                      v-show="item.type != 2 && item.type != 3"
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
import logo from "../components/svg_logo.vue";
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
      pickerOptions: {
        shortcuts: [
          {
            text: "今天",
            onClick(picker) {
              picker.$emit("pick", new Date());
            },
          },
          {
            text: "明天",
            onClick(picker) {
              const date = new Date();
              date.setTime(date.getTime() + 3600 * 1000 * 24);
              picker.$emit("pick", date);
            },
          },
          {
            text: "一周后",
            onClick(picker) {
              const date = new Date();
              date.setTime(date.getTime() + 3600 * 1000 * 24 * 7);
              picker.$emit("pick", date);
            },
          },
        ],
      },
      activeNames: [0, 1],
      template: {},
      rules: {},
      templateId: 0,
      code: "",
      modelForm: {
        title: "新的问卷",
        description: "",
        conclusion: "",
        showIndex: true,
        password: "",
        quota: undefined,
        startTime: "",
        endTime: "",
        questions: [
          {
            type: "0",
            required: true,
            questionName: "",
            questionSummary: "",
            max: 2,
            min: 1,
            height: 1,
            width: 800,
            grades: ["非常不满意", "不满意", "一般", "满意", "非常满意"],
            answers: [{ value: "" }, { value: "" }],
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
      this.template = {
        type: "0",
        required: true,
        questionName: "",
        questionSummary: "",
        max: 2,
        min: 1,
        height: 1,
        width: 800,
        grades: [],
        answers: [],
      };
      this.template.type = this.modelForm.questions[index].type;
      this.template.required = this.modelForm.questions[index].required;
      this.template.questionName = this.modelForm.questions[index].questionName;
      this.template.questionSummary =
        this.modelForm.questions[index].questionSummary;
      this.template.max = this.modelForm.questions[index].max;
      this.template.min = this.modelForm.questions[index].min;
      this.template.height = this.modelForm.questions[index].height;
      this.template.width = this.modelForm.questions[index].width;
      var i = 0;
      for (i in this.modelForm.questions[index].answers) {
        this.template.answers.push({
          value: this.modelForm.questions[index].answers[i].value,
          scores: this.modelForm.questions[index].answers[i].scores,
        });
      }
      i = 0;
      for (i in this.modelForm.questions[index].grades) {
        this.template.grades.push(this.modelForm.questions[index].grades[i]);
      }
      this.modelForm.questions.splice(index + 1, 0, this.template);
      this.activeNames.push(this.modelForm.questions.length - 1);
      console.log(this.modelForm.questions);
    },
    addDomain(index) {
      // 新增选项
      this.modelForm.questions[index].answers.push({ value: "" });
    },
    addQuestion(index) {
      // 新增题目
      this.modelForm.questions.push({
        type: index.toString(),
        required: true,
        questionName: "",
        questionSummary: "",
        max: 2,
        min: 1,
        height: 1,
        width: 800,
        grades: ["非常不满意", "不满意", "一般", "满意", "非常满意"],
        answers: [{ value: "" }, { value: "" }],
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
            quest.required = question.required;
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
                if (quest.max < quest.min) {
                  var mes =
                    "第" + (parseInt(i) + 1) + "题最小选项数大于最大选项数！";
                  this.$message({
                    message: mes,
                    type: "warning",
                  });
                  return;
                }
                for (j in question.answers) {
                  x = question.answers[j];
                  quest.choices.push(x.value);
                }
                break;
              case "2":
                quest.type = "filling";
                quest.height = parseInt(question.height);
                quest.width = parseInt(question.width);
                break;
              case "3":
                quest.type = "grade";
                quest.grades = [];
                for (j in question.grades) {
                  quest.grades.push(question.grades[j]);
                }
                break;
              case "4":
                quest.type = "dropdown";
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
              showIndex: this.modelForm.showIndex,
              password: this.modelForm.password,
              startTime: this.modelForm.startTime,
              endTime: this.modelForm.endTime,
              quota:
                this.modelForm.quota == undefined
                  ? 0
                  : parseInt(this.modelForm.quota),
              type: "normal",
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
            quest.required = question.required;
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
                if (quest.max < quest.min) {
                  var mes =
                    "第" + (parseInt(i) + 1) + "题最小选项数大于最大选项数！";
                  this.$message({
                    message: mes,
                    type: "warning",
                  });
                  return;
                }
                for (j in question.answers) {
                  x = question.answers[j];
                  quest.choices.push(x.value);
                }
                break;
              case "2":
                quest.type = "filling";
                quest.height = parseInt(question.height);
                quest.width = parseInt(question.width);
                break;
              case "3":
                quest.type = "grade";
                quest.grades = [];
                for (j in question.grades) {
                  quest.grades.push(question.grades[j]);
                }
                break;
              case "4":
                quest.type = "dropdown";
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
              showIndex: this.modelForm.showIndex,
              password: this.modelForm.password,
              startTime: this.modelForm.startTime,
              endTime: this.modelForm.endTime,
              quota:
                this.modelForm.quota == undefined
                  ? 0
                  : parseInt(this.modelForm.quota),
              type: "normal",
              questions: templateQuestions,
            }),
          }).then(
            (response) => {
              console.log(response);
              if (response.data.success == true) {
                this.templateId = response.data.templateId;
                this.code = response.data.code;
                this.$message({
                  message: "问卷保存成功！",
                  type: "success",
                });
                this.$router.push("/preview?code=" + this.code);
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
            quest.required = question.required;
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
                if (quest.max < quest.min) {
                  var mes =
                    "第" + (parseInt(i) + 1) + "题最小选项数大于最大选项数！";
                  this.$message({
                    message: mes,
                    type: "warning",
                  });
                  return;
                }
                for (j in question.answers) {
                  x = question.answers[j];
                  quest.choices.push(x.value);
                }
                break;
              case "2":
                quest.type = "filling";
                quest.height = parseInt(question.height);
                quest.width = parseInt(question.width);
                break;
              case "3":
                quest.type = "grade";
                quest.grades = [];
                for (j in question.grades) {
                  quest.grades.push(question.grades[j]);
                }
                break;
              case "4":
                quest.type = "dropdown";
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
              showIndex: this.modelForm.showIndex,
              password: this.modelForm.password,
              startTime: this.modelForm.startTime,
              endTime: this.modelForm.endTime,
              quota:
                this.modelForm.quota == undefined
                  ? 0
                  : parseInt(this.modelForm.quota),
              type: "normal",
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
                    code: this.code,
                  }),
                }).then(
                  (response) => {
                    console.log(response);
                    if (response.data.success == true) {
                      this.$message({
                        message: "问卷发布成功！",
                        type: "success",
                      });
                      this.code = response.data.code;
                      this.qrData.text =
                        window.location.host + "/fill?code=" + this.code;
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
.question-index {
  font-family: 仿宋;
  font-size: 20px;
  font-weight: bolder;
}
.question-title {
  font-family: 仿宋;
  font-size: 20px;
  font-weight: bolder;
}
.question-type .el-radio {
  height: 35px;
  width: 80px;
  margin: 0;
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
.foot {
  display: flex;
  align-items: center;
  justify-content: center;
}
.foot .el-button {
  margin-top: 15px;
  font-size: 20px;
  height: 60px;
  width: 160px;
}
</style>