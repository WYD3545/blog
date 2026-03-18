package com.example.blog_system.controller;
import com.example.blog_system.dto.CreatePostRequest;
import com.example.blog_system.dto.PostResponse;
import com.example.blog_system.dto.UpdatePostRequest;
import com.example.blog_system.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // 创建文章（需登录）
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestBody @Valid CreatePostRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7); // 移除 "Bearer "
        PostResponse response = postService.createPost(request, token);
        return ResponseEntity.ok(response);
    }

    // 获取单篇文章（公开）
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        PostResponse response = postService.getPostById(id);
        return ResponseEntity.ok(response);
    }

    // 分页获取文章列表（公开）
    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (size > 50) size = 50; // 防止恶意请求
        Page<PostResponse> posts = postService.getAllPosts(page, size);
        return ResponseEntity.ok(posts);
    }
    // 根据标题搜索文章（公开）
    @GetMapping("/search")
    public ResponseEntity<Page<PostResponse>> searchPosts(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (size > 50) size = 50; // 防止恶意请求
        Page<PostResponse> posts = postService.searchPostsByTitle(title, page, size);
        return ResponseEntity.ok(posts);
    }
    // 更新文章（需登录 + 作者权限）
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @RequestBody @Valid UpdatePostRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        PostResponse response = postService.updatePost(id, request, token);
        return ResponseEntity.ok(response);
    }

    // 删除文章（需登录 + 作者权限）
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        postService.deletePost(id, token);
        return ResponseEntity.noContent().build();
    }


}
