package com.example.blog_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePostRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    // getters & setters
}
