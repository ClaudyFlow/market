import request from './request'

export const categoryApi = {
  getTree: () => request.get('/category/tree'),
  getAll: () => request.get('/category'),
  getChildren: (parentId: number) => request.get(`/category/\${parentId}/children`),
  create: (data: any) => request.post('/category', data),
  update: (id: number, data: any) => request.put(\`/category/\${id}\`, data),
  delete: (id: number) => request.delete(\`/category/\${id}\`),
  updateSort: (data: any[]) => request.put('/category/sort', data),
}
