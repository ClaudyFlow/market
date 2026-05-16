package com.market.controller;

import com.market.common.Result;
import com.market.entity.ForumComment;
import com.market.entity.ForumPost;
import com.market.entity.User;
import com.market.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 论坛控制器
 * 提供论坛帖子的 CRUD、点赞、搜索、标签筛选、评论管理等功能。
 * 权限要求：查询接口公开，发帖/删帖等需要登录
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/forum
 */
@RestController
@RequestMapping("/api/forum")
@CrossOrigin(origins = "*")
public class ForumController {

    @Autowired
    private ForumService forumService;

    /**
     * 获取版块列表
     * API路径：GET /api/forum/boards
     * 权限：公开
     *
     * @return 版块列表
     */
    @GetMapping("/boards")
    public Result<List<Map<String, Object>>> getBoards() {
        List<Map<String, Object>> boards = new java.util.ArrayList<>();

        Map<String, Object> general = new HashMap<>();
        general.put("id", 1);
        general.put("name", "综合讨论");
        general.put("description", "综合讨论板块");
        boards.add(general);

        Map<String, Object> trade = new HashMap<>();
        trade.put("id", 2);
        trade.put("name", "交易经验");
        trade.put("description", "交易经验分享");
        boards.add(trade);

        Map<String, Object> question = new HashMap<>();
        question.put("id", 3);
        question.put("name", "问答求助");
        question.put("description", "问题解答板块");
        boards.add(question);

        return Result.success(boards);
    }

    /**
     * 获取帖子列表
     * API路径：GET /api/forum/posts
     * 权限：公开
     *
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @return 分页的帖子列表
     */
    @GetMapping("/posts")
    public Result<Map<String, Object>> getPosts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ForumPost> postPage = forumService.getPostList(pageable);

        List<Map<String, Object>> postList = postPage.getContent().stream()
            .map(this::convertPostToMap)
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", postList);
        response.put("total", postPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);

        return Result.success(response);
    }

    /**
     * 获取热门帖子
     * API路径：GET /api/forum/posts/hot
     * 权限：公开
     *
     * @param days 时间范围（天），默认7
     * @param limit 数量限制，默认10
     * @return 热门帖子列表
     */
    @GetMapping("/posts/hot")
    public Result<List<Map<String, Object>>> getHotPosts(
            @RequestParam(defaultValue = "7") Integer days,
            @RequestParam(defaultValue = "10") Integer limit) {
        LocalDateTime sinceTime = LocalDateTime.now().minusDays(days);
        Pageable pageable = PageRequest.of(0, limit);
        Page<ForumPost> postPage = forumService.getHotPosts(sinceTime, pageable);

        List<Map<String, Object>> postList = postPage.getContent().stream()
            .map(this::convertPostToMap)
            .collect(Collectors.toList());

        return Result.success(postList);
    }

    /**
     * 获取精华帖子
     * API路径：GET /api/forum/posts/featured
     * 权限：公开
     *
     * @param limit 数量限制，默认10
     * @return 精华帖子列表
     */
    @GetMapping("/posts/featured")
    public Result<List<Map<String, Object>>> getFeaturedPosts(
            @RequestParam(defaultValue = "10") Integer limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<ForumPost> postPage = forumService.getFeaturedPosts(pageable);

        List<Map<String, Object>> postList = postPage.getContent().stream()
            .map(this::convertPostToMap)
            .collect(Collectors.toList());

        return Result.success(postList);
    }

    /**
     * 获取我的帖子
     * API路径：GET /api/forum/posts/my
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @param limit 数量限制，默认10
     * @return 我的帖子列表
     */
    @GetMapping("/posts/my")
    public Result<List<Map<String, Object>>> getMyPosts(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "10") Integer limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<ForumPost> postPage = forumService.getUserPosts(user, pageable);

        List<Map<String, Object>> postList = postPage.getContent().stream()
            .map(this::convertPostToMap)
            .collect(Collectors.toList());

        return Result.success(postList);
    }

    /**
     * 获取帖子详情
     * API路径：GET /api/forum/posts/{id}
     * 权限：公开
     *
     * @param id 帖子ID
     * @return 帖子详情
     */
    @GetMapping("/posts/{id}")
    public Result<Map<String, Object>> getPostDetail(@PathVariable Long id) {
        ForumPost post = forumService.getPostDetail(id);
        Map<String, Object> result = convertPostToMap(post);
        return Result.success(result);
    }

    /**
     * 创建帖子
     * API路径：POST /api/forum/posts
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @param title 帖子标题
     * @param content 帖子内容
     * @param tags 标签（可选）
     * @param category 分类（可选）
     * @return 创建的帖子
     */
    @PostMapping("/posts")
    public Result<Map<String, Object>> createPost(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> postData) {

        ForumPost post = forumService.createPost(user, postData.get("title"), postData.get("content"), postData.get("tags"), postData.get("category"));
        Map<String, Object> result = convertPostToMap(post);
        return Result.success(result);
    }

    /**
     * 更新帖子
     * API路径：PUT /api/forum/posts/{id}
     * 权限：需要登录
     *
     * @param id 帖子ID
     * @param user 当前登录用户
     * @param title 新标题（可选）
     * @param content 新内容（可选）
     * @param tags 新标签（可选）
     * @return 更新后的帖子
     */
    @PutMapping("/posts/{id}")
    public Result<Map<String, Object>> updatePost(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> postData) {

        ForumPost post = forumService.updatePost(id, user, postData.get("title"), postData.get("content"), postData.get("tags"));
        Map<String, Object> result = convertPostToMap(post);
        return Result.success(result);
    }

    /**
     * 删除帖子
     * API路径：DELETE /api/forum/posts/{id}
     * 权限：需要登录
     *
     * @param id 帖子ID
     * @param user 当前登录用户
     * @return 操作结果
     */
    @DeleteMapping("/posts/{id}")
    public Result<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        forumService.deletePost(id, user);
        return Result.success();
    }

    /**
     * 点赞帖子
     * API路径：POST /api/forum/posts/{id}/like
     * 权限：公开
     *
     * @param id 帖子ID
     * @return 点赞结果
     */
    @PostMapping("/posts/{id}/like")
    public Result<Map<String, Object>> likePost(@PathVariable Long id) {
        forumService.likePost(id);
        Map<String, Object> result = new HashMap<>();
        result.put("liked", true);
        return Result.success(result);
    }

    /**
     * 搜索帖子
     * API路径：GET /api/forum/posts/search
     * 权限：公开
     *
     * @param keyword 搜索关键词
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @return 搜索到的帖子列表
     */
    @GetMapping("/posts/search")
    public Result<List<Map<String, Object>>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ForumPost> postPage = forumService.searchPosts(keyword, pageable);

        List<Map<String, Object>> postList = postPage.getContent().stream()
            .map(this::convertPostToMap)
            .collect(Collectors.toList());

        return Result.success(postList);
    }

    /**
     * 按标签获取帖子
     * API路径：GET /api/forum/posts/tag
     * 权限：公开
     *
     * @param tag 标签名
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @return 指定标签的帖子列表
     */
    @GetMapping("/posts/tag")
    public Result<List<Map<String, Object>>> getPostsByTag(
            @RequestParam String tag,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ForumPost> postPage = forumService.getPostsByTag(tag, pageable);

        List<Map<String, Object>> postList = postPage.getContent().stream()
            .map(this::convertPostToMap)
            .collect(Collectors.toList());

        return Result.success(postList);
    }

    // ==================== 评论相关接口 ====================

    /**
     * 获取帖子评论列表
     * API路径：GET /api/forum/posts/{postId}/comments
     * 权限：公开
     *
     * @param postId 帖子ID
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @return 评论列表
     */
    @GetMapping("/posts/{postId}/comments")
    public Result<List<Map<String, Object>>> getPostComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<ForumComment> commentPage = forumService.getPostComments(postId, pageable);

        List<Map<String, Object>> commentList = commentPage.getContent().stream()
            .map(this::convertCommentToMap)
            .collect(Collectors.toList());

        return Result.success(commentList);
    }

    /**
     * 创建评论
     * API路径：POST /api/forum/posts/{postId}/comments
     * 权限：需要登录
     *
     * @param postId 帖子ID
     * @param user 当前登录用户
     * @param content 评论内容
     * @param parentId 父评论ID（可选，用于回复评论）
     * @return 创建的评论
     */
    @PostMapping("/posts/{postId}/comments")
    public Result<Map<String, Object>> createComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> commentData) {

        ForumComment comment = forumService.createComment(user, postId, (String) commentData.get("content"), commentData.get("parentId") != null ? ((Number) commentData.get("parentId")).longValue() : null);
        Map<String, Object> result = convertCommentToMap(comment);
        return Result.success(result);
    }

    /**
     * 删除评论
     * API路径：DELETE /api/forum/comments/{id}
     * 权限：需要登录
     *
     * @param id 评论ID
     * @param user 当前登录用户
     * @return 操作结果
     */
    @DeleteMapping("/comments/{id}")
    public Result<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        forumService.deleteComment(id, user);
        return Result.success();
    }

    /**
     * 点赞评论
     * API路径：POST /api/forum/comments/{id}/like
     * 权限：公开
     *
     * @param id 评论ID
     * @return 点赞结果
     */
    @PostMapping("/comments/{id}/like")
    public Result<Map<String, Object>> likeComment(@PathVariable Long id) {
        forumService.likeComment(id);
        Map<String, Object> result = new HashMap<>();
        result.put("liked", true);
        return Result.success(result);
    }

    /**
     * 获取论坛统计信息
     * API路径：GET /api/forum/stats
     * 权限：公开
     *
     * @return 论坛统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = forumService.getPostStats();
        return Result.success(stats);
    }

    // ==================== 工具方法 ====================

    private Map<String, Object> convertPostToMap(ForumPost post) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", post.getId());
        map.put("userId", post.getUserId());
        map.put("userName", post.getUserName());
        map.put("userAvatar", post.getUserAvatar());
        map.put("title", post.getTitle());
        map.put("content", post.getContent());
        map.put("tags", post.getTags());
        map.put("category", post.getCategory());
        map.put("likeCount", post.getLikeCount());
        map.put("commentCount", post.getCommentCount());
        map.put("viewCount", post.getViewCount());
        map.put("isPinned", post.getIsPinned());
        map.put("isFeatured", post.getIsFeatured());
        map.put("createdAt", post.getCreatedAt());
        return map;
    }

    private Map<String, Object> convertCommentToMap(ForumComment comment) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", comment.getId());
        map.put("postId", comment.getPostId());
        map.put("userId", comment.getUserId());
        map.put("userName", comment.getUserName());
        map.put("userAvatar", comment.getUserAvatar());
        map.put("content", comment.getContent());
        map.put("parentId", comment.getParentId());
        map.put("likeCount", comment.getLikeCount());
        map.put("createdAt", comment.getCreatedAt());
        return map;
    }
}
