import Vue from 'vue'
import VueRouter from 'vue-router'

const Home = () => import("../views/Home.vue")
const Main = () => import("../views/Main.vue")
const Questionnaire = () => import("../views/Questionnaire.vue")
const History = () => import("../views/History.vue")
const Recycle = () => import("../views/Recycle.vue")
const Adjust = () => import("../views/Adjust.vue")
const Normal = () => import("../views/Normal.vue")
const EditNormal = () => import("../views/EditNormal.vue")
const Vote = () => import("../views/Vote.vue")
const EditVote = () => import("../views/EditVote.vue")
const Apply = () => import("../views/Apply.vue")
const EditApply = () => import("../views/EditApply.vue")
const Exam = () => import("../views/Exam.vue")
const Preview = () => import("../views/Preview.vue")
const Fill = () => import("../views/Fill.vue")
const Statistics = () => import("../views/Statistics.vue")
const Personal = () => import("../views/Personal.vue")

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
  },
  {
    path: '/main',
    name: 'Main',
    component: Main,
    children: [
      {
        path: '/questionnaire',
        name: 'Questionnaire',
        component: Questionnaire,
      },
      {
        path: '/history',
        name: 'History',
        component: History
      },
      {
        path: '/recycle',
        name: 'Recycle',
        component: Recycle
      }
    ]
  },
  {
    path: '/adjust',
    name: 'Adjust',
    component: Adjust
  },
  {
    path: '/normal/new',
    name: 'Normal',
    component: Normal
  },
  {
    path: '/normal/edit',
    name: 'EditNormal',
    component: EditNormal
  },
  {
    path: '/vote/new',
    name: 'Vote',
    component: Vote
  },
  {
    path: '/vote/edit',
    name: 'EditVote',
    component: EditVote
  },
  {
    path: '/apply/new',
    name: 'Apply',
    component: Apply
  },
  {
    path: '/apply/edit',
    name: 'EditApply',
    component: EditApply
  },
  {
    path: '/exam/new',
    name: 'Exam',
    component: Exam
  },
  {
    path: '/preview',
    name: 'Preview',
    component: Preview
  },
  {
    path: '/fill',
    name: 'Fill',
    component: Fill
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: Statistics
  },
  {
    path: '/personal',
    name: 'Personal',
    component: Personal
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})
/*路由拦截：请登录后操作。避免繁琐开发，先注释着。
router.beforeEach((to, from, next) => {
  if(sessionStorage.getItem("isLogin") === true || to.path === "/" || to.path === "/register") {
    next();
  }
  else {
    next("/");
    alert("请登录后操作");
  }
})
*/
export default router
