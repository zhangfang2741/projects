// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import $ from 'jquery'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap'
import 'element-ui/lib/theme-chalk/index.css'
import ElementUI from 'element-ui'
import App from './App'
import router from './router'
import store from './vuex/store'

Vue.config.productionTip = false
Vue.use(ElementUI, { size: 'small' })
/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,//使用store  
  components: { App},
  template: '<App/>'
})
