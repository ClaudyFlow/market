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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 论坛控制器
 */
@RestController
@RequestMapping("/api/forum")
@CrossOrigin(origins = "*")
public class ForumController {

    @Autowired
    private ForumService forumService;

    /**
     * 获取帖子列表
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
     */
    @GetMapping("/posts/{id}")
    public Result<Map<String, Object>> getPostDetail(@PathVariable Long id) {
        ForumPost post = forumService.getPostDetail(id);
        Map<String, Object> result = convertPostToMap(post);
        return Result.success(result);
    }

    /**
     * 创建帖子
     */
    @PostMapping("/posts")
    public Result<Map<String, Object>> createPost(
            @AuthenticationPrincipal User user,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String category) {

        ForumPost post = forumService.createPost(user, title, content, tags, category);
        Map<String, Object> result = convertPostToMap(post);
        return Result.success(result);
    }

    /**
     * 更新帖子
     */
    @PutMapping("/posts/{id}")
    public Result<Map<String, Object>> updatePost(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String tags) {

        ForumPost post = forumService.updatePost(id, user, title, content, tags);
        Map<String, Object> result = convertPostToMap(post);
        return Result.success(result);
    }

    /**
     * 删除帖子
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
     */
    @PostMapping("/posts/{postId}/comments")
    public Result<Map<String, Object>> createComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user,
            @RequestParam String content,
            @RequestParam(required = false) Long parentId) {

        ForumComment comment = forumService.createComment(user, postId, content, parentId);
        Map<String, Object> result = convertCommentToMap(comment);
        return Result.success(result);
    }

    /**
     * 删除评论
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
     */
    @PostMapping("/comments/{id}/like")
    public Result<Map<String, Object>> likeComment(@PathVariable Long id) {
        forumService.likeComment(id);
        Map<String, Object> result = new HashMap<>();
        result.put("liked", true);
        return Result.success(result);
    }

    /**
     * 获取论坛统计
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
