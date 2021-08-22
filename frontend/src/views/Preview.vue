<template>
  <div>
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
        <div v-for="(item, index_question) in questions" :key="index_question">
          <div class="question-title">
            <div>第{{ index_question + 1 }}题,题目:{{ item.stem }}</div>
            <a>题目描述：{{ item.description }}</a>
          </div>
          <div class="question-content">
            <div v-if="item.type == 'choice'">
              <el-radio-group v-model="answers[index_question]" v-for="(i, index) in item.choices" :key="index" @change="changeValue">
                <el-radio :label="index">{{ i }}</el-radio>
              </el-radio-group>
            </div>
            <div v-if="item.type == 'multi-choice'">
              <el-checkbox-group
              v-model="multi" 
              v-for="(i, index) in item.choices"
              :min = "item.min"
              :max = "item.max"
              :key="index" 
              @change="multiChangeValue(index_question)">
                <el-checkbox :label="index" border>{{ i }}</el-checkbox>
              </el-checkbox-group>
            </div>
            <div v-if="item.type == 'filling'">
              <el-input
              type="textarea"
              class="input"
              :rows="item.height"
              :style="{'--width': item.width}"
              placeholder="请输入内容"
              v-model="answers[index_question]">
              </el-input>
            </div>
            <div v-if="item.type == 'grade'">
              <el-radio-group v-model="answers[index_question]" v-for="(i, index) in item.choices" :key="index" @change="changeValue">
                <el-radio :label="index">{{ i }}({{item.scores[ index ]}})</el-radio>
              </el-radio-group>
            </div>
            <div v-if="item.type == 'dropdown'">
              <el-select v-model="answers[index_question]" clearable placeholder="请选择">
                <el-option
                  v-for="(i, index) in item.choices"
                  :key="index"
                  :label="i"
                  :value="index">
                </el-option>
              </el-select>
            </div>
          </div>
        </div>
      </div>
    </div>

    <br />
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
</template>

<script>
export default {
  data() {
    return {
      templateId: 0,
      locked: false,
      title: "问卷标题",
      type: "normal",
      description: "问卷描述",
      password: "",
      questions: [
        {
          type: "choice",
          stem: "1",
          description: "testtesttesttesttest",
          required: true,
          choices: [
            "hello","question","whats up"
          ],
        },
        {
          type: "choice",
          stem: "666",
          description: "testtesttesttesttest",
          required: true,
          choices: [
            "hello","question","whats up"
          ],
        },
        {
          type: "multi-choice",
          stem: "2",
          description: "testtesttesttesttest",
          required: true,
          choices: [
            "question","hello","whats up"
          ],
          max: 2,
          min: 0,
        },
        {
          type: "filling",
          stem: "3",
          description: "testtesttesttesttest",
          required: true,
          height: 3,
          width: '1000px',
        },
        {
          type: "grade",
          stem: "4",
          description: "testtesttesttesttest",
          required: true,
          choices: [
            "good","very good","very very good"
          ],
          scores: [
            1,2,3
          ]
        },
        {
          type: "dropdown",
          stem: "5",
          description: "testtesttesttesttest",
          required: true,
          choices: [
            "hello","question","whats up"
          ]
        }
      ],
      multi: [],
      answers: []
    };
  },
  created: function () {
    this.templateId = this.$route.query.templateId;
    if (this.templateId == undefined) this.templateId = 0;
    console.log(this.templateId);
    this.$axios({
      method: "get",
      url: "http://139.224.50.146:80/apis/locked",
      params: { templateId: this.templateId },
    })
      .then((response) => {
        console.log(response);
        if (response.data.success == true) {
          this.locked = response.data.locked;
        } else {
          console.log(response.data.message);
        }
      })
      .catch((err) => console.log(err));
    this.$axios({
      method: "get",
      url: "http://139.224.50.146:80/apis/details",
      params: { templateId: this.templateId, password: "" },
    })
      .then((response) => {
        console.log(response);
        if (response.data.success == true) {
          this.title = response.data.title;
          this.type = response.data.type;
          this.description = response.data.description;
          this.password = response.data.password;
          this.questions = response.data.questions;
        } else {
          console.log(response.data.message);
        }
      })
      .catch((err) => console.log(err));
  },
  methods: {
    exportQuest() {
      this.$PDFSave(this.$refs.quest, this.title);
    },
    changeValue() {
      console.log(this.answers)
    },
    multiChangeValue(index) {
      this.answers[index] = this.multi
      console.log(this.multi)
    }
  },
};
</script>

<style scoped>
.question-content .input {
  width: var(--width)
}
</style>