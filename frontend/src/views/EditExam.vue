<template>
  <div class="normal">
    <Header class="header"></Header>
    <div class="editor">
      <el-tabs style="width: 200px" stretch="true">
        <el-tab-pane>
          <span slot="label" style="font-size: 15px"
            ><i class="el-icon-edit-outline"></i>编辑题目</span
          >
          <div class="editor_1">
            <el-button @click="addQuestion(0)">单选题</el-button>
            <el-button @click="addQuestion(1)">多选题</el-button>
            <el-button @click="addQuestion(2)">填空题</el-button>
            <el-button @click="addQuestion(3)">评分题</el-button>
            <el-button @click="addQuestion(4)">下拉题</el-button>
          </div>
        </el-tab-pane>
        <el-tab-pane>
          <span slot="label" style="font-size: 15px"
            ><i class="el-icon-set-up"></i>问卷操作</span
          >
          <div class="editor_2">
            <el-button @click="resetForm('modelForm')">重置</el-button>
            <el-button @click="preview()">预览</el-button>
            <el-button @click="addSubmit()">保存问卷</el-button>
            <el-button @click="publishQuestion">发布问卷</el-button>
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
        </el-tab-pane>
      </el-tabs>
    </div>
    <div class="main">
      <el-button-group class="button_group">
        <el-button icon="el-icon-tickets" @click="editing()"
          >问卷信息</el-button
        >
        <el-button icon="el-icon-setting" @click="notEditing()"
          >问卷设置</el-button
        >
      </el-button-group>
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
            style="margin-top: 15px"
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
          <el-form-item label="是否显示题号" v-if="isEditing == false">
            <el-switch v-model="modelForm.showIndex"> </el-switch>
          </el-form-item>
          <!-- 结束语 -->
          <el-form-item label="结束语" v-if="isEditing == false">
            <el-input
              v-model="modelForm.conclusion"
              style="width: 258px"
              clearable
              placeholder="答卷后展示"
            />
          </el-form-item>
          <!-- 问卷密码 -->
          <el-form-item label="问卷密码" v-if="isEditing == false">
            <el-input
              v-model="modelForm.password"
              style="width: 258px"
              clearable
              placeholder="设置后需要密码才可回答"
            />
          </el-form-item>
          <!-- 问卷限额 -->
          <el-form-item
            v-if="isEditing == false"
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
          <el-form-item label="自动发布时间" v-if="isEditing == false">
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
          <el-form-item label="自动回收时间" v-if="isEditing == false">
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
        <div v-if="isEditing">
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
                :id="setid(index)"
                class="questions"
              >
                <template slot="title">
                  <div class="question-index" v-show="modelForm.showIndex">
                    第{{ index + 1 }}题
                  </div>
                  <div class="question-title">题目:{{ item.questionName }}</div>
                </template>
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
                  <!-- 是否乱序 -->
                  <el-col :span="10">
                    <el-form-item
                      v-if="
                        item.type == '0' || item.type == '1' || item.type == '2'
                      "
                      :prop="`questions.${index}.shuffle`"
                      :label="`是否随机排序此题`"
                      :rules="{
                        required: true,
                        message: '请选择是否随机排序此题',
                        trigger: 'change',
                      }"
                    >
                      <el-switch
                        v-model="item.shuffle"
                        active-color="#13ce66"
                        inactive-color="#ff4949"
                      >
                      </el-switch>
                    </el-form-item>
                  </el-col>
                  <!-- 是否计分 -->
                  <el-col :span="10">
                    <el-form-item
                      v-if="
                        item.type == '0' || item.type == '1' || item.type == '2'
                      "
                      :prop="`questions.${index}.score`"
                      :label="`是否计分`"
                      :rules="{
                        required: true,
                        message: '请选择是否计分',
                        trigger: 'change',
                      }"
                    >
                      <el-switch
                        v-model="item.score"
                        active-color="#13ce66"
                        inactive-color="#ff4949"
                      >
                      </el-switch>
                    </el-form-item>
                  </el-col>
                </el-row>
                <!-- 分数 -->
                <el-form-item
                  v-if="item.score == true"
                  :prop="`questions.${index}.points`"
                  label="分数"
                  :rules="{
                    required: true,
                    message: '请填写此题分数',
                    trigger: 'change',
                  }"
                >
                  <el-input-number
                    v-model="item.points"
                    :precision="1"
                    :step="0.5"
                    :max="20"
                    step-strictly
                  ></el-input-number>
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
                <!-- 选项 -->
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
                    label="评分文字"
                    :prop="`questions.${index}.grades`"
                    :rules="[
                      {
                        required: true,
                        message: '请输入评分',
                        trigger: 'blur',
                      },
                    ]"
                  >
                    <div style="display: flex; flex-wrap: wrap">
                      <el-input
                        v-for="(i, idx) in item.grades"
                        :key="i"
                        v-model="item.grades[idx]"
                        style="
                          width: 120px;
                          margin-right: 10px;
                          margin-bottom: 10px;
                        "
                        clearable
                        :placeholder="'第' + `${idx + 1}` + '级评分'"
                      />
                    </div>
                  </el-form-item>
                </el-row>
                <!-- 选项 -->
                <div v-if="item.type == '0'">
                  <el-form-item label="答案">
                    <el-radio-group
                      v-model="item.answer"
                      v-for="(j, idx2) in item.answers"
                      :key="idx2"
                      @change="changeValue(index)"
                    >
                      <el-radio class="option" :label="idx2">{{
                        j.value
                      }}</el-radio>
                    </el-radio-group></el-form-item
                  >
                </div>
                <div class="multi" v-if="item.type == '1'">
                  <el-form-item label="答案">
                    <el-checkbox-group
                      v-model="item.answer"
                      v-for="(j, idx2) in item.answers"
                      :min="0"
                      :max="item.max"
                      :key="idx2"
                      @change="changeValue(index)"
                    >
                      <el-checkbox class="option" :label="idx2" border>{{
                        j.value
                      }}</el-checkbox>
                    </el-checkbox-group></el-form-item
                  >
                </div>
                <div v-if="item.type == '2'">
                  <el-form-item label="参考答案">
                    <el-input
                      type="textarea"
                      class="input"
                      :rows="item.height"
                      :style="{ '--width': item.width }"
                      placeholder="请输入内容"
                      v-model="item.answer"
                    >
                    </el-input
                  ></el-form-item>
                </div>
                <el-form-item label="编辑题目">
                  <el-button
                    icon="el-icon-circle-plus"
                    v-show="item.type != 2 && item.type != 3"
                    @click="addDomain(index)"
                    >新增选项</el-button
                  >
                  <el-button icon="el-icon-s-order" @click="copyQuestion(index)"
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
      <div class="foot" v-if="isEditing">
        <el-popover placement="top" width="1200px" v-model="popVisible">
          <el-button-group>
            <el-button @click="addQuestion(0)">单选题</el-button>
            <el-button @click="addQuestion(1)">多选题</el-button>
            <el-button @click="addQuestion(2)">填空题</el-button>
            <el-button @click="addQuestion(3)">评分题</el-button>
            <el-button @click="addQuestion(4)">下拉题</el-button>
          </el-button-group>
          <el-button
            id="addButton"
            icon="el-icon-circle-plus-outline"
            slot="reference"
            >添加题目</el-button
          >
        </el-popover>
      </div>
    </div>
    <div class="anchor">
      <h1>目录</h1>
      <Anchor
        show-ink
        container=".main"
        :affix="false"
        v-for="(item, index) in modelForm.questions"
        :key="index"
      >
        <AnchorLink :href="'#question' + index" :title="'题目' + (index + 1)" />
      </Anchor>
    </div>
  </div>
</template>

<script>
import Header from "../components/Header.vue";
import vuedraggable from "vuedraggable";
import VueQr from "vue-qr";
import Clipboard from "clipboard";
export default {
  name: "HelloWorld",
  components: {
    vuedraggable,
    VueQr,
    Header: Header,
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
      quest: 0,
      activeNames: [],
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
        questions: [],
      },
      qrData: {
        text: window.location.host + "/fill?code=" + this.code,
        logo: require("../assets/logo.png"),
      },
      exportLink: "",
      downloadFilename: "",
      isEditing: true,
      dialogVisible: false,
      popVisible: false,
    };
  },
  created: function () {
    this.templateId = this.$route.query.templateId;
    this.code = this.$route.query.code;
    if (this.templateId == undefined) this.templateId = 0;
    console.log(this.templateId);
    this.$axios({
      method: "get",
      url: "http://139.224.50.146:80/apis/details",
      params: { password: "", code: this.code },
    })
      .then((response) => {
        console.log(response);
        if (response.data.success == true) {
          this.modelForm.title = response.data.title;
          this.modelForm.description = response.data.description;
          this.modelForm.conclusion = response.data.conclusion;
          this.modelForm.password = response.data.password;
          response.data.quota == undefined
            ? (this.modelForm.quota = 0)
            : (this.modelForm.quota = response.data.quota);
          if (response.data.startTime != undefined) {
            this.modelForm.startTime = response.data.startTime;
          }
          if (response.data.endTime != undefined) {
            this.modelForm.endTime = response.data.endTime;
          }
          var question = {
            type: "0",
            required: true,
            questionName: "",
            questionSummary: "",
            max: 2,
            min: 1,
            height: 1,
            width: 800,
            shuffle: false,
            score: false,
            points: 5.0,
            answer: [],
            grades: [],
            answers: [],
          };
          var item = {};
          var i = 0;
          var j = 0;
          for (i in response.data.questions) {
            question = {
              type: "0",
              required: true,
              questionName: "",
              questionSummary: "",
              max: 2,
              min: 1,
              height: 1,
              width: 800,
              shuffle: false,
              score: false,
              points: 5.0,
              answer: [],
              grades: [],
              answers: [],
            };
            item = response.data.questions[i];
            question.questionName = item.stem;
            question.questionSummary = item.description;
            question.required = item.required;
            question.shuffle = item.shuffle;
            question.points = item.points;
            if (question.points != undefined) {
              question.score = true;
            }
            question.answer = item.answer;
            if (question.answer == undefined) {
              question.answer = [];
            }
            switch (item.type) {
              case "choice":
                question.type = "0";
                for (j in item.choices) {
                  question.answers.push({ value: item.choices[j] });
                }
                break;
              case "multi-choice":
                question.type = "1";
                question.max = item.max;
                question.min = item.min;
                for (j in item.choices) {
                  question.answers.push({ value: item.choices[j] });
                }
                break;
              case "filling":
                question.type = "2";
                question.height = parseInt(item.height);
                question.width = parseInt(item.width);
                question.answers.push({ value: "" });
                question.answers.push({ value: "" });
                break;
              case "grade":
                question.type = "3";
                for (j in item.grades) {
                  question.grades.push(item.grades[j]);
                }
                break;
              case "dropdown":
                question.type = "4";
                for (j in item.choices) {
                  question.answers.push({ value: item.choices[j] });
                }
                break;
            }
            console.log(question);
            this.modelForm.questions.push(question);
            this.activeNames.push(this.modelForm.questions.length - 1);
          }
          console.log(this.modelForm.questions);
        } else {
          console.log(response.data.message);
        }
      })
      .catch((err) => console.log(err));
  },
  methods: {
    setid(i) {
      return "question" + i;
    },
    editing() {
      this.isEditing = true;
    },
    notEditing() {
      this.isEditing = false;
    },
    changeValue(index) {
      console.log(this.modelForm.questions[index].answer);
    },
    handleType(index) {
      if (this.modelForm.questions[index].type == "1") {
        this.modelForm.questions[index].answer = [];
      } else {
        this.modelForm.questions[index].answer = null;
      }
    },
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
        shuffle: false,
        score: false,
        points: 5.0,
        answer: null,
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
      this.template.shuffle = this.modelForm.questions[index].shuffle;
      this.template.score = this.modelForm.questions[index].score;
      this.template.points = this.modelForm.questions[index].points;
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
      if (this.template.type == "2") {
        i = 0;
        this.template.answer = [];
        for (i in this.modelForm.questions[index].answer) {
          this.template.answer.push(this.modelForm.questions[index].answer[i]);
        }
      } else {
        this.template.answer = this.modelForm.questions[index].answer;
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
        shuffle: false,
        score: false,
        points: 5.0,
        answer: [],
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
            quest.shuffle = question.shuffle;
            quest.answer = question.answer;
            if (question.score == true) {
              quest.points = question.points;
              if (
                quest.answer == null ||
                quest.answer == [] ||
                quest.answer == undefined ||
                quest.answer == ""
              ) {
                var mes = "第" + (parseInt(i) + 1) + "题未设定答案！";
                this.$message({
                  message: mes,
                  type: "warning",
                });
                return;
              }
            } else {
              quest.points = null;
            }
            if (
              quest.answer == [] ||
              quest.answer == undefined ||
              quest.answer == ""
            ) {
              quest.answer = null;
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
                if (quest.max < quest.min) {
                  mes =
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
              type: "exam",
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
            quest.shuffle = question.shuffle;
            quest.answer = question.answer;
            if (question.score == true) {
              quest.points = question.points;
              if (
                quest.answer == null ||
                quest.answer == [] ||
                quest.answer == undefined ||
                quest.answer == ""
              ) {
                var mes = "第" + (parseInt(i) + 1) + "题未设定答案！";
                this.$message({
                  message: mes,
                  type: "warning",
                });
                return;
              }
            } else {
              quest.points = null;
            }
            if (
              quest.answer == [] ||
              quest.answer == undefined ||
              quest.answer == ""
            ) {
              quest.answer = null;
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
                if (quest.max < quest.min) {
                  mes =
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
              type: "exam",
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
            quest.shuffle = question.shuffle;
            quest.answer = question.answer;
            if (question.score == true) {
              quest.points = question.points;
              if (
                quest.answer == null ||
                quest.answer == [] ||
                quest.answer == undefined ||
                quest.answer == ""
              ) {
                var mes = "第" + (parseInt(i) + 1) + "题未设定答案！";
                this.$message({
                  message: mes,
                  type: "warning",
                });
                return;
              }
            } else {
              quest.points = null;
            }
            if (
              quest.answer == [] ||
              quest.answer == undefined ||
              quest.answer == ""
            ) {
              quest.answer = null;
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
                if (quest.max < quest.min) {
                  mes =
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
              type: "exam",
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
.normal {
  display: grid;
  grid-template-columns: 200px auto 200px;
  grid-template-rows: 60px auto;
  height: 100%;
  background-image: url("../assets/Main_bg.jpg");
  background-size: 100%;
  background-repeat: no-repeat;
}
.header {
  grid-column-start: 1;
  grid-column-end: 4;
  grid-row-start: 1;
  grid-row-end: 2;
}
.editor {
  grid-column-start: 1;
  grid-column-end: 2;
  grid-row-start: 2;
  grid-row-end: 3;
  width: 200px;
  height: 100%;
  background-color: #f0f0f0;
  opacity: 0.9;
}
.main {
  grid-column-start: 2;
  grid-column-end: 3;
  grid-row-start: 2;
  grid-row-end: 3;
  width: 100%;
  height: 100%;
  text-align: left;
  overflow-x: hidden;
  overflow-y: scroll;
  height: 100%;
  background-color: #ffffff;
  opacity: 0.9;
}
.anchor {
  grid-column-start: 3;
  grid-column-end: 4;
  grid-row-start: 2;
  grid-row-end: 3;
  background-color: #f0f0f0;
  opacity: 0.9;
}
.button_group {
  position: fixed;
}
.button_group .el-button {
  border-radius: 0;
  border: white;
  border-bottom: #000000;
  font-size: 15px;
}
.basic {
  width: 600px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: baseline;
  text-align: left;
}
.editor .el-button {
  border-radius: 0;
  font-size: 18px;
  color: #000000;
  background-color: #f0f0f0;
  margin: 0;
}
.editor_1,
.editor_2 {
  display: flex;
  flex-direction: column;
}
.question_name {
  display: flex;
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
#addButton {
  margin-top: 15px;
  font-size: 20px;
  height: 60px;
  width: 160px;
}
</style>