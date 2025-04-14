package com.xxz.loginhouduan.service;

import com.xxz.loginhouduan.entity.Post;
import com.xxz.loginhouduan.entity.Comment;
import com.xxz.loginhouduan.repository.PostRepository;
import com.xxz.loginhouduan.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    // ✅ 获取所有帖子（自动计算 timeAgo）
    public List<Post> getAllPosts(String userName) {
        List<Post> posts = postRepository.findAllPosts();
        for (Post post : posts) {
            post.setIsLiked(post.getLikedUsers().contains(userName));
        }

        posts.forEach(post -> {
            if (post.getCreatedAt() != null) {
                post.setTimeAgo(formatTimeAgo(post.getCreatedAt()));
            }
            post.getComments().forEach(comment -> {
                if (comment.getCreatedAt() != null) {
                    comment.setTimeAgo(formatTimeAgo(comment.getCreatedAt()));
                }
            });
        });

        return posts;
    }

    // ✅ 发布新帖子
    public Post createPost(Post post) {
        post.setLikes(0);
        post.setCreatedAt(new Date()); // ✅ 记录创建时间
        post.setTimeAgo("Just now");  // ✅ 默认显示 "Just now"
        return postRepository.save(post);
    }

    // ✅ 删除帖子（仅作者可删除）
    public boolean deletePost(Long postId, String userName) {
        Optional<Post> postOptional = postRepository.findById(postId);

        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            if (post.getUserName().equals(userName)) { // ✅ 确保当前用户是作者
                postRepository.deleteById(postId);
                return true;
            } else {
                throw new RuntimeException("Unauthorized to delete this post");
            }
        } else {
            throw new RuntimeException("Post not found");
        }
    }

    // ✅ 点赞帖子
    public Map<String, Object> likePost(Long postId, String userName) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (!postOpt.isPresent()) {
            throw new RuntimeException("Post not found");
        }

        Post post = postOpt.get();
        if (post.getLikedUsers() == null) {
            post.setLikedUsers(new HashSet<>());
        }

        boolean isLiked = post.getLikedUsers().contains(userName);
        if (isLiked) {
            post.getLikedUsers().remove(userName);
            post.setLikes(post.getLikes() - 1);
        } else {
            post.getLikedUsers().add(userName);
            post.setLikes(post.getLikes() + 1);
        }

        postRepository.save(post);

        Map<String, Object> response = new HashMap<>();
        response.put("isLiked", !isLiked);
        response.put("likes", post.getLikes());
        return response;
    }

    // ✅ 添加评论
    public Comment addComment(Long postId, Comment comment) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (!postOpt.isPresent()) {
            throw new RuntimeException("Post not found");
        }

        comment.setPost(postOpt.get());
        comment.setCreatedAt(new Date());  // ✅ 这里设置 `createdAt`
        comment.setTimeAgo("Just now");  // ✅ 刚评论时显示 "Just now"
        return commentRepository.save(comment);
    }



    // ✅ 计算时间差（返回 timeAgo 格式）
    private String formatTimeAgo(Date createdAt) {
        Instant instant = createdAt.toInstant();
        LocalDateTime postTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        Duration duration = Duration.between(postTime, LocalDateTime.now());

        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 1) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " minutes ago";
        } else if (hours < 24) {
            return hours + " hours ago";
        } else {
            return days + " days ago";
        }
    }
}
