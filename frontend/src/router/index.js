import Vue from 'vue'
import VueRouter from 'vue-router'
import ElementUI from 'element-ui'

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
const EditExam = () => import("../views/EditExam.vue")
const Epidemic = () => import("../views/Epidemic.vue")
const EditEpidemic = () => import("../views/EditEpidemic.vue")
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
    path: '/questionnaire',
    name: 'Questionnaire',
    component: Questionnaire,
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
    path: '/exam/edit',
    name: 'EditExam',
    component: EditExam
  },
  {
    path: '/epidemic/new',
    name: 'Epidemic',
    component: Epidemic
  },
  {
    path: '/epidemic/edit',
    name: 'EditEpidemic',
    component: EditEpidemic
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
router.beforeEach((to, from, next) => {
  var regx = /^\/fill.*?$/;
  if(sessionStorage.getItem("isLogin") == "true" || to.path == "/" || to.path.match(regx)) {
    next();
  }
  else {
    next("/");
    ElementUI.Notification({
      title: '????????????',
      message: '???????????????????????????',
      type: 'warning'
    });
  }
})
export default router
