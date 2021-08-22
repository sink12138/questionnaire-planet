<template>
  <div class="reviewer">
    <div>
      <h1>您已删除的问卷如下</h1>
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
    convert: function () {
      this.$axios({
        method: "get",
        url: "http://139.224.50.146:80/apis/all",
        params: {
          removed: true,
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