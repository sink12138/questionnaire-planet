<template>
  <div>
    <div id="quest" ref="quest">
      <div class="head">
        <h1>
          {{ title }}
        </h1>
        <a>
          {{ description }}
        </a>
        <br />
      </div>
      <div class="question">
        <ul id="quests">
          <li v-for="(item, index) in questions" :key="index">
            <div class="question-title">
              <h3>第{{ index + 1 }}题,题目:{{ item.stem }}</h3>
              <a>题目描述：{{ item.description }}</a>
            </div>
            <div class="question-content">
              <div v-if="item.type == 'choice'"></div>
              <div v-elif="item.type == 'multi-choice'"></div>
              <div v-elif="item.type == 'filling'"></div>
              <div v-elif="item.type == 'grade'"></div>
              <div v-elif="item.type == 'dropdown'"></div>
            </div>
          </li>
        </ul>
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
      questions: [],
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
  },
};
</script>