import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import axios from 'axios'
import router from './router'
import store from './store'
import vuedraggable from 'vuedraggable'
import vueToPdf from 'vue-to-pdf';
import VueRouter from 'vue-router';
import iView from 'iview';
import 'iview/dist/styles/iview.css';

Vue.prototype.$axios = axios
axios.defaults.headers.post['Content-Type'] = 'application/json'
Vue.config.productionTip = false
Vue.use(ElementUI)
Vue.use(vueToPdf);
Vue.use(VueRouter);
Vue.use(iView);

new Vue({
  router,
  store,
  axios,
  components: {
    vuedraggable
  },
  render: h => h(App)
}).$mount('#app')
