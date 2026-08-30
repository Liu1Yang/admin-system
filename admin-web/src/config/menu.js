/**
 * 侧边栏菜单配置
 * permission 对应后端 RBAC 权限编码；不填则登录即可见
 */
export const menuItems = [
  { path: '/', title: '首页', icon: 'HomeFilled' },
  { path: '/users', title: '用户管理', icon: 'User', permission: 'user:delete' },
  { path: '/roles', title: '角色管理', icon: 'Avatar', permission: 'role:assign' },
  { path: '/categories', title: '分类管理', icon: 'Menu', permission: 'product:write' },
  { path: '/products', title: '商品管理', icon: 'Goods', permission: 'product:write' },
  { path: '/operation-logs', title: '操作日志', icon: 'Document', permission: 'log:read' }
]
