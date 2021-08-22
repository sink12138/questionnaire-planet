<template>
  <div class="reviewer">
    <div class="questionnaire">
      <div style="margin-left: 1%; margin-right: 1%">
        <el-row>
          <el-col
            :span="6"
            v-for="item in allQuest.slice(
              (current_page - 1) * pagesize,
              current_page * pagesize
            )"
            :key="item.templateId"
            style="margin: 0;padding: 0;border: 0;"
          >
            <div class="card">
              <div class="title">
                <span>
                  <strong>{{ item.title }}</strong>
                </span>
              </div>
              <div class="type_show">
                <question-pic></question-pic>
              </div>
              <div class="time">
                <time>
                  <strong>创建时间:</strong>{{ item.creationTime }}
                </time>
              </div>
              <div class="time" v-if="item.releaseTime != ''">
                <time>
                  <strong>发布时间:</strong>{{ item.releaseTime }}
                </time>
              </div>
              <div class="time" v-else>
                <time>
                  <strong>发布时间:</strong>未曾发布
                </time>
              </div>
              <div class="time">
                <time>
                  <strong>收集时长:</strong>{{ item.duration }}
                </time>
              </div>
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
      quest: 0,
      current_page: 1,
      total: 0,
      pagesize: 8,
      allQuest: [
        {
          duration: "00:00:00",
          creationTime: "2021-08-21 17:27",
          releaseTime: "2021-08-21 18:27",
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
          title: "测试问卷",
          released: false,
        },
        {
          duration: "00:00:00",
          creationTime: "2021-08-21 17:27",
          releaseTime: "2021-08-21 18:27",
          templateId: 1,
          type: "normal",
          title: "测试问卷",
          released: true,
        },
        {
          duration: "00:00:00",
          creationTime: "2021-08-21 17:27",
          releaseTime: "2021-08-21 18:27",
          templateId: 1,
          type: "normal",
          title: "测试问卷",
          released: false,
        },
        {
          duration: "00:00:00",
          creationTime: "2021-08-21 17:27",
          releaseTime: "2021-08-21 18:27",
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
    this.total = this.allQuest.length;
  },
  methods: {
    handleSizeChange: function (size) {
      this.pagesize = size;
    },
    handleCurrentChange: function (currentPage) {
      this.current_page = currentPage;
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

<style scoped>
.card {
  display: flex;
  background-color: rgba(46, 140, 219, 0.94);
  flex-direction: column;
  align-items: center;
  height: 300px;
}
.time {
  align-self: flex-start;
  margin: 0;
}
.type_show {
  margin-top: 0px;
  height: 200px;
  width: 100px;
}
.title {
  margin-top: 0px;
}
.button {
  color: black;
}
</style>