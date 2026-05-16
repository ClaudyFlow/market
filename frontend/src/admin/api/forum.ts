import request from './request'

export const forumApi = {
  // 获取论坛审核统计
  getAuditStats: () => request({
    url: '/operations/forum-audit/stats',
    method: 'get',
  }),

  // 获取待审核帖子
  getPendingPosts: (page = 0, size = 20) => request({
    url: '/operations/forum-audit/pending',
    method: 'get',
    params: { page, size },
  }),

  // 审核帖子（通过/拒绝）
  auditPost: (postId: number, status: string, reason?: string) => request({
    url: \`/operations/forum-audit/\${postId}\`,
    method: 'put',
    data: { status, reason },
  }),
}
