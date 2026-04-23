export interface AppRoute {
  path: string
  name: string
  view: string
}

export const appRoutes: AppRoute[] = [
  { path: '/', name: 'home', view: 'views/home/index.vue' },
  { path: '/template/detail', name: 'template-detail', view: 'views/template/detail.vue' },
  { path: '/result', name: 'result', view: 'views/result/index.vue' },
  { path: '/history', name: 'history', view: 'views/history/index.vue' },
]
