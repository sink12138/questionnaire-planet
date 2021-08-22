import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import axios from 'axios'
import router from './router'
import store from './store'
import vuedraggable from 'vuedraggable'
import qs from 'qs'


Vue.prototype.$axios = axios
axios.defaults.headers.post['Content-Type'] = 'application/json'
Vue.config.productionTip = false
Vue.use(ElementUI)

new Vue({
  router,
  store,
  axios,
  qs,
  components: {
    vuedraggable
  },
  render: h => h(App)
}).$mount('#app')
