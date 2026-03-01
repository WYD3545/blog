package com.example.blog_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

//DTO 是专门用于“在不同层之间安全、清晰地传递数据”的对象，它隔离了内部实体（Entity）与外部接口。
@Getter
@Setter
public class CreatePostRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    // getters & setters
}
