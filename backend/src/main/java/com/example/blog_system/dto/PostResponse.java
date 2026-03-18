package com.example.blog_system.dto;

import com.example.blog_system.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String author; // 用户名，非 ID
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 构造函数（用于转换）
    public static PostResponse from(Post post) {
        PostResponse response = new PostResponse();
        response.id = post.getId();
        response.title = post.getTitle();
        response.content = post.getContent();
        response.author = post.getAuthor().getUsername();
        response.createdAt = post.getCreatedAt();
        response.updatedAt = post.getUpdatedAt();
        return response;
    }

    // getters (no setters needed)
}