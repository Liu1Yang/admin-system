/** 避免 request 与 router 循环依赖，在 main.js 注入 router 实例 */
let router = null

export function setRouter(instance) {
  router = instance
}

export function getRouter() {
  return router
}
