<template>
  <div class="home">
    <el-container>
      <el-header>Header</el-header>
      <el-container>
        <el-aside>
          <el-menu
            default-active="this.$route.path"
            class="el-menu-vertical-demo"
            @open="handleOpen"
            @close="handleClose"
            router>
            <el-menu-item index="/questionnaire">
              <i class="el-icon-circle-plus"></i>
              <span slot="title">新建问卷</span>
            </el-menu-item>
            <el-menu-item index="/history">
              <i class="el-icon-s-order"></i>
              <span slot="title">历史问卷</span>
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

</script>
