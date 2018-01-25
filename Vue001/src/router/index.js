import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'
import Hi from '@/components/Hi'
import Hi1 from '@/components/Hi.1'
import Hi2 from '@/components/Hi.2'
import Elselect from '@/components/Elselect'
import Error from '@/components/Error'
import Count from '@/components/Count'
import Use from '@/components/Use'
Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'HelloWorld',
      component: HelloWorld
    },
    {
      path:'/hi',
     // name:'HelloWorld/hi',
      component:Hi,
      children:[
        {path:'/',component:Hi,name:"HelloWorld/hi"},
        {path:'hi1',component:Hi1,name:'Hi1'},
        {path:'hi2',component:Hi2,name:'HelloWorld/hi/Hi2'}//可以做面包屑
      ]
    },
    {
      path:'/elselect',
      name:'Elselect',
      component:Elselect
    },
    {
      path:'/count',
      name:'Count',
      component:Count
    },{
      path:'/use',
      name:Use,
      component:Use
    },
    {
      path:'*',
      component:Error
   }
  ]
})
