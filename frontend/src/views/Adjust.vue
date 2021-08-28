<template>
  <div>
    <div id="quest" ref="quest">
      <div class="head">
        <el-container>
          <el-container>
            <el-aside width="200px">
              <div class="editor">
                <router-link to="/history">
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
                  <el-row>
                    <el-col :span="10">
                      <!-- 显示题号 -->
                      <el-form-item label="是否显示题号">
                        <el-switch v-model="modelForm.showIndex"> </el-switch>
                      </el-form-item>
                    </el-col>
                    <el-col :span="10">
                      <!-- 限填一次 -->
                      <el-form-item label="是否每人限填一次">
                        <el-switch v-model="modelForm.limited"> </el-switch>
                      </el-form-item>
                    </el-col>
                  </el-row>
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
                <div class="question">
                  <div
                    v-for="(item, index_question) in questions"
                    :key="index_question"
                  >
                    <el-divider content-position="left" style="margin-top: 15px"
                      ><div v-show="modelForm.showIndex">
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
                            <el-radio class="option" :label="index">{{
                              i
                            }}</el-radio>
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
import AMapJS from "amap-js";
import logo from "../components/svg_logo.vue";
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
      code: "",
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
        showIndex: true,
        limited: true,
        password: "",
        quota: undefined,
        questions: [],
      },
      qrData: {
        text: window.location.host + "/fill?templateId=" + this.templateId,
        logo: require("../assets/logo.png"),
      },
      exportLink: "",
      downloadFilename: "",
      dialogVisible: false,
      gaodeMap: {},
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
          this.type = response.data.type;
          this.modelForm.description = response.data.description;
          this.modelForm.conclusion = response.data.conclusion;
          this.modelForm.showIndex = response.data.showIndex;
          this.modelForm.limited = response.data.limited;
          this.modelForm.password = response.data.password;
          this.modelForm.quota = response.data.quota;
          if (response.data.startTime != undefined) {
            this.modelForm.startTime = response.data.startTime;
          }
          if (response.data.endTime != undefined) {
            this.modelForm.endTime = response.data.endTime;
          }
          this.questions = response.data.questions;
        } else {
          console.log(response.data.message);
        }
        while (this.answers.length < this.questions.length) {
          this.answers.push(null);
        }
      })
      .catch((err) => console.log(err));
    var i = 0;
    for (i in this.questions) {
      this.answers[i] = [];
    }
    console.log(this.answers);
  },
  mounted() {
    this.gaodeMap = new AMapJS.AMapLoader({
      key: "20f6820df07b227d816cb3a065241c7a",
      version: "1.4.15",
      plugins: ["AMap.Geolocation", "AMap.CitySearch"], //需要加载的插件
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
          var answertmp = this.answers;
          var tmp = this;
          this.gaodeMap.load().then(({ AMap }) => {
            var citysearch = new AMap.CitySearch();
            //自动获取用户IP，返回当前城市
            citysearch.getLocalCity(function (status, result) {
              if (status === "complete" && result.info === "OK") {
                if (result && result.city && result.bounds) {
                  var cityinfo = result.city;
                  console.log("您当前所在城市：", cityinfo);
                  tmp.$set(answertmp, index, cityinfo);
                  console.log("您当前所在城市：", answertmp[index]);
                  //地图显示当前城市
                }
              } else {
                console.log(result.info);
              }
            });
            // new AMap.Geolocation({
            //   enableHighAccuracy: false, //是否使用高精度定位，默认:true
            //   timeout: 10000, //超过10秒后停止定位，默认：无穷大
            // }).getCurrentPosition((status, result) => {
            //   console.log("状态", status);
            //   this.$set(
            //     this.answers,
            //     index,
            //     result.addressComponent.province +
            //       result.addressComponent.city +
            //       result.addressComponent.district
            //   );
            //   console.log("位置", this.answers[index]);
            // });
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
    save() {
      if (this.modelForm.password == undefined) {
        this.modelForm.password = "";
      }
      if (this.modelForm.conclusion == undefined) {
        this.modelForm.conclusion = "";
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
          showIndex: this.modelForm.showIndex,
          limited: this.modelForm.limited,
          password: this.modelForm.password,
          startTime: this.modelForm.startTime,
          endTime: this.modelForm.endTime,
          quota: parseInt(this.modelForm.quota),
        }),
      }).then(
        (response) => {
          console.log(response);
          if (response.data.success == true) {
            this.$notify({
              title: "提示",
              message: "问卷修改成功",
              type: "success",
            });
          } else {
            this.$notify({
              title: "提示",
              message: response.data.message,
              type: "info",
            });
          }
        },
        (err) => {
          this.$notify({
            title: "错误",
            message: err,
            type: "error",
          });
        }
      );
    },
    publishQuestion() {
      if (this.modelForm.password == undefined) {
        this.modelForm.password = "";
      }
      if (this.modelForm.conclusion == undefined) {
        this.modelForm.conclusion = "";
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
          showIndex: this.modelForm.showIndex,
          password: this.modelForm.password,
          startTime: this.modelForm.startTime,
          endTime: this.modelForm.endTime,
          quota: this.modelForm.quota,
        }),
      }).then(
        (response) => {
          console.log(response);
          if (response.data.success == true) {
            this.$notify({
              title: "提示",
              message: "问卷修改成功",
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
                  this.$$notify({
                    title: "提示",
                    message: "问卷发布成功",
                    type: "success",
                  });
                  this.code = response.data.code;
                  this.qrData.text =
                    window.location.host + "/fill?code=" + this.code;
                  this.dialogVisible = true;
                } else {
                  this.$notify({
                    title: "提示",
                    message: response.data.message,
                    type: "info",
                  });
                }
              },
              (err) => {
                this.$notify({
                  title: "错误",
                  message: err,
                  type: "error",
                });
              }
            );
            console.log("发布成功!");
          } else {
            this.$notify({
              title: "提示",
              message: response.data.message,
              type: "info",
            });
          }
        },
        (err) => {
          this.$notify({
            title: "错误",
            message: err,
            type: "error",
          });
        }
      );
    },
    async copyShareLink() {
      let clipboard = new Clipboard(".tag-copy");
      console.log(clipboard);
      await clipboard.on("success", () => {
        this.$notify({
          title: "提示",
          message: "已复制链接到剪贴板",
          type: "success",
        });
        clipboard.destroy();
      });
      clipboard.on("error", () => {
        this.$notify({
          title: "错误",
          message: "复制出现错误",
          type: "error",
        });
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
.share {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
</style>