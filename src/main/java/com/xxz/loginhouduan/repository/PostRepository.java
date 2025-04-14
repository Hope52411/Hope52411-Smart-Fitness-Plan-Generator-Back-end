package com.xxz.loginhouduan.repository;

import com.xxz.loginhouduan.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // ✅ 让查询结果按 `createdAt` 降序排列
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likedUsers LEFT JOIN FETCH p.comments ORDER BY p.createdAt DESC")
    List<Post> findAllPosts();

    // ✅ 让用户可以查询自己的帖子
    @Query("SELECT p FROM Post p WHERE p.userName = :userName ORDER BY p.createdAt DESC")
    List<Post> findByUserName(@Param("userName") String userName);
}
