import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import router from './router'
import store from './store'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import vuedraggable from 'vuedraggable'

Vue.config.productionTip = false
Vue.use(ElementUI)

Vue.use(ElementUI);

new Vue({
  router,
  store,
  components: {
    vuedraggable
  },
  render: h => h(App)
}).$mount('#app')
