import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'

const Questionnaire = () => import("../views/Questionnaire.vue")
const History = () => import("../views/History.vue")
const Recycle = () => import("../views/Recycle.vue")
const Normal = () => import("../views/Normal.vue")
const Vote = () => import("../views/Vote.vue")
const Apply = () => import("../views/Apply.vue")
const Exam = () => import("../views/Exam.vue")
const Login = () => import("../components/Login.vue")
const Register = () => import("../components/Register.vue")

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
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
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/about',
    name: 'About',
    component: About
  },
  {
    path: '/normal/new',
    name: 'Normal',
    component: Normal
  },
  {
    path: '/vote/new',
    name: 'Vote',
    component: Vote
  },
  {
    path: '/apply/new',
    name: 'Apply',
    component: Apply
  },
  {
    path: '/exam/new',
    name: 'Exam',
    component: Exam
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
