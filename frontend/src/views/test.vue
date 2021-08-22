<template>
  <div class="hello">
    <h1> 二维码</h1>
    <el-button
    plain
    @click="dialogVisible = true">
    打开Dialog
    </el-button>
    <el-dialog
    title="title"
    :visible.sync="dialogVisible"
    width="30%"
    :before-close="handleClose">
        <vue-qr ref="Qrcode" 
                :text="dataObj.text" 
                :logoSrc="dataObj.logo" 
                :callback="test" qid="testQrId"></vue-qr>
        <button 
        class="tag-copy"
        :data-clipboard-text="dataObj.text"
        @click="copyShareLink">复制二维码</button>
        <a 
        :href="exportLink" 
        @click="downloadImg" 
        :download="downloadFilename">下载二维码</a>
    </el-dialog>
  </div>
</template>

<script>
// 二维码组件
import VueQr from 'vue-qr'
// 复制粘贴组件
import Clipboard from 'clipboard'

export default {
  name: 'HelloWorld',
  components: { VueQr },
  data () {
    return {
      dataObj: {
        text: 'https://blog.csdn.net/weixin_43760328/rss/list',
        logo: require('../assets/logo.png')
      },
      exportLink: '',
      downloadFilename: '',
      dialogVisible: false
    }
  },
  methods: {
    test(dataUrl, id) {
        console.log(dataUrl)
        console.log(id)
    },
    // 复制链接
    async copyShareLink() {
      let clipboard = new Clipboard('.tag-copy')
      await clipboard.on('success', () => {
        alert('链接复制成功，请到微信打开！')
        clipboard.destroy() // 释放内存
      })
      clipboard.on('error', () => { 
        alert('该浏览器不支持自动复制！')  // 不支持复制  
        clipboard.destroy()  // 释放内存  
      })
    },
    // 下载二维码图片
    downloadImg () {
      let Qrcode = this.$refs.Qrcode
      this.exportLink = Qrcode.$el.currentSrc
      this.downloadFilename = '二维码'
    },
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1, h2 {
  font-weight: normal;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>