package com.market.service;

import com.market.entity.ForumPost;
import com.market.entity.Product;
import com.market.entity.User;
import com.market.repository.ForumPostRepository;
import com.market.repository.ProductRepository;
import com.market.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 综合搜索服务类
 */
@Service
public class SearchService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ForumPostRepository forumPostRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 综合搜索（商品、帖子、用户）
     */
    public SearchResult searchAll(String keyword, Pageable pageable) {
        SearchResult result = new SearchResult();

        // 搜索商品
        Page<Product> products = productRepository.searchProducts(keyword, pageable);
        result.setProducts(products);

        // 搜索帖子
        Page<ForumPost> posts = forumPostRepository.searchPosts(keyword, pageable);
        result.setPosts(posts);

        // 搜索用户
        Page<User> users = userRepository.searchUsers(keyword, pageable);
        result.setUsers(users);

        // 统计总数
        result.setTotal(products.getTotalElements() + posts.getTotalElements() + users.getTotalElements());

        return result;
    }

    /**
     * 搜索商品
     */
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchProducts(keyword, pageable);
    }

    /**
     * 搜索帖子
     */
    public Page<ForumPost> searchPosts(String keyword, Pageable pageable) {
        return forumPostRepository.searchPosts(keyword, pageable);
    }

    /**
     * 搜索用户
     */
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        return userRepository.searchUsers(keyword, pageable);
    }

    /**
     * 热门搜索关键词
     */
    public List<String> getHotKeywords() {
        // 这里可以记录搜索历史，统计热门搜索词
        // 暂时返回固定列表
        List<String> hotKeywords = new ArrayList<>();
        hotKeywords.add("手机");
        hotKeywords.add("电脑");
        hotKeywords.add("耳机");
        hotKeywords.add("平板");
        hotKeywords.add("手表");
        return hotKeywords;
    }

    /**
     * 搜索建议
     */
    public List<String> getSuggestions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<String> suggestions = new ArrayList<>();

        // 从商品名称中获取建议
        List<Product> products = productRepository.findByNameContaining(keyword);
        suggestions.addAll(products.stream()
            .limit(5)
            .map(Product::getName)
            .collect(Collectors.toList()));

        return suggestions.stream().distinct().limit(10).collect(Collectors.toList());
    }

    /**
     * 搜索结果封装类
     */
    public static class SearchResult {
        private Page<Product> products;
        private Page<ForumPost> posts;
        private Page<User> users;
        private long total;

        public Page<Product> getProducts() {
            return products;
        }

        public void setProducts(Page<Product> products) {
            this.products = products;
        }

        public Page<ForumPost> getPosts() {
            return posts;
        }

        public void setPosts(Page<ForumPost> posts) {
            this.posts = posts;
        }

        public Page<User> getUsers() {
            return users;
        }

        public void setUsers(Page<User> users) {
            this.users = users;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }
    }
}
