<template>
  <div class="reviewer">
    <div>
      <h1>您已删除的问卷如下</h1>
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
            v-for="item in allQuest.slice(
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
                      @click="deleteQuest(item)"
                      icon="el-icon-delete-solid"
                    ></el-button>
                    <el-button
                      type="text"
                      class="button"
                      @click="recover(item)"
                      icon="el-icon-refresh"
                    ></el-button>
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
import svg from "../components/svg-questionnaire.vue";
export default {
  components: {
    "question-pic": svg,
  },
  data() {
    return {
      search: "",
      current_page: 1,
      total: 0,
      pagesize: 8,
      allQuest: [],
      searchQue: [],
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
          removed: true,
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
    deleteQuest(item) {
      this.$confirm("此操作将永久删除该问卷, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$axios({
            method: "post",
            url: "http://139.224.50.146:80/apis/delete",
            data: JSON.stringify({
              templateId: item.templateId,
            }),
          }).then(
            (response) => {
              console.log(response);
              if (response.data.success == true) {
                this.$message({
                  message: "问卷已彻底删除。",
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
    recover(item) {
      this.$axios({
        method: "post",
        url: "http://139.224.50.146:80/apis/recover",
        data: JSON.stringify({
          templateId: item.templateId,
        }),
      }).then(
        (response) => {
          console.log(response);
          if (response.data.success == true) {
            this.$message({
              message: "问卷已恢复。",
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