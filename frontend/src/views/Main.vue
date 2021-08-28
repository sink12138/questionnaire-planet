<template>
  <div class="home">
    <el-container>
      <el-header>
        <Header></Header>
      </el-header>
      <el-container>
        <el-aside v-bind:class="aside" style="width: 121px">
          <el-menu
            default-active="this.$route.path"
            class="el-menu-vertical-demo"
            style="margin: 0"
            :collapse="isCollapse"
            router>
            <Button v-if="this.isCollapse" icon="ios-arrow-dropright-circle" @click="changeCollapse()"></Button>
            <Button v-if="this.isCollapse == false" icon="ios-arrow-dropleft-circle" @click="changeCollapse()">收起菜单</Button>
            <router-link to="/questionnaire">
              <Button icon="md-add-circle" v-if="this.isCollapse"></Button>
              <Button icon="md-add-circle" v-if="this.isCollapse == false">创建问卷</Button>
            </router-link>
            <el-menu-item index="/history">
              <i class="el-icon-s-order"></i>
              <span slot="title">我的问卷</span>
            </el-menu-item>
            <el-menu-item index="/recycle">
              <i class="el-icon-delete-solid"></i>
              <span slot="title">回收站</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import Header from "../components/Header.vue"
export default {
  components: {
    'Header': Header,
  },
  data() {
    return {
      isCollapse: false,
    }
  },
  computed: {
    aside:function() {
      return {
        aside_collapse:this.isCollapse
      }
    }
  },
  methods: {
    changeCollapse() {
      this.isCollapse = !this.isCollapse
    }
  }
}
</script>

<style scoped>
.el-header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding: 0;
}
.el-main {
  padding: 0;
}
.el-menu {
  margin-top: 20px;
}
.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 120px;
  min-height: 400px;
}
.ivu-btn {
  height: 56px;
  width: 100%;
  font-size: 15px;
  margin: 0;
  border: #ffffff;
}
.aside_collapse {
  width: 65px;
}
</style>