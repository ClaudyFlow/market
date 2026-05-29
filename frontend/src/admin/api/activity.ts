import request from './request'

export const activityApi = {
  getMyActivities: () => request.get('/activity'),
  setSetting: (activityId: number, data: any) => request.put(`/activity/${activityId}/setting`, data),
  optOut: (activityId: number, data: any) => request.post(`/activity/${activityId}/optout`, data),
}

export const platformActivityApi = {
  getAll: () => request.get('/activity'),
  getActive: () => request.get('/activity/active'),
  getByStatus: (status: string) => request.get(`/activity/status/${status}`),
  getById: (id: number) => request.get(`/activity/${id}`),
  create: (data: any) => request.post('/activity', data),
  update: (id: number, data: any) => request.put(`/activity/${id}`, data),
  publish: (id: number) => request.put(`/activity/${id}/publish`),
  pause: (id: number) => request.put(`/activity/${id}/pause`),
  end: (id: number) => request.put(`/activity/${id}/end`),
  delete: (id: number) => request.delete(`/activity/${id}`),
}

export const publicActivityApi = {
  getActive: () => request.get('/activity/active'),
  getById: (id: number) => request.get(`/activity/${id}`),
}
