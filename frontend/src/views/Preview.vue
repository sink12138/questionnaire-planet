<template>
  <el-container>
    <el-aside width="200px">
      <div class="editor">
        <router-link to="/history">
          <div class="logo">
            <Logo></Logo>
            <div class="web-title">问卷星球</div>
          </div>
        </router-link>
        <div class="info">填写问卷的用户将看到右侧的界面</div>
        <div class="export">
          <el-button
            type="primary"
            class="button"
            @click="exportQuest()"
            icon="el-icon-download"
            >导出</el-button
          >
        </div>
      </div>
    </el-aside>
    <el-main>
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
          <el-form
            :model="answers"
            :rules="rules"
            ref="ruleForm"
            label-width="100px"
            class="ruleForm"
          >
            <div
              v-for="(item, index_question) in questions"
              :key="index_question"
            >
              <el-divider content-position="left" style="margin-top: 15px"
                ><div v-show="showIndex">
                  第{{ index_question + 1 }}题
                </div></el-divider
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
                    </el-radio-group>
                  </el-form-item>
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
                    </el-checkbox-group>
                  </el-form-item>
                </div>
                <div v-if="item.type == 'filling'">
                  <el-form-item
                    label="答案"
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
                    </el-input>
                  </el-form-item>
                </div>
                <div v-if="item.type == 'grade'">
                  <el-form-item
                    label="评分"
                    :rules="{
                      required: item.required,
                    }"
                  >
                    <el-rate
                      v-model="answers[index_question]"
                      show-text
                      :texts="item.grades"
                    >
                    </el-rate>
                  </el-form-item>
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
                <div class="multi" v-if="item.type == 'sign-up'">
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
                      <el-checkbox class="option" :label="index" border
                        >{{ i }} 共{{ item.quotas[index] }},剩余{{
                          item.remains[index]
                        }}</el-checkbox
                      >
                    </el-checkbox-group>
                  </el-form-item>
                </div>
                <div v-if="item.type == 'location'">
                  <el-form-item
                    label="定位"
                    :rules="{
                      required: item.required,
                    }"
                  >
                    <el-button
                      type="primary"
                      icon="el-icon-location-information"
                      @click="getLocation(index_question)"
                      >点击获取定位</el-button
                    >
                    <el-input
                      disabled
                      type="text"
                      class="input"
                      placeholder="您的位置"
                      v-model="answers[index_question]"
                    >
                    </el-input>
                  </el-form-item>
                </div>
              </div>
            </div>
          </el-form>
        </div>
      </div>
    </el-main>
  </el-container>
</template>

<script>
import AMapJS from "amap-js";
import logo from "../components/svg_logo.vue";
export default {
  components: {
    Logo: logo,
  },
  data() {
    return {
      templateId: 0,
      code: "",
      locked: false,
      title: "问卷标题",
      type: "normal",
      description: "问卷描述",
      showIndex: true,
      password: "",
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
          grades: [
            "非常不满意",
            "不满意",
            "一般",
            "very good",
            "very very good",
          ],
        },
        {
          type: "dropdown",
          stem: "瓜是什么做的?",
          description: "",
          required: true,
          choices: ["(C2H5O)n", "Au", "Fe"],
        },
        {
          type: "location",
          stem: "瓜从哪吃?",
          description: "",
          required: true,
        },
      ],
      multi: [],
      answers: [],
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
      params: { code: this.code, password: "" },
    })
      .then((response) => {
        console.log(response);
        if (response.data.success == true) {
          this.title = response.data.title;
          this.type = response.data.type;
          this.description = response.data.description;
          this.showIndex = response.data.showIndex;
          this.password = response.data.password;
          this.questions = response.data.questions;
        } else {
          console.log(response.data.message);
        }
      })
      .catch((err) => console.log(err));
  },
  mounted() {
    this.gaodeMap = new AMapJS.AMapLoader({
      key: "20f6820df07b227d816cb3a065241c7a",
      version: "1.4.15",
      plugins: ["AMap.Geolocation"], //需要加载的插件
    });
  },
  methods: {
    getLocation(index) {
      this.$confirm("此操作将获取您当前的位置, 是否同意?", "提示", {
        confirmButtonText: "同意",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.gaodeMap.load().then(({ AMap }) => {
            new AMap.Geolocation({
              enableHighAccuracy: false, //是否使用高精度定位，默认:true
              timeout: 10000, //超过10秒后停止定位，默认：无穷大
            }).getCurrentPosition((status, result) => {
              console.log("状态", status);
              this.$set(
                this.answers,
                index,
                result.addressComponent.province +
                  result.addressComponent.city +
                  result.addressComponent.district
              );
              console.log("位置", this.answers[index]);
            });
          });
        })
        .catch(() => {
          this.$notify({
            title: "提示",
            message: "已取消定位",
            type: "info",
          });
        });
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