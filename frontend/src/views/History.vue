<template>
  <div class="reviewer">
    <div class="top">
      <div class="search">
        <el-dropdown trigger="click">
          <span class="el-dropdown-link">
            <el-button><i class="el-icon-s-operation"></i></el-button>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              ><el-button type="text" class="button" @click="creationTime()"
                >创建时间</el-button
              ></el-dropdown-item
            >
            <el-dropdown-item
              ><el-button type="text" class="button" @click="releaseTime()"
                >发布时间</el-button
              ></el-dropdown-item
            >
            <el-dropdown-item
              ><el-button type="text" class="button" @click="duration()"
                >持续时间</el-button
              ></el-dropdown-item
            >
          </el-dropdown-menu>
        </el-dropdown>
        <el-input
          v-model.trim="search"
          style="width: 250px"
          clearable
          placeholder="请输入要搜索的问卷"
        />
        <el-button icon="el-icon-search" @click="searchQuest"></el-button>
      </div>
    </div>
    <div class="questionnaire">
      <div class="list" style="margin-left: 1%; margin-right: 1%">
        <div
        class="cards"
        v-for="item in searchQue"
        :key="item.templateId">
          <el-card shadow="hover">
            <div class="card">
              <div class="banner">
                <div class="title">
                  <div v-if="item.released == true">
                    {{ item.title }}(已发布)
                  </div>
                  <div v-else>{{ item.title }}(未发布)</div>
                </div>
                <div class="type_show">
                  <question-pic></question-pic>
                </div>
              </div>
              <div class="time">
                <time> 创建时间:{{ item.creationTime }} </time>
              </div>
              <div class="time" v-if="item.releaseTime != ''">
                <time> 最后发布:{{ item.releaseTime }} </time>
              </div>
              <div class="time" v-else>
                <time> 最后发布:未曾发布 </time>
              </div>
              <div class="time">
                <time> 收集时长:{{ item.duration }} </time>
              </div>
              <div class="time">
                <strong>收集数量:</strong>{{ item.answerCount }}
              </div>
              <div class="bottom clearfix">
                <el-button-group>
                  <el-button
                    type="text"
                    class="button"
                    @click="adjust(item)"
                    icon="el-icon-edit"
                  ></el-button>
                  <el-button
                    type="text"
                    class="button"
                    @click="statistics(item)"
                    icon="el-icon-pie-chart"
                  ></el-button>
                  <el-button
                    type="text"
                    class="button"
                    @click="release(item)"
                    icon="el-icon-video-play"
                    v-if="item.released == false"
                  ></el-button>
                  <el-button
                    type="text"
                    class="button"
                    @click="close(item)"
                    icon="el-icon-video-pause"
                    v-else
                  ></el-button>
                  <el-dropdown>
                    <el-button
                      type="text"
                      icon="el-icon-more"
                      style="color: black"
                    ></el-button>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item>
                        <el-button
                          type="text"
                          class="button"
                          @click="edit(item)"
                          icon="el-icon-edit-outline"
                          >编辑</el-button
                        >
                      </el-dropdown-item>
                      <el-dropdown-item
                        ><el-button
                          type="text"
                          class="button"
                          @click="remove(item)"
                          icon="el-icon-delete"
                          >删除</el-button
                        ></el-dropdown-item
                      >
                      <el-dropdown-item
                        ><el-button
                          type="text"
                          class="button"
                          @click="clone(item)"
                          icon="el-icon-document-copy"
                          >复制</el-button
                        ></el-dropdown-item
                      >
                      <el-dropdown-item
                        ><el-button
                          type="text"
                          class="button"
                          @click="preview(item)"
                          icon="el-icon-view"
                          >预览</el-button
                        ></el-dropdown-item
                      >
                      <el-dropdown-item>
                        <el-button 
                        type="text"
                        class="button"
                        @click="qr(item)"
                        icon="el-icon-share">分享
                        </el-button>
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
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </el-button-group>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import svg from "../components/svg-history.vue";
import VueQr from "vue-qr";
import Clipboard from "clipboard";
export default {
  components: {
    "question-pic": svg,
    VueQr,
  },
  data() {
    return {
      search: "",
      quest: 0,
      total: 0,
      searchQue: [],
      templateId: 0,
      allQuest: [],
      qrData: {
        text: window.location.host + "/fill?templateId=" + this.templateId,
        logo: require("../assets/logo.png"),
      },
      exportLink: "",
      downloadFilename: "",
      dialogVisible: false,
    };
  },
  created() {
    this.convert();
    this.searchQuest();
  },
  methods: {
    convert: function () {
      this.$axios({
        method: "get",
        url: "http://139.224.50.146:80/apis/all",
        params: {
          removed: false,
        },
      }).then((res) => {
        console.log(res);
        if (res.data.templates.length != 0) {
          this.allQuest = res.data.templates;
        } else {
          this.allQuest = [];
        }
        console.log(this.allQuest);
        this.searchQue = this.allQuest;
        console.log(this.searchQue);
        this.total = this.searchQue.length;
      });
    },
    creationTime() {
      this.searchQue = this.searchQue.sort(function (a, b) {
        if (a.creationTime < b.creationTime) {
          return -1;
        } else if (a.creationTime == b.creationTime) {
          return 0;
        } else {
          return 1;
        }
      });
    },
    releaseTime() {
      this.searchQue = this.searchQue.sort(function (a, b) {
        if (a.releaseTime < b.releaseTime) {
          return -1;
        } else if (a.releaseTime == b.releaseTime) {
          return 0;
        } else {
          return 1;
        }
      });
    },
    duration() {
      this.searchQue = this.searchQue.sort(function (a, b) {
        if (a.duration < b.duration) {
          return -1;
        } else if (a.duration == b.duration) {
          return 0;
        } else {
          return 1;
        }
      });
    },
    searchQuest() {
      if (this.search == "") {
        this.searchQue = this.allQuest;
      } else {
        this.searchQue = [];
        let regStr = "";
        // 初始化正则表达式
        regStr = ".*" + this.search + ".*"; //跨字匹配
        let reg = new RegExp(regStr);
        console.log(reg);
        for (let i = 0; i < this.allQuest.length; i++) {
          let name = this.allQuest[i].title; //按照名字匹配
          let regMatch = name.match(reg);
          if (null !== regMatch) {
            // 将匹配的数据放入结果列表中
            this.searchQue.push(this.allQuest[i]);
          }
        }
      }
      this.total = this.searchQue.length;
    },
    adjust(item) {
      if (item.released == false) {
        this.quest = item.templateId;
        console.log(this.quest);
        this.$router.push("/adjust?templateId=" + this.quest);
      } else {
        this.$message({
          message: "问卷已发布！",
        });
      }
    },
    edit(item) {
      if (item.type == "normal") {
        if (item.released == false) {
          this.quest = item.templateId;
          console.log(this.quest);
          this.$router.push("/normal/edit?templateId=" + this.quest);
        } else {
          this.$message({
            message: "问卷已发布！",
          });
        }
      } else if (item.type == "vote") {
        if (item.released == false) {
          this.quest = item.templateId;
          console.log(this.quest);
          this.$router.push("/vote/edit?templateId=" + this.quest);
        } else {
          this.$message({
            message: "问卷已发布！",
          });
        }
      } else if (item.type == "sign-up") {
        if (item.released == false) {
          this.quest = item.templateId;
          console.log(this.quest);
          this.$router.push("/apply/edit?templateId=" + this.quest);
        } else {
          this.$message({
            message: "问卷已发布！",
          });
        }
      }
    },
    release(item) {
      this.$axios({
        method: "post",
        url: "http://139.224.50.146:80/apis/release",
        data: JSON.stringify({
          templateId: item.templateId,
        }),
      }).then(
        (response) => {
          console.log(response);
          if (response.data.success == true) {
            this.$message({
              message: "问卷发布成功！",
              type: "success",
            });
            this.templateId = item.templateId;
            this.qrData.text =
              window.location.host + "/fill?templateId=" + this.templateId;
            this.dialogVisible = true;
            item.released = true;
          }
        },
        (err) => {
          alert(err);
        }
      );
    },
    qr(item) {
      this.templateId = item.templateId;
      this.qrData.text =
        window.location.host + "/fill?templateId=" + this.templateId;
      this.dialogVisible = true;
    },
    close(item) {
      this.$axios({
        method: "post",
        url: "http://139.224.50.146:80/apis/close",
        data: JSON.stringify({
          templateId: item.templateId,
        }),
      }).then(
        (response) => {
          console.log(response);
          if (response.data.success == true) {
            this.$message({
              message: "问卷关闭成功！",
              type: "success",
            });
            location.reload();
          }
        },
        (err) => {
          alert(err);
        }
      );
    },
    clone(item) {
      this.$axios({
        method: "post",
        url: "http://139.224.50.146:80/apis/clone",
        data: JSON.stringify({
          templateId: item.templateId,
        }),
      }).then(
        (response) => {
          console.log(response);
          if (response.data.success == true) {
            this.$message({
              message: "问卷复制成功！",
              type: "success",
            });
            location.reload();
          }
        },
        (err) => {
          alert(err);
        }
      );
    },
    remove(item) {
      this.$confirm("此操作将删除该问卷, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$axios({
            method: "post",
            url: "http://139.224.50.146:80/apis/remove",
            data: JSON.stringify({
              templateId: item.templateId,
            }),
          }).then(
            (response) => {
              console.log(response);
              if (response.data.success == true) {
                this.$message({
                  message: "问卷已删除。",
                  type: "success",
                });
                location.reload();
              }
            },
            (err) => {
              alert(err);
            }
          );
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除",
          });
        });
    },
    preview(item) {
      this.quest = item.templateId;
      console.log(this.quest);
      this.$router.push("/preview?templateId=" + this.quest);
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
    statistics(item) {
      this.quest = item.templateId;
      console.log(this.quest);
      this.$router.push("/statistics?templateId=" + this.quest);
    }
  },
};
</script>

<style scoped>
.top {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  margin-bottom: 10px;
}
.list {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  flex-flow: row wrap;
}
.cards {
  width: 16%;
  margin-right: 5px;
  margin-bottom: 5px;
}
.card {
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: rgba(233, 233, 233, 0.5);
}
.banner {
  position: relative;
  overflow: hidden;
  width: 100%;
  background-color: rgba(71, 157, 230, 0.94);
  padding-top: 10px;
  padding-bottom: 10px;
  zoom: 1;
}
.el-card /deep/.el-card__body {
  padding: 0;
}
.time {
  align-self: flex-start;
  font-size: 14px;
  margin: 0;
  margin-top: 5px;
}
.type_show {
  margin-top: 0px;
}
.title {
  margin-bottom: 10px;
}
.button {
  color: black;
}
.share {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
.clearfix .el-button {
  width: 45px;
}
</style>