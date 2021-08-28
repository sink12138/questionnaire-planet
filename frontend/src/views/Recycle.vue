<template>
  <div class="recycle">
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
        <el-table :data="searchQue" border :header-cell-style="{'text-align':'center',background:'#eee',color:'#606266','height':'58px'}">
          <el-table-column fixed prop="title" label="标题" width="150">
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
          <el-table-column label="操作" width="280">
            <template slot-scope="scope">
              <el-button
                @click="deleteQuest(scope.row)"
                icon="el-icon-delete"
                >彻底删除</el-button
              >
              <el-button
                @click="recover(scope.row)"
                icon="el-icon-refresh"
                >恢复问卷</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      search: "",
      total: 0,
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
    handleSizeChange: function (size) {
      this.pagesize = size;
    },
    handleCurrentChange: function (currentPage) {
      this.current_page = currentPage;
    },
    deleteQuest(row) {
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
              templateId: row.templateId,
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
    recover(row) {
      this.$axios({
        method: "post",
        url: "http://139.224.50.146:80/apis/recover",
        data: JSON.stringify({
          templateId: row.templateId,
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
  },
};
</script>

<style scoped>
.recycle {
  margin-left: 60px;
}
.top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  margin: 15px;
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
.el-table__column-filter-trigger {
  height: 20px;
}
</style>