<template>
  <div id="quest" ref="quest">
    <el-dialog
      title="请先登录后再填写问卷哦~~~"
      :visible.sync="dialogFormVisible1"
      style="text-align: left; width: 1050px; margin: auto"
      :close-on-click-modal="false"
      :before-close="goBack"
    >
      <el-form :model="formData" :rules="rules" ref="formData">
        <el-form-item
          label="电子邮箱"
          :label-width="formLabelWidth"
          prop="email"
        >
          <el-input
            v-model="formData.email"
            autocomplete="off"
            style="width: 300px"
            placeholder="请输入您的电子邮箱"
            v-focus
            @keyup.enter.native="tologin"
          ></el-input>
        </el-form-item>
        <el-form-item
          label="密码"
          :label-width="formLabelWidth"
          prop="password"
        >
          <el-input
            v-model="formData.password"
            autocomplete="off"
            show-password
            style="width: 300px"
            placeholder="请输入密码"
            @keyup.enter.native="tologin"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="goBack">取 消</el-button>
        <el-button type="primary" @click="tologin">登 录</el-button>
        <el-link href="register" type="info" style="margin: 5px 5px 5px 320px"
          ><sup>还没有账号？点此处注册账号</sup></el-link
        >
      </div>
    </el-dialog>

    <el-dialog
      title="哎呀，问卷被加密了，请输入问卷密码！"
      :visible.sync="dialogFormVisible2"
      style="text-align: left; width: 1050px; margin: auto"
      :close-on-click-modal="false"
      :before-close="goBack"
    >
      <el-form :model="password" @submit.native.prevent>
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
            v-focus
            @keyup.enter.native="unlock"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="goBack">返回首页</el-button>
        <el-button type="primary" @click="unlock">确认</el-button>
      </div>
    </el-dialog>

    <div class="head" v-if="submitted == false">
      <h1>
        {{ title }}
      </h1>
      <h3>
        {{ description }}
      </h3>
      <h3>问卷剩余{{ remain }}份</h3>

      <div
        v-if="type === 'exam'"
        class="timer"
        style="position: absolute; float: right; right: 10px"
      >
        <p>截止时间：{{ deadlline }}</p>
        <p>剩余时间：{{ day }}天{{ hour }}时{{ minute }}分{{ second }}秒</p>
      </div>
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
          <div v-if="mark[index_question] == true">
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
                    @change="
                      (val) => {
                        changeValue(val, index_question);
                      }
                    "
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
                    v-model="answers[index_question]"
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
                    v-model="answers[index_question]"
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
                    v-model="answers[index_question]"
                    v-for="(i, index) in item.choices"
                    :min="0"
                    :max="item.max"
                    :key="index"
                    @change="multiChangeValue(index_question)"
                  >
                    <el-checkbox
                      class="option"
                      :label="index"
                      border
                      :disabled="item.remains[index] == 0 ? true : false"
                      >{{ i }}共{{ item.quotas[index] }},剩余{{
                        item.remains[index]
                      }}
                    </el-checkbox>
                  </el-checkbox-group>
                </el-form-item>
              </div>
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
    <div class="conclusion" v-if="submitted == true">{{ conclusion }}</div>
    <div class="conclusion" v-if="submitted == true && type == 'exam'">
      您的分数是{{ points }}
    </div>
    <div class="voted" v-if="submitted == true && isVote == true">
      <div class="result">
        <div class="chart">
          <canvas id="myChart"></canvas>
        </div>
        <el-table :data="results" max-height="300">
          <el-table-column fixed type="index" width="80"> </el-table-column>
          <el-table-column prop="stem" label="题目题干"> </el-table-column>
          <el-table-column>
            <template slot-scope="scope">
              <el-button @click="updateChart(scope.row)">投票结果</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <div class="question" v-if="submitted == true && type == 'exam'">
      <el-form
        :model="results"
        :rules="rules"
        ref="ruleForm"
        label-width="100px"
        class="ruleForm"
      >
        <div v-for="(item, index_question) in results" :key="index_question">
          <div class="question-title" v-if="item.yourAnswer != null">
            <div class="stem">{{ item.stem }}</div>
          </div>

          <div class="question-content">
            <div v-if="item.type == 'choice' && item.yourAnswer != null">
              <el-form-item label="你的答案">
                <el-radio-group
                  v-model="item.yourAnswer"
                  v-for="(i, index) in item.choices"
                  :key="index"
                  @change="changeValue"
                >
                  <el-radio class="option" :label="index" disabled>{{
                    i
                  }}</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="正确答案">
                <el-radio-group
                  v-model="item.correctAnswer"
                  v-for="(i, index) in item.choices"
                  :key="index"
                  @change="changeValue"
                >
                  <el-radio class="option" :label="index" disabled>{{
                    i
                  }}</el-radio>
                </el-radio-group>
              </el-form-item>
              <div v-if="item.points != null">
                <el-form-item label="你的得分">
                  <el-input
                    disabled
                    type="text"
                    class="input"
                    placeholder="你的得分"
                    v-model="item.points"
                  >
                  </el-input>
                </el-form-item>
              </div>
            </div>
            <div class="multi" v-if="item.type == 'multi-choice'">
              <el-form-item label="你的答案">
                <el-checkbox-group
                  v-model="item.yourAnswer"
                  v-for="(i, index) in item.choices"
                  :min="0"
                  :max="item.max"
                  :key="index"
                  @change="multiChangeValue(index_question)"
                >
                  <el-checkbox class="option" :label="index" border disabled>{{
                    i
                  }}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="正确答案">
                <el-checkbox-group
                  v-model="item.correctAnswer"
                  v-for="(i, index) in item.choices"
                  :min="0"
                  :max="item.max"
                  :key="index"
                  @change="multiChangeValue(index_question)"
                >
                  <el-checkbox class="option" :label="index" border disabled>{{
                    i
                  }}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <div v-if="item.points != null">
                <el-form-item label="你的得分">
                  <el-input
                    disabled
                    type="text"
                    class="input"
                    placeholder="你的得分"
                    v-model="item.points"
                  >
                  </el-input>
                </el-form-item>
              </div>
            </div>
            <div v-if="item.type == 'filling'">
              <el-form-item label="你的答案">
                <el-input
                  disabled
                  type="text"
                  class="input"
                  placeholder="你的答案"
                  v-model="item.yourAnswer"
                >
                </el-input>
              </el-form-item>
              <el-form-item
                label="参考答案"
                v-for="(opt, idx) in item.correctAnswer"
                :key="idx"
              >
                <el-input
                  disabled
                  type="text"
                  class="input"
                  placeholder="无参考答案"
                  v-model="item.correctAnswer[idx]"
                >
                </el-input>
              </el-form-item>
              <div v-if="item.points != null">
                <el-form-item label="你的得分">
                  <el-input
                    disabled
                    type="text"
                    class="input"
                    placeholder="你的得分"
                    v-model="item.points"
                  >
                  </el-input>
                </el-form-item>
              </div>
            </div>
          </div>
        </div>
      </el-form>
    </div>
    <div class="submit" v-if="fillRight == true">
      <el-button @click="submit()" v-if="submitted == false"
        >提交问卷</el-button
      >
    </div>
  </div>
</template>

<script>
import AMapJS from "amap-js";
import Chart from "chart.js/auto";
Chart.defaults.font.size = 15;
export default {
  data() {
    var checkEmail = (rule, value, callback) => {
      if (!value) {
        return callback(new Error("邮箱不能为空"));
      } else {
        callback();
      }
    };
    var validatePass = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请输入密码"));
      } else {
        if (value.length < 6 || value.length > 20) {
          callback(new Error("请输入六至二十位"));
        }
        var regx = /^(?!([a-zA-Z]+|\d+)$)[a-zA-Z\d]{6,20}$/;
        if (!this.formData.password.match(regx)) {
          callback(new Error("请同时包含字母数字"));
        }
        callback();
      }
    };
    return {
      code: "",
      submitted: false,
      isVote: false,
      locked: false,
      login: false,
      fillRight: false,
      title: "未找到问卷",
      type: "normal",
      description: "问卷未找到/该问卷已停止发布/已填过该问卷，请确认问卷链接",
      conclusion: "谢谢",
      showIndex: true,
      remain: "∞",
      password: "",
      shuffleId: "",
      deadlline: "",
      nowtime: "",
      lefttime: 0,
      day: 0,
      hour: 0,
      minute: 0,
      second: 0,
      formData: {
        email: "",
        password: "",
      },
      rules: {
        email: [{ validator: checkEmail, trigger: "blur" }],
        password: [{ validator: validatePass, trigger: "blur" }],
      },
      dialogFormVisible1: false,
      dialogFormVisible2: false,
      formLabelWidth: "100px",
      results: [],
      questions: [],
      answers: [],
      mark: [],
      logic: [],
      myChart: null,
      canvas: null,
      points: "",
      answerId: 0,
    };
  },
  created: function () {
    this.code = this.$route.query.code;
    this.$axios({
      method: "get",
      url: "http://139.224.50.146:80/apis/attempt",
      params: { code: this.code },
    })
      .then((response) => {
        console.log(response);
        if (response.data.success == true) {
          this.fillRight = true;
          this.login = response.data.login;
          this.locked = response.data.locked;
          if (this.login == true) {
            this.dialogFormVisible1 = true;
          } else {
            if (this.locked == true) {
              this.fillRight = true;
              this.dialogFormVisible2 = true;
            } else {
              this.$axios({
                method: "get",
                url: "http://139.224.50.146:80/apis/details",
                params: { code: this.code, visitor: true },
              })
                .then((response) => {
                  console.log(response);
                  if (response.data.success == true) {
                    this.title = response.data.title;
                    this.type = response.data.type;
                    this.description = response.data.description;
                    this.showIndex = response.data.showIndex;
                    this.questions = response.data.questions;
                    this.logic = response.data.logic;
                    for (var j = 0; j < this.questions.length; j++) {
                      this.mark.push(true);
                    }
                    for (j = 0; j < this.logic.length; j++) {
                      this.mark[this.logic[j][2]] = false;
                    }
                    this.deadlline = response.data.endTime;
                    this.shuffleId = response.data.shuffleId;
                    this.settime();
                    if (this.type == "vote") {
                      this.isVote = true;
                    } else {
                      this.isVote = false;
                    }
                    var i = 0;
                    for (i in this.questions) {
                      if (
                        this.questions[i].type == "multi-choice" ||
                        this.questions[i].type == "vote" ||
                        this.questions[i].type == "sign-up"
                      ) {
                        this.answers[i] = [];
                      }
                    }
                    while (this.answers.length < this.questions.length) {
                      this.answers.push(null);
                    }
                  } else if (response.data.message == "已填过问卷") {
                    this.submitted = true;
                    this.$axios({
                      method: "get",
                      url: "http://139.224.50.146:80/apis/results",
                      params: {
                        code: this.code,
                      },
                    }).then((response) => {
                      console.log(response);
                      if (response.data.success == true) {
                        if (response.data.conclusion == undefined) {
                          this.conclusion = "感谢您的提交!";
                        } else {
                          this.conclusion = response.data.conclusion;
                        }
                        if (response.data.results != undefined) {
                          this.results = response.data.results;
                          console.log(this.results);
                          this.points = response.data.points;
                        }
                      }
                    });
                  } else {
                    console.log(response.data.message);
                    this.$notify({
                      title: "提示",
                      message: response.data.message,
                      type: "warning",
                    });
                  }
                })
                .catch((err) => console.log(err));
            }
          }
        } else {
          console.log(response.data.message);
          this.$notify({
            title: "提示",
            message: response.data.message,
            type: "warning",
          });
        }
      })
      .catch((err) => console.log(err));
  },
  watch: {
    canvas: function () {
      console.log("load chart");
      this.loadChart();
    },
    results: {
      handler: function () {
        this.updateChart();
      },
      deep: true,
    },
  },
  mounted() {
    var el = document.getElementById("myChart");
    this.canvas = el;
    this.gaodeMap = new AMapJS.AMapLoader({
      key: "20f6820df07b227d816cb3a065241c7a",
      version: "1.4.15",
      plugins: ["AMap.CitySearch", "AMap.Geolocation"], //需要加载的插件
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
    forceSubmit: function () {},
    settime: function () {
      if (this.type === "exam") {
        /*获取服务器时间*/
        this.$axios({
          method: "get",
          url: "http://139.224.50.146/apis/time",
        }).then((res) => {
          if (res.data.success == true) {
            this.nowtime = new Date(res.data.time).getTime() / 1000;
            this.lefttime = Math.floor(
              new Date(this.deadlline).getTime() / 1000 - this.nowtime
            );

            this.lefttime++;
            this.timer = setInterval(() => {
              this.lefttime--;

              this.day = Math.floor(this.lefttime / (60 * 60 * 24));
              this.hour = Math.floor(this.lefttime / (60 * 60)) - 24 * this.day;
              this.minute =
                Math.floor(this.lefttime / 60) -
                24 * 60 * this.day -
                60 * this.hour;
              this.second =
                Math.floor(this.lefttime) -
                24 * 60 * 60 * this.day -
                60 * 60 * this.hour -
                60 * this.minute;

              if (this.lefttime == 0) {
                this.submit();
                clearInterval(this.timer);
              }
            }, 1000);
          } else {
            this.$notify({
              title: "提示",
              message: res.data.message,
              type: "error",
            });
          }
          console.log(res);
        });
      }
    },
    step: function (i) {
      return "step" + i;
    },
    goBack() {
      this.$router.push("/");
    },
    tologin() {
      this.$axios({
        method: "post",
        url: "http://139.224.50.146/apis/login",
        data: JSON.stringify(this.formData),
      }).then((res) => {
        console.log(this.formData);
        if (res.data.success == true) {
          sessionStorage.setItem("isLogin", true);
          this.$store.commit("login");
          this.$notify({
            title: "提示",
            message: "登录成功",
            type: "success",
          });
          this.dialogFormVisible1 = false;
          this.$axios({
            method: "get",
            url: "http://139.224.50.146:80/apis/attempt",
            params: { code: this.code },
          }).then((response) => {
            if (response.data.answered == true) {
              this.submitted = true;
              this.$axios({
                method: "get",
                url: "http://139.224.50.146:80/apis/results",
                params: {
                  code: this.code,
                },
              }).then((response) => {
                console.log(response);
                if (response.data.success == true) {
                  if (response.data.conclusion == undefined) {
                    this.conclusion = "感谢您的提交!";
                  } else {
                    this.conclusion = response.data.conclusion;
                  }
                  if (response.data.results != undefined) {
                    this.results = response.data.results;
                    console.log(this.results);
                    this.points = response.data.points;
                  }
                }
              });
            } else {
              this.isLogin();
            }
          });
        } else {
          this.$notify({
            title: "提示",
            message: "用户名或密码错误",
            type: "error",
          });
        }
        console.log(res);
      });
    },
    isLogin() {
      if (this.locked == true) {
        this.dialogFormVisible2 = true;
      } else {
        this.$axios({
          method: "get",
          url: "http://139.224.50.146:80/apis/details",
          params: { code: this.code, visitor: true },
        })
          .then((response) => {
            console.log(response);
            if (response.data.success == true) {
              this.title = response.data.title;
              this.type = response.data.type;
              this.description = response.data.description;
              this.questions = response.data.questions;
              this.logic = response.data.logic;
              for (var j = 0; j < this.questions.length; j++) {
                this.mark.push(true);
              }
              for (j = 0; j < this.logic.length; j++) {
                this.mark[this.logic[j][2]] = false;
              }
              this.deadlline = response.data.endTime;
              this.shuffleId = response.data.shuffleId;
              this.settime();
              var i = 0;
              for (i in this.questions) {
                if (
                  this.questions[i].type == "multi-choice" ||
                  this.questions[i].type == "vote" ||
                  this.questions[i].type == "sign-up"
                ) {
                  this.answers[i] = [];
                }
              }
            } else {
              console.log(response.data.message);
              this.$notify({
                title: "提示",
                message: response.data.message,
                type: "warning",
              });
            }
          })
          .catch((err) => console.log(err));
      }
    },
    unlock() {
      this.$axios({
        method: "get",
        url: "http://139.224.50.146:80/apis/details",
        params: {
          code: this.code,
          password: this.password,
          visitor: true,
        },
      })
        .then((response) => {
          if (response.data.success == true) {
            console.log(response);
            this.title = response.data.title;
            this.type = response.data.type;
            this.description = response.data.description;
            this.questions = response.data.questions;
            this.logic = response.data.logic;
            for (j = 0; j < this.questions.length; j++) {
              this.mark.push(true);
            }
            for (var j = 0; j < this.logic.length; j++) {
              this.mark[this.logic[j][2]] = false;
            }
            this.deadlline = response.data.endTime;
            this.shuffleId = response.data.shuffleId;
            this.settime();
            this.dialogFormVisible2 = false;
            var i = 0;
            for (i in this.questions) {
              if (
                this.questions[i].type == "multi-choice" ||
                this.questions[i].type == "vote" ||
                this.questions[i].type == "sign-up"
              ) {
                this.answers[i] = [];
              }
            }
          } else {
            if (response.data.message === "密码错误") {
              this.$notify({
                title: "提示",
                message: "问卷密码错误！",
                type: "error",
              });
            } else {
              this.$notify({
                title: "提示",
                message: response.data.message,
                type: "error",
              });
            }
          }
        })
        .catch((err) => console.log(err));
    },
    exportQuest() {
      this.$PDFSave(this.$refs.quest, this.title);
    },
    changeValue(val, index_question) {
      console.log(this.answers);
      for (var j = 0; j < this.logic.length; j++) {
        if (this.logic[j][0] == index_question) {
          this.mark[this.logic[j][2]] = false;
        }
      }

      for (j = 0; j < this.logic.length; j++) {
        if (this.logic[j][0] == index_question) {
          if (this.logic[j][1] == val) {
            this.mark[this.logic[j][2]] = true;
          }
        }
      }
      this.$forceUpdate();
    },
    multiChangeValue(index) {
      console.log(this.answers[index]);
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
              while (this.answers.length < this.questions.length) {
                this.answers.push(null);
              }
              var i = 0;
              for (i in this.questions) {
                if (
                  (this.questions[i].type == "grade" && this.answers[i] == 0) ||
                  (this.questions[i].type == "multi-choice" &&
                    this.answers[i] == [])
                ) {
                  this.answers[i] = null;
                }
              }
              for (i in this.questions) {
                if (this.questions[i].type == "grade" && this.answers[i] > 0) {
                  this.answers[i] = this.answers[i] - 1;
                }
              }
              let submitData;
              if (this.type === "exam") {
                submitData = JSON.stringify({
                  code: this.code,
                  password: this.password,
                  answers: this.answers,
                  shuffleId: this.shuffleId,
                });
              } else {
                submitData = JSON.stringify({
                  code: this.code,
                  password: this.password,
                  answers: this.answers,
                });
              }

              console.log(submitData);
              this.$axios({
                method: "post",
                url: "http://139.224.50.146:80/apis/answer",
                data: submitData,
              }).then(
                (response) => {
                  console.log(response);
                  if (response.data.success == true) {
                    this.answerId = response.data.answerId;
                    this.submitted = true;
                    this.$axios({
                      method: "get",
                      url: "http://139.224.50.146:80/apis/results",
                      params: {
                        code: this.code,
                        answerId: this.answerId,
                        shuffleId: this.shuffleId,
                      },
                    }).then((response) => {
                      console.log(response);
                      if (response.data.success == true) {
                        if (response.data.conclusion == undefined) {
                          this.conclusion = "感谢您的提交!";
                        } else {
                          this.conclusion = response.data.conclusion;
                        }
                        if (response.data.results != undefined) {
                          this.results = response.data.results;
                          console.log(this.results);
                          this.points = response.data.points;
                        }
                      }
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
            } else {
              console.log("error submit!!");
              return false;
            }
          });
        })
        .catch(() => {
          this.$notify({
            title: "提示",
            message: "已取消提交",
            type: "info",
          });
        });
    },
    loadChart: function () {
      var ctx1 = document.getElementById("myChart");
      this.myChart = new Chart(ctx1, {
        type: "bar",
        data: {
          labels: [],
          datasets: [
            {
              data: [],
              backgroundColor: [
                "rgba(2, 62, 138, 1)",
                "rgba(0, 150, 199, 1)",
                "rgba(72, 202, 228, 1)",
                "rgba(144, 224, 239, 1)",
                "rgba(173, 232, 244, 1)",
                "rgba(202, 240, 248, 1)",
                "rgba(68, 108, 179, 1)",
                "rgba(52, 152, 219, 1)",
                "rgba(89, 171, 227, 1)",
                "rgba(137, 196, 244, 1)",
              ],
            },
          ],
        },
      });
    },
    updateChart: function (item) {
      this.loadChart();
      console.log("update", item);
      this.myChart.data.labels = item["answers"];
      this.myChart.data.datasets[0].data = item["counts"];
      this.myChart.data.datasets[0].label = item["stem"];
      this.myChart.update();
    },
  },
  directives: {
    focus: {
      inserted: function (el) {
        el.querySelector("input").focus();
      },
    },
  },
};
</script>

<style scoped>
#quest {
  height: 100%;
}
.voted {
  height: 80%;
  display: flex;
  justify-content: center;
  align-items: center;
}
.results {
  display: flex;
  justify-content: space-between;
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
.conclusion {
  font-size: 20px;
}
.chart {
  height: 280px;
  width: 560px;
}
</style>