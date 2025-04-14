package com.xxz.loginhouduan.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Data
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @JsonIgnoreProperties("comments")  // ✅ 避免 JSON 无限递归
    private Post post;

    private String userName;
    private String content;
    private String timeAgo;

    @Temporal(TemporalType.TIMESTAMP)  // ✅ 让 Hibernate 以时间戳格式存储
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date createdAt;

    // ✅ 修正 equals() 和 hashCode()，不包含 post
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
