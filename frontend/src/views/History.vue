<template>
  <div class="reviewer">
    <div>
      <h1>您的所有问卷如下</h1>
    </div>
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
      <el-button icon="el-icon-search" circle @click="searchQuest"></el-button>
    </div>
    <div class="questionnaire">
      <div style="margin-left: 1%; margin-right: 1%">
        <el-row>
          <el-col
            :span="4"
            v-for="item in searchQue.slice(
              (current_page - 1) * pagesize,
              current_page * pagesize
            )"
            :key="item.templateId"
            :offset="1"
          >
            <div style="margin-top: 15px">
              <el-card :body-style="{ padding: '0px' }">
                <div class="card">
                  <br />
                  <div class="title">
                    <span
                      ><strong>{{ item.title }}</strong></span
                    ><br />
                  </div>
                  <br />
                  <div class="type_show">
                    <question-pic></question-pic>
                  </div>
                  <br />
                  <span
                    ><time class="time"
                      ><strong>创建时间:</strong>{{ item.creationTime }}</time
                    ></span
                  ><br />
                  <span v-if="item.releaseTime != ''"
                    ><time class="time"
                      ><strong>发布时间:</strong>{{ item.releaseTime }}</time
                    ></span
                  >
                  <span v-else
                    ><time class="time"
                      ><strong>发布时间:</strong>未曾发布</time
                    ></span
                  ><br />
                  <span
                    ><time class="time"
                      ><strong>收集时长:</strong>{{ item.duration }}</time
                    ></span
                  ><br />
                  <div class="bottom clearfix">
                    <div v-if="item.released == true">已发布</div>
                    <div v-else>未发布</div>
                    <el-button
                      type="text"
                      class="button"
                      @click="edit(item)"
                      icon="el-icon-edit-outline"
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
                    <el-dropdown trigger="click">
                      <span class="el-dropdown-link">
                        <i class="el-icon-more"></i>
                      </span>
                      <el-dropdown-menu slot="dropdown">
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
                  </div>
                </div>
              </el-card>
            </div>
          </el-col>
        </el-row>
        <div style="margin-left: 35%">
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
      pagesize: 8,
      searchQue: [],
      allQuest: [
        {
          duration: "00:00:00",
          creationTime: "2021-05-21 17:27",
          releaseTime: "2021-08-11 18:27",
          templateId: 1,
          type: "normal",
          title: "测试问卷",
          released: false,
        },
        {
          duration: "00:00:00",
          creationTime: "2021-08-21 17:27",
          releaseTime: "",
          templateId: 1,
          type: "normal",
          title: "例题",
          released: false,
        },
        {
          duration: "00:00:30",
          creationTime: "2021-07-23 17:27",
          releaseTime: "2021-08-24 18:27",
          templateId: 1,
          type: "normal",
          title: "测试问卷",
          released: true,
        },
        {
          duration: "01:00:00",
          creationTime: "2021-08-21 17:27",
          releaseTime: "2021-08-21 19:27",
          templateId: 1,
          type: "normal",
          title: "问卷",
          released: false,
        },
        {
          duration: "00:02:00",
          creationTime: "2021-08-22 17:27",
          releaseTime: "2021-08-21 18:27",
          templateId: 1,
          type: "normal",
          title: "实验",
          released: false,
        },
      ],
    };
  },
  created() {
    this.convert();
    this.total = this.allQuest.length;
    this.searchQue = this.allQuest.sort(function (a, b) {
      if (a.releaseTime < b.releaseTime) {
        return -1;
      } else if (a.releaseTime == b.releaseTime) {
        return 0;
      } else {
        return 1;
      }
    });
  },
  methods: {
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
    convert: function () {
      this.$axios({
        method: "get",
        url: "http://139.224.50.146:80/apis/all",
        params: {
          removed: false,
        },
        paramsSerializer: function (params) {
          return this.$qs.stringify(params, { arrayFormat: "indices" });
        },
        responseTpe: "blob",
      }).then((res) => {
        if (res.data.templates != undefined) {
          this.allQuest = res.data.templates;
        } else {
          this.allQuest = [];
        }
        console.log(this.allQuest);
      });
    },
  },
};
</script>

<style>
.card {
  background-color: rgba(46, 140, 219, 0.94);
}
.type_show {
  margin-top: 0px;
}
.title {
  margin-top: 0px;
}
.button {
  color: black;
}
</style>