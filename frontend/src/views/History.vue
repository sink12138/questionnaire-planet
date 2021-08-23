<template>
  <div class="reviewer">
    <div class="top">
      <div class="page-title">您的所有问卷如下</div>
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
          v-for="item in searchQue.slice(
            (current_page - 1) * pagesize,
            current_page * pagesize
          )"
          :key="item.templateId"
        >
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
                <time> 发布时间:{{ item.releaseTime }} </time>
              </div>
              <div class="time" v-else>
                <time> 发布时间:未曾发布 </time>
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
                    </el-dropdown-menu>
                  </el-dropdown>
                </el-button-group>
              </div>
            </div>
          </el-card>
        </div>
        <!-- 该div是做分页的 -->
        <div class="block">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="current_page"
            :page-sizes="[4, 8, 12, 16]"
            :page-size="pagesize"
            layout="total, sizes, prev, pager, next"
            :total="total"
          >
          </el-pagination>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import svg from "../components/svg-history.vue";
export default {
  components: {
    "question-pic": svg,
  },
  data() {
    return {
      search: "",
      quest: 0,
      current_page: 1,
      total: 0,
      pagesize: 12,
      searchQue: [],
      allQuest: [
        {
          duration: "00:00:00",
          creationTime: "2021-08-21 21:00",
          releaseTime: "2021-08-21 21:00",
          templateId: 1,
          type: "normal",
          title: "测试问卷",
          released: false,
        },
        {
          duration: "00:00:00",
          creationTime: "2021-08-21 21:00",
          releaseTime: "2021-08-21 21:00",
          templateId: 1,
          type: "normal",
          title: "测试问卷",
          released: false,
        },
        {
          duration: "00:00:00",
          creationTime: "2021-08-21 21:00",
          releaseTime: "2021-08-21 21:00",
          templateId: 1,
          type: "normal",
          title: "测试问卷",
          released: false,
        },
        {
          duration: "00:00:00",
          creationTime: "2021-08-21 21:00",
          releaseTime: "2021-08-21 21:00",
          templateId: 1,
          type: "normal",
          title: "测试问卷",
          released: false,
        },
        {
          duration: "00:00:00",
          creationTime: "2021-08-21 21:00",
          releaseTime: "2021-08-21 21:00",
          templateId: 1,
          type: "normal",
          title: "测试问卷",
          released: false,
        },
        {
          duration: "00:00:00",
          creationTime: "2021-08-21 21:00",
          releaseTime: "2021-08-21 21:00",
          templateId: 1,
          type: "normal",
          title: "测试问卷",
          released: false,
        },
        {
          duration: "00:00:00",
          creationTime: "2021-08-21 21:00",
          releaseTime: "2021-08-21 21:00",
          templateId: 1,
          type: "normal",
          title: "测试问卷",
          released: false,
        },
        {
          duration: "00:00:00",
          creationTime: "2021-08-21 21:00",
          releaseTime: "2021-08-21 21:00",
          templateId: 1,
          type: "normal",
          title: "测试问卷",
          released: false,
        },
        {
          duration: "00:00:00",
          creationTime: "2021-08-21 21:00",
          releaseTime: "2021-08-21 21:00",
          templateId: 1,
          type: "normal",
          title: "测试问卷",
          released: false,
        },
        {
          duration: "00:00:00",
          creationTime: "2021-08-21 21:00",
          releaseTime: "2021-08-21 21:00",
          templateId: 1,
          type: "normal",
          title: "测试问卷",
          released: false,
        },
        {
          duration: "00:00:00",
          creationTime: "2021-08-21 21:00",
          releaseTime: "2021-08-21 21:00",
          templateId: 1,
          type: "normal",
          title: "测试问卷",
          released: false,
        },
        {
          duration: "00:00:00",
          creationTime: "2021-08-21 21:00",
          releaseTime: "2021-08-21 21:00",
          templateId: 1,
          type: "normal",
          title: "测试问卷",
          released: false,
        },
        {
          duration: "00:00:00",
          creationTime: "2021-08-21 21:00",
          releaseTime: "2021-08-21 21:00",
          templateId: 1,
          type: "normal",
          title: "测试问卷",
          released: false,
        },
        {
          duration: "00:00:00",
          creationTime: "2021-08-21 21:00",
          releaseTime: "2021-08-21 21:00",
          templateId: 1,
          type: "normal",
          title: "测试问卷",
          released: false,
        },
      ],
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
    handleSizeChange: function (size) {
      this.pagesize = size;
    },
    handleCurrentChange: function (currentPage) {
      this.current_page = currentPage;
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
            location.reload();
          }
        },
        (err) => {
          alert(err);
        }
      );
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
  },
};
</script>

<style scoped>
.top {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-bottom: 10px;
}
.page-title {
  font-weight: bolder;
  font-size: 24px;
  flex-grow: 50;
}
.search {
  flex-grow: 1;
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
.clearfix .el-button {
  width: 45px;
}
.block {
  position: absolute;
  bottom: 5px;
  left: 40%;
}
</style>