<template>
  <div>
    <div id="quest" ref="quest">
      <div class="head">
        <el-container>
          <el-container>
            <el-aside width="200px">
              <div class="editor">
                <router-link to="/">
                  <div class="logo">
                    <Logo></Logo>
                    <div class="web-title">问卷星球</div>
                  </div>
                </router-link>
                <div class="editor-save">
                  <el-button @click="save()">保存修改</el-button>
                </div>
                <div class="publish">
                  <el-button @click="publishQuestion" type="primary"
                    >发布问卷</el-button
                  >
                  <el-button @click="dialogVisible = true">Qrcode</el-button>
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
                  <el-form-item
                    label="问卷描述"
                  >
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
                      placeholder="可为空"
                    />
                  </el-form-item>
                </div>
                <div class="question">
                  <div
                    v-for="(item, index_question) in questions"
                    :key="index_question"
                  >
                    <div class="question-title">
                      <div>
                        第{{ index_question + 1 }}题,题目:{{ item.stem }}
                      </div>
                      <a>题目描述：{{ item.description }}</a>
                    </div>
                    <div class="question-content">
                      <div v-if="item.type == 'choice'">
                        <el-radio-group
                          v-model="answers[index_question]"
                          v-for="(i, index) in item.choices"
                          :key="index"
                          @change="changeValue"
                        >
                          <el-radio :label="index">{{ i }}</el-radio>
                        </el-radio-group>
                      </div>
                      <div v-if="item.type == 'multi-choice'">
                        <el-checkbox-group
                          v-model="multi"
                          v-for="(i, index) in item.choices"
                          :min="item.min"
                          :max="item.max"
                          :key="index"
                          @change="multiChangeValue(index_question)"
                        >
                          <el-checkbox :label="index" border>{{
                            i
                          }}</el-checkbox>
                        </el-checkbox-group>
                      </div>
                      <div v-if="item.type == 'filling'">
                        <el-input
                          type="textarea"
                          class="input"
                          :rows="item.height"
                          :style="{ '--width': item.width }"
                          placeholder="请输入内容"
                          v-model="answers[index_question]"
                        >
                        </el-input>
                      </div>
                      <div v-if="item.type == 'grade'">
                        <el-radio-group
                          v-model="answers[index_question]"
                          v-for="(i, index) in item.choices"
                          :key="index"
                          @change="changeValue"
                        >
                          <el-radio :label="index"
                            >{{ i }}({{ item.scores[index] }})</el-radio
                          >
                        </el-radio-group>
                      </div>
                      <div v-if="item.type == 'dropdown'">
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
                      </div>
                      <div v-if="item.type == 'vote'">
                        <el-checkbox-group
                          v-model="multi"
                          v-for="(i, index) in item.choices"
                          :min="item.min"
                          :max="item.max"
                          :key="index"
                          @change="multiChangeValue(index_question)"
                        >
                          <el-checkbox :label="index" border>{{
                            i
                          }}</el-checkbox>
                        </el-checkbox-group>
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
                              >{{ i }}共{{ item.quotas[index] }},剩余{{
                                item.remains[index]
                              }}
                            </el-checkbox>
                          </el-checkbox-group>
                        </el-form-item>
                      </div>
                    </div>
                  </div>
                </div>
              </el-form>
            </el-main>
          </el-container>
        </el-container>
      </div>
    </div>
  </div>
</template>


<script>
import logo from "../components/svg-logo.vue";
import VueQr from "vue-qr";
import Clipboard from "clipboard";
export default {
  name: "HelloWorld",
  components: {
    VueQr,
    Logo: logo,
  },
  data() {
    return {
      templateId: 0,
      questions: [],
      multi: [],
      answers: [],
      quest: 0,
      template: {},
      rules: {},
      modelForm: {
        title: "新的问卷",
        description: "",
        conclusion: "",
        password: "",
        quota: 0,
      },
      qrData: {
        text: window.location.host + "/questionnaire/" + this.templateId,
        logo: require("../assets/logo.png"),
      },
      exportLink: "",
      downloadFilename: "",
      dialogVisible: false,
    };
  },
  created: function () {
    this.templateId = this.$route.query.templateId;
    if (this.templateId == undefined) this.templateId = 0;
    console.log(this.templateId);
    this.$axios({
      method: "get",
      url: "http://139.224.50.146:80/apis/details",
      params: { templateId: this.templateId, password: "" },
    })
      .then((response) => {
        console.log(response);
        if (response.data.success == true) {
          this.modelForm.title = response.data.title;
          this.type = response.data.type;
          this.modelForm.description = response.data.description;
          this.modelForm.conclusion = response.data.conclusion;
          this.modelForm.password = response.data.password;
          this.modelForm.quota = response.data.quota;
          this.questions = response.data.questions;
        } else {
          console.log(response.data.message);
        }
      })
      .catch((err) => console.log(err));
  },
  methods: {
    save() {
      if (this.modelForm.password == undefined) {
        this.modelForm.password = "";
      }
      if (this.modelForm.conclusion == undefined) {
        this.modelForm.conclusion = "";
      }
      if (this.modelForm.quota == undefined) {
        this.modelForm.quota = 0;
      }
      console.log(this.modelForm.password);
      this.$axios({
        method: "post",
        url: "http://139.224.50.146:80/apis/adjust",
        data: JSON.stringify({
          templateId: parseInt(this.templateId),
          title: this.modelForm.title,
          description: this.modelForm.description,
          conclusion: this.modelForm.conclusion,
          password: this.modelForm.password,
          quota: parseInt(this.modelForm.quota),
        }),
      }).then(
        (response) => {
          console.log(response);
          if (response.data.success == true) {
            this.$message({
              message: "问卷修改成功！",
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
    },
    publishQuestion() {
      this.$axios({
        method: "post",
        url: "http://139.224.50.146:80/apis/release",
        data: JSON.stringify({
          templateId: this.templateId,
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
</style>