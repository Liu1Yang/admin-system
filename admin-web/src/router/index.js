import { createRouter, createWebHistory } from 'vue-router'
import { getToken, hasPermission } from '../utils/auth'
import AdminLayout from '../layout/AdminLayout.vue'
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import UserList from '../views/user/UserList.vue'
import RoleList from '../views/role/RoleList.vue'
import PlaceholderPage from '../views/PlaceholderPage.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { guest: true }
  },
  {
    path: '/',
    component: AdminLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: '首页' }
      },
      {
        path: 'users',
        name: 'Users',
        component: UserList,
        meta: { title: '用户管理', permission: 'user:delete' }
      },
      {
        path: 'roles',
        name: 'Roles',
        component: RoleList,
        meta: { title: '角色管理', permission: 'role:assign' }
      },
      {
        path: 'categories',
        name: 'Categories',
        component: PlaceholderPage,
        meta: { title: '分类管理', permission: 'product:write' }
      },
      {
        path: 'products',
        name: 'Products',
        component: PlaceholderPage,
        meta: { title: '商品管理', permission: 'product:write' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const token = getToken()

  if (to.meta.requiresAuth && !token) {
    return {
      path: '/login',
      query: { redirect: to.fullPath }
    }
  }

  if (to.meta.guest && token) {
    const redirect = typeof to.query.redirect === 'string' ? to.query.redirect : '/'
    return redirect
  }

  if (to.meta.permission && !hasPermission(to.meta.permission)) {
    return '/'
  }

  return true
})

export default router
