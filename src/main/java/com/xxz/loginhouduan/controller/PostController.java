package com.xxz.loginhouduan.controller;

import com.xxz.loginhouduan.entity.Post;
import com.xxz.loginhouduan.entity.Comment;
import com.xxz.loginhouduan.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true") // 允许前端访问
public class PostController {

    @Autowired
    private PostService postService;

    // ✅ 删除帖子
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId, @RequestParam String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("User name is required");
        }

        boolean isDeleted = postService.deletePost(postId, userName);
        if (isDeleted) {
            return ResponseEntity.ok("Post deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only delete your own posts.");
        }
    }
    // ✅ 1. 获取所有帖子
    @GetMapping
    public List<Post> getAllPosts(@RequestParam String userName) {
        try {
            List<Post> posts = postService.getAllPosts(userName);
            System.out.println("📌 成功获取帖子数量：" + posts.size());
            return posts;
        } catch (Exception e) {
            e.printStackTrace(); // ✅ 打印详细错误日志
            return new ArrayList<>(); // 避免前端返回 500 错误
        }
    }

    // ✅ 2. 发布帖子
    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.createPost(post);
    }

    // ✅ 3. 点赞帖子
    @PostMapping("/{postId}/like")
    public Map<String, Object> likePost(@PathVariable Long postId, @RequestBody Map<String, String> request) {
        String userName = request.get("userName");
        return postService.likePost(postId, userName);
    }

    // ✅ 4. 添加评论
    @PostMapping("/{postId}/comments")
    public Comment addComment(@PathVariable Long postId, @RequestBody Comment comment) {
        return postService.addComment(postId, comment);
    }
}
