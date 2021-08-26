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
            id="email"
            v-model="formData.email"
            autocomplete="off"
            style="width: 300px"
            placeholder="请输入您的电子邮箱"
            v-focus
            @keyup.enter.native="pressEnter"
          ></el-input>
        </el-form-item>
        <el-form-item
          label="密码"
          :label-width="formLabelWidth"
          prop="password"
        >
          <el-input
            id="password"
            v-model="formData.password"
            autocomplete="off"
            show-password
            style="width: 300px"
            placeholder="请输入密码"
            @keyup.enter.native="pressEnter"
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
            v-focus
            @keyup.enter.native="pressEnter"
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
            ><div v-show="showIndex">第{{ index_question + 1 }}题</div></el-divider
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
                @change="changeValue">
                  <el-radio class="option" :label="index">
                    {{ i }}({{ item.scores[index] }})
                  </el-radio>
                </el-radio-group>
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
                  <el-checkbox class="option" :label="index" border :disabled="item.remains[index]==0?true:false"
                    >{{ i }}共{{ item.quotas[index] }},剩余{{
                      item.remains[index]
                    }}
                  </el-checkbox>
                </el-checkbox-group>
              </el-form-item>
            </div>
          </div>
        </div>
      </el-form>
    </div>
    <div class="conclusion" v-if="submitted == true">{{ conclusion }}</div>
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
    <div class="submit" v-if="fillRight == true">
      <el-button @click="submit()" v-if="submitted == false"
        >提交问卷</el-button
      >
    </div>
  </div>
</template>

<script>
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
      templateId: 0,
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
      multi: [],
      answers: [],
      myChart: null,
      canvas: null,
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
          this.fillRight = true;
          this.login = response.data.login;
          this.locked = response.data.locked;
          if (this.login == true) {
            this.dialogFormVisible1 = true;
          } else {
            if (this.locked == true) {
              console.log(22);
              this.fillRight = true;
              this.dialogFormVisible2 = true;
            } else {
              console.log(33);
              
              this.$axios({
                method: "get",
                url: "http://139.224.50.146:80/apis/details",
                params: { templateId: this.templateId, visitor: true },
              })
                .then((response) => {
                  console.log(response);
                  if (response.data.success == true) {
                    this.title = response.data.title;
                    this.type = response.data.type;
                    this.description = response.data.description;
                    this.showIndex = response.data.showIndex;
                    this.questions = response.data.questions;
                  } else {
                    console.log(response.data.message);
                    this.$message({
                      message: response.data.message,
                      type: "warning",
                      showClose: true
                    });
                  }
                })
                .catch((err) => console.log(err));
            }
          }
        } else {
          console.log(response.data.message);
          this.$message({
            message: response.data.message,
            type: "warning",
            showClose: true
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
  },
  methods: {
    pressEnter: function () {
      if (this.dialogFormVisible1 == true) {
        this.tologin();
        console.log(1111);
        
      }
      if (this.dialogFormVisible2 == true) {
        this.unlock();
        console.log("???");
      }
    },
    step: function (i) {
      return "step" + i;
    },
    goBack() {
      this.$router.push("/");
    },
    tologin() {
      document.getElementById("email").blur();
      document.getElementById("password").blur();

      this.$axios({
        method: "post",
        url: "http://139.224.50.146/apis/login",
        data: JSON.stringify(this.formData),
      }).then((res) => {
        console.log(this.formData);
        if (res.data.success == true) {
          sessionStorage.setItem("isLogin", true);
          this.$store.commit("login");
          this.$message({
            message: "登录成功！",
            type: "success",
          });
          this.dialogFormVisible1 = false;
          this.isLogin();
        } else {
          alert("用户名或密码错误！");
        }
        console.log(res);
      });
    },
    isLogin() {
      if (this.locked == true) {
        console.log(22);
        this.dialogFormVisible2 = true;
      }
      else {
        console.log(33);
        this.$axios({
          method: "get",
          url: "http://139.224.50.146:80/apis/details",
          params: { templateId: this.templateId, visitor: true },
        })
          .then((response) => {
            console.log(response);
            if (response.data.success == true) {
              this.title = response.data.title;
              this.type = response.data.type;
              this.description = response.data.description;
              this.questions = response.data.questions;
            } else {
              console.log(response.data.message);
              this.$message({
                message: response.data.message,
                type: "warning",
                showClose: true
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
        params: { templateId: this.templateId, password: this.password, visitor: true },
      })
        .then((response) => {
          console.log(response);
          if (response.data.success == true) {
            if (response.data.password == this.password) {
              this.title = response.data.title;
              this.type = response.data.type;
              this.description = response.data.description;
              this.questions = response.data.questions;
              this.dialogFormVisible2 = false;
            } else {
              alert("问卷密码错误！");
            }
          } else {
            console.log(response.data.message);
            this.$message({
              message: response.data.message,
              type: "warning",
              showClose: true
            });
            this.dialogFormVisible2 = false;
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
                    if (response.data.results == undefined) {
                      this.isVote = false;
                    } else {
                      this.isVote = true;
                      this.results = response.data.results;
                      console.log(this.results);
                    }
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
    loadChart: function() {
      var ctx1 = document.getElementById('myChart');
      this.myChart = new Chart(ctx1, {
        type: 'bar',
        data: {
          labels: [],
          datasets: [
            {
              data: [],
              backgroundColor: [
                'rgba(2, 62, 138, 1)',
                'rgba(0, 150, 199, 1)',
                'rgba(72, 202, 228, 1)',
                'rgba(144, 224, 239, 1)',
                'rgba(173, 232, 244, 1)',
                'rgba(202, 240, 248, 1)',
                'rgba(68, 108, 179, 1)',
                'rgba(52, 152, 219, 1)',
                'rgba(89, 171, 227, 1)',
                'rgba(137, 196, 244, 1)'
              ],
            },
          ],
        },
      });
    },
    updateChart: function(item) {
      this.loadChart()
      console.log('update',item)
      this.myChart.data.labels = item['answers']
      this.myChart.data.datasets[0].data = item['counts']
      this.myChart.data.datasets[0].label = item['stem']
      this.myChart.update()
    }
  },
  directives: {
    focus: {
      inserted: function(el) {
        el.querySelector('input').focus();
      }
    }
  }
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