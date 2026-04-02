/**
 * 论坛相关 API
 */

import { get, post, put, del } from './request'
import type { ForumPost, ForumComment, ForumBoard } from '@user/types/forum'
import type { PageData, PageParams } from './request'

const BASE_URL = '/forum'

/**
 * 获取版块列表
 */
export function getBoards(): Promise<ForumBoard[]> {
  return get(`${BASE_URL}/boards`)
}

/**
 * 获取版块详情
 */
export function getBoardDetail(boardId: number | string): Promise<ForumBoard> {
  return get(`${BASE_URL}/board/${boardId}`)
}

/**
 * 获取帖子列表
 */
export function getPostList(params?: PageParams & { boardId?: number; tag?: string; sort?: string }): Promise<PageData<ForumPost>> {
  return get(`${BASE_URL}/posts`, params)
}

/**
 * 获取帖子详情
 */
export function getPostDetail(postId: number | string): Promise<ForumPost> {
  return get(`${BASE_URL}/post/${postId}`)
}

/**
 * 创建帖子
 */
export function createPost(data: {
  boardId: number
  title: string
  content: string
  tags?: string[]
  images?: string[]
  anonymous?: boolean
}): Promise<ForumPost> {
  return post(`${BASE_URL}/post`, data)
}

/**
 * 更新帖子
 */
export function updatePost(postId: number | string, data: { title?: string; content?: string; tags?: string[] }): Promise<ForumPost> {
  return put(`${BASE_URL}/post/${postId}`, data)
}

/**
 * 删除帖子
 */
export function deletePost(postId: number | string): Promise<void> {
  return del(`${BASE_URL}/post/${postId}`)
}

/**
 * 置顶帖子
 */
export function pinPost(postId: number | string, pinned: boolean): Promise<void> {
  return put(`${BASE_URL}/post/${postId}/pin`, { pinned })
}

/**
 * 精华帖子
 */
export function setEssence(postId: number | string, essence: boolean): Promise<void> {
  return put(`${BASE_URL}/post/${postId}/essence`, { essence })
}

/**
 * 锁定帖子
 */
export function lockPost(postId: number | string, locked: boolean): Promise<void> {
  return put(`${BASE_URL}/post/${postId}/lock`, { locked })
}

/**
 * 获取评论列表
 */
export function getCommentList(postId: number | string, params?: PageParams): Promise<PageData<ForumComment>> {
  return get(`${BASE_URL}/post/${postId}/comments`, params)
}

/**
 * 创建评论
 */
export function createComment(postId: number | string, content: string, parentId?: number): Promise<ForumComment> {
  return post(`${BASE_URL}/post/${postId}/comment`, { content, parentId })
}

/**
 * 删除评论
 */
export function deleteComment(commentId: number | string): Promise<void> {
  return del(`${BASE_URL}/comment/${commentId}`)
}

/**
 * 点赞帖子
 */
export function likePost(postId: number | string): Promise<{ likeCount: number }> {
  return post(`${BASE_URL}/post/${postId}/like`)
}

/**
 * 取消点赞
 */
export function unlikePost(postId: number | string): Promise<{ likeCount: number }> {
  return del(`${BASE_URL}/post/${postId}/like`)
}

/**
 * 收藏帖子
 */
export function favoritePost(postId: number | string): Promise<void> {
  return post(`${BASE_URL}/post/${postId}/favorite`)
}

/**
 * 获取我的帖子
 */
export function getMyPosts(params?: PageParams): Promise<PageData<ForumPost>> {
  return get(`${BASE_URL}/my-posts`, params)
}

/**
 * 获取我的评论
 */
export function getMyComments(params?: PageParams): Promise<PageData<ForumComment>> {
  return get(`${BASE_URL}/my-comments`, params)
}

/**
 * 搜索帖子
 */
export function searchPosts(keyword: string, params?: PageParams): Promise<PageData<ForumPost>> {
  return get(`${BASE_URL}/search`, { keyword, ...params })
}
