package com.example.blog_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    @Column(nullable = false)
    private String title;
    @Setter
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    // 领域方法：更新文章
    public void updateContent(String newTitle, String newContent) {
        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("标题不能为空");
        }
        this.title = newTitle;
        this.content = newContent;
    }
}
