package com.xxz.loginhouduan.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.BatchSize;
import javax.persistence.*;
import java.util.*;

@Data
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;
    private String content;
    private String timeAgo;
    private int likes = 0;

    @ElementCollection
    @CollectionTable(name = "post_images", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "image_url")
    private List<String> images; // ✅ 存储图片 URL

    private String video; // ✅ 存储视频 URL

    @Column(name = "created_at", updatable = false, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt; // ✅ 新增字段：帖子创建时间

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_liked_users", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "liked_user")
    private Set<String> likedUsers = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @BatchSize(size = 10)
    @JsonIgnoreProperties("post")
    private Set<Comment> comments = new HashSet<>();

    @Transient
    private boolean isLiked;

    public boolean getIsLiked() { // ✅ 添加 getter
        return isLiked;
    }

    public void setIsLiked(boolean isLiked) { // ✅ 添加 setter
        this.isLiked = isLiked;
    }

    // ✅ 在新建帖子时自动设置 `createdAt`
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
