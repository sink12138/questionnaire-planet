<template>
  <div class="history">
    <div class="top">
      <div class="search">
        <el-input
        v-model.trim="search"
        style="width: 320px"
        clearable
        placeholder="请输入要搜索的问卷">
          <el-dropdown trigger="click" slot="prepend" placement="bottom">
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
          <el-button slot="append" icon="el-icon-search" @click="searchQuest"></el-button>
        </el-input>
      </div>
      <ButtonGroup size="large">
        <Button icon="md-menu"></Button>
        <Button icon="ios-apps"></Button>
      </ButtonGroup>
    </div>
    <div class="questionnaire">
      <div class="table" style="margin-left: 1%; margin-right: 1%">
        <el-table :data="searchQue" border style="width: 100%">
            <el-table-column fixed prop="title" label="标题" width="150">
            </el-table-column>
            <el-table-column
              label="状态"
              width="100"
            >
              <template slot-scope="scope">
                  <p>{{ scope.row.released == true ? '正在回收':'未发布' }}</p>
              </template>
            </el-table-column>
            <el-table-column
              prop="answerCount"
              label="收集数量"
              width="100"
            >
            </el-table-column>
            <el-table-column
              prop="creationTime"
              label="创建时间"
              width="150"
            >
            </el-table-column>
            <el-table-column
              prop="releaseTime"
              label="最后发布"
              width="150"
            >
            </el-table-column>
            <el-table-column prop="duration" label="收集时长" width="150">
            </el-table-column>
          <div class="bottom clearfix">
            <el-table-column label="操作" width="500" fixed="right">
              <template slot-scope="scope">
                <el-button
                  @click="adjust(scope.row)"
                  type="text"
                  icon="el-icon-edit"
                  >微调</el-button
                >
                <el-button
                  type="text"
                  @click="statistics(scope.row)"
                  icon="el-icon-pie-chart"
                  >数据分析</el-button
                >
                <el-button
                  type="text"
                  @click="release(scope.row)"
                  icon="el-icon-video-play"
                  v-if="scope.row.released == false"
                  >发布</el-button
                >
                <el-button
                  type="text"
                  @click="close(scope.row)"
                  icon="el-icon-video-pause"
                  v-else
                  >关闭</el-button
                >
                <el-dropdown placement="bottom">
                  <el-button
                    type="text"
                    icon="el-icon-more"
                    style="color: black"
                    >更多操作</el-button
                  >
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item>
                      <el-button
                        type="text"
                        class="button"
                        @click="edit(scope.row)"
                        icon="el-icon-edit-outline"
                        >编辑问卷</el-button
                      >
                    </el-dropdown-item>
                    <el-dropdown-item
                      ><el-button
                        type="text"
                        class="button"
                        @click="remove(scope.row)"
                        icon="el-icon-delete"
                        >删除问卷</el-button
                      ></el-dropdown-item
                    >
                    <el-dropdown-item
                      ><el-button
                        type="text"
                        class="button"
                        @click="clone(scope.row)"
                        icon="el-icon-document-copy"
                        >复制问卷</el-button
                      ></el-dropdown-item
                    >
                    <el-dropdown-item
                      ><el-button
                        type="text"
                        class="button"
                        @click="preview(scope.row)"
                        icon="el-icon-view"
                        >预览问卷</el-button
                      ></el-dropdown-item
                    >
                    <el-dropdown-item
                      ><el-button
                        type="text"
                        class="button"
                        @click="updateCode(scope.row)"
                        icon="el-icon-refresh"
                        >更新链接</el-button
                      ></el-dropdown-item
                    >
                    <el-dropdown-item>
                      <el-button
                        type="text"
                        class="button"
                        @click="qr(scope.row)"
                        icon="el-icon-share"
                        >分享问卷
                      </el-button>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </template>
            </el-table-column>
          </div>
        </el-table>
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
  </div>
</template>

<script>
import VueQr from "vue-qr";
import Clipboard from "clipboard";
export default {
  components: {
    VueQr,
  },
  data() {
    return {
      code: "",
      search: "",
      quest: 0,
      total: 0,
      searchQue: [],
      templateId: 0,
      allQuest: [
        {released: false},{released: true},{released: false},
      ],
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
        var i = 0;
        for (i in this.allQuest) {
          if (this.allQuest[i].releaseTime == undefined) {
            this.allQuest[i].releaseTime = "未曾发布";
          }
        }
        console.log(this.allQuest);
        this.searchQue = this.allQuest;
        console.log(this.searchQue);
        this.total = this.searchQue.length;
      });
    },
    creationTime() {
      this.searchQue = this.searchQue.sort(function (a, b) {
        if (a.creationTime > b.creationTime) {
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
        if (a.releaseTime > b.releaseTime) {
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
    adjust(row) {
      if (row.released == false) {
        this.code = row.code;
        this.quest = row.templateId;
        console.log(this.quest);
        this.$router.push(
          "/adjust?templateId=" + this.quest + "&code=" + this.code
        );
      } else {
        this.$message({
          message: "问卷已发布！",
        });
      }
    },
    edit(row) {
      if (row.type == "normal") {
        if (row.released == false) {
          this.code = row.code;
          this.quest = row.templateId;
          console.log(this.quest);
          this.$router.push(
            "/normal/edit?templateId=" + this.quest + "&code=" + this.code
          );
        } else {
          this.$message({
            message: "问卷已发布！",
          });
        }
      } else if (row.type == "vote") {
        if (row.released == false) {
          this.code = row.code;
          this.quest = row.templateId;
          console.log(this.quest);
          this.$router.push(
            "/vote/edit?templateId=" + this.quest + "&code=" + this.code
          );
        } else {
          this.$message({
            message: "问卷已发布！",
          });
        }
      } else if (row.type == "sign-up") {
        if (row.released == false) {
          this.code = row.code;
          this.quest = row.templateId;
          console.log(this.quest);
          this.$router.push(
            "/apply/edit?templateId=" + this.quest + "&code=" + this.code
          );
        } else {
          this.$message({
            message: "问卷已发布！",
          });
        }
      } else if (row.type == "exam") {
        if (row.released == false) {
          this.code = row.code;
          this.quest = row.templateId;
          console.log(this.quest);
          this.$router.push(
            "/exam/edit?templateId=" + this.quest + "&code=" + this.code
          );
        } else {
          this.$message({
            message: "问卷已发布！",
          });
        }
      }
    },
    release(row) {
      if (row.scheduled == true) {
        this.$confirm("此操作将取消该问卷自动发布, 是否继续?", "提示", {
          confirmButtonText: "发布",
          cancelButtonText: "取消",
          type: "warning",
        })
          .then(() => {
            this.$axios({
              method: "post",
              url: "http://139.224.50.146:80/apis/release",
              data: JSON.stringify({
                templateId: row.templateId,
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
                  row.released = true;
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
              message: "已取消发布",
            });
          });
      } else {
        this.$axios({
          method: "post",
          url: "http://139.224.50.146:80/apis/release",
          data: JSON.stringify({
            templateId: row.templateId,
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
              row.released = true;
            }
          },
          (err) => {
            alert(err);
          }
        );
      }
    },
    updateCode(row) {
      this.$axios({
        method: "post",
        url: "http://139.224.50.146:80/apis/code",
        data: JSON.stringify({
          templateId: row.templateId,
        }),
      }).then(
        (response) => {
          console.log(response);
          if (response.data.success == true) {
            this.$message({
              message: "更新链接成功！",
              type: "success",
            });
            row.code = response.data.code;
            this.qrData.text = window.location.host + "/fill?code=" + row.code;
            this.dialogVisible = true;
          }
        },
        (err) => {
          alert(err);
        }
      );
    },
    qr(row) {
      this.code = row.code;
      this.qrData.text = window.location.host + "/fill?code=" + this.code;
      this.dialogVisible = true;
    },
    close(row) {
      this.$axios({
        method: "post",
        url: "http://139.224.50.146:80/apis/close",
        data: JSON.stringify({
          templateId: row.templateId,
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
    clone(row) {
      this.$axios({
        method: "post",
        url: "http://139.224.50.146:80/apis/clone",
        data: JSON.stringify({
          templateId: row.templateId,
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
    remove(row) {
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
              templateId: row.templateId,
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
    preview(row) {
      this.quest = row.templateId;
      this.code = row.code;
      console.log(this.quest);
      this.$router.push(
        "/preview?templateId=" + this.quest + "&code=" + this.code
      );
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
    statistics(row) {
      this.quest = row.templateId;
      console.log(this.quest);
      this.$router.push("/statistics?templateId=" + this.quest);
    },
  },
};
</script>

<style scoped>
.history {
  margin-left: 60px;
}
.top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  margin: 15px;
}
.list {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  flex-flow: row wrap;
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