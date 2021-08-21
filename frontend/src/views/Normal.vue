<template>
  <div class="hello">
    <el-form
      ref="modelForm"
      :rule="rules"
      :model="modelForm"
      label-position="right"
      label-width="150px"
    >
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
      <el-form-item
        label="问卷描述"
        :rules="{
          required: true,
        }"
      >
        <el-input
          v-model="modelForm.description"
          style="width: 258px"
          clearable
          placeholder="请填写问卷描述"
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
      <el-form-item> 可通过拖拽题目改变顺序</el-form-item>
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
            >
              <template slot="title">
                第{{ index + 1 }}题,题目:{{ item.questionName }}
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
                <el-radio-group v-model="item.type">
                  <el-radio label="0">单选题</el-radio>
                  <el-radio label="1">多选题</el-radio>
                  <el-radio label="2">填空题</el-radio>
                  <el-radio label="3">评分题</el-radio>
                  <el-radio label="4">下拉题</el-radio>
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
                  <el-radio label=true>是</el-radio>
                  <el-radio label=false>否</el-radio>
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
                :rules="{
                  required: true,
                  message: '请填写问题描述',
                  trigger: 'change',
                }"
              >
                <el-input
                  v-model.trim="item.questionSummary"
                  style="width: 258px"
                  clearable
                  placeholder="请填写问题描述"
                />
              </el-form-item>
              <!-- 最大选项 -->
              <el-form-item
                v-show="item.type == 1"
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
              <!-- 最小选项 -->
              <el-form-item
                v-show="item.type == 1"
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
              <!-- 高度 -->
              <el-form-item
                v-show="item.type == 2"
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
              <!-- 宽度 -->
              <el-form-item
                v-show="item.type == 2"
                :prop="`questions.${index}.width`"
                label="填空框宽度（px）"
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
              <!-- 答案 -->
              <el-form-item
                v-for="(opt, idx) in item.answers"
                v-show="item.type != 2"
                :key="idx"
                :label="`选项${idx + 1}`"
                :prop="`questions.${index}.answers.${idx}.value`"
                :rules="[
                  { required: true, message: '请输入选项', trigger: 'blur' },
                ]"
              >
                <el-input
                  v-model.trim="opt.value"
                  style="width: 258px"
                  clearable
                  placeholder="请输入选项"
                />
                <el-input
                  v-model.trim="opt.scores"
                  v-show="item.type == 3"
                  style="width: 125px"
                  clearable
                  placeholder="请输入评分"
                />
                <el-button
                  style="margin-left: 20px"
                  @click.prevent="removeDomain(index, idx)"
                  >删除</el-button
                >
              </el-form-item>
              <el-form-item>
                <el-button v-show="item.type != 2" @click="addDomain(index)"
                  >新增选项</el-button
                >
                <el-button @click="copyQuestion(index)">复制题目</el-button>
                <el-button @click="removeQuestion(index)">删除题目</el-button>
              </el-form-item>
            </el-collapse-item>
          </vuedraggable>
        </el-collapse>
      </div>
      <el-form-item>
        <el-button @click="addQuestion">新增题目</el-button>
        <el-button style="margin-top: 10px" @click="addSubmit()"
          >保存问卷</el-button
        >
        <el-button @click="resetForm('modelForm')">重置</el-button>
      </el-form-item>
      <el-form-item>
        <el-button @click="publishQuestion" type="primary">发布问卷</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>


<script>
import vuedraggable from "vuedraggable";
export default {
  name: "HelloWorld",
  components: {
    vuedraggable,
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
        password: "",
        questions: [
          {
            type: "0",
            required: false,
            questionName: "",
            questionSummary: "",
            max: 2,
            min: 1,
            height: 1,
            width: 100,
            answers: [{ value: "" , scores: 0}, { value: "" , scores: 0}],
          },
          {
            type: "0",
            required: false,
            questionName: "",
            questionSummary: "",
            max: 2,
            min: 1,
            height: 1,
            width: 100,
            answers: [{ value: "" , scores: 0}, { value: "" , scores: 0}],
          },
        ],
      },
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
        required: false,
        questionName: "",
        questionSummary: "",
        max: 2,
        min: 1,
        height: 1,
        width: 100,
        answers: [{ value: "" , scores: 0}, { value: "" , scores: 0}],
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
          let templateQuestions = [];
          let quest = {};
          let question = {};
          let x = {};
          for (question in this.modelForm.questions){
            quest.stem = question.questionName;
            quest.description = question.questionSummary;
            quest.required = question.required;
            switch (question.type){
              case 0:
                quest.type = "choice";
                for (x in question.answers){
                  quest.choices.push(x.value);
                }
                break;
              case 1:
                quest.type = "multi-choice";
                quest.max = question.max;
                quest.min = question.min;
                for (x in question.answers){
                  quest.choices.push(x.value);
                }
                break;
              case 2:
                quest.type = "filling";
                quest.height = question.height;
                quest.width = question.width;
                break;
              case 3:
                quest.type = "grade";
                for (x in question.answers){
                  quest.choices.push(x.value);
                  quest.scores.push(x.scores);
                }
                break;
              case 4:
                quest.type = "dropdown";
                for (x in question.answers){
                  quest.choices.push(x.value);
                }
                break;
            }
            templateQuestions.push(quest);
          }
          this.$axios({
            method: "post",
            url: "http://82.156.190.251:80/apis/normal/submit",
            data: JSON.stringify({
              templateId: this.templateId,
              title: this.modelForm.title,
              description: this.modelForm.description,
              password: this.modelForm.password,
              questions: templateQuestions,
            }),
          }).then(
            (response) => {
              console.log(response);
              if (response.data.success == true) {
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
          console.log(this.modelForm.questions);
        }
      });
    },
    publishQuestion() {
      this.addSubmit();
      console.log("上传成功!");
    },
  },
};
</script>
