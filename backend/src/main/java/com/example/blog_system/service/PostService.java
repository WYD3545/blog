package com.example.blog_system.service;
import com.example.blog_system.dto.CreatePostRequest;
import com.example.blog_system.dto.PostResponse;
import com.example.blog_system.dto.UpdatePostRequest;
import com.example.blog_system.entity.Post;
import com.example.blog_system.entity.User;
import com.example.blog_system.exception.PostNotFoundException;
import com.example.blog_system.repository.PostRepository;
import com.example.blog_system.repository.UserRepository;
import com.example.blog_system.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository; // 你需要这个来查用户

    @Autowired
    private JwtUtil jwtUtil; // 你的 JWT 工具类
    //创建文章
    public PostResponse createPost(CreatePostRequest request, String token) {
        String username = jwtUtil.extractUsername(token);
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setAuthor(author);

        Post savedPost = postRepository.save(post);
        return PostResponse.from(savedPost);
    }

    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));
        return PostResponse.from(post);
    }
    // 根据标题搜索文章（公开）
    public Page<PostResponse> searchPostsByTitle(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByTitleContaining(keyword, pageable)
                .map(PostResponse::from);
    }

    public Page<PostResponse> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findAll(pageable)
                .map(PostResponse::from);
    }

    public PostResponse updatePost(Long id, UpdatePostRequest request, String token) {
        String username = jwtUtil.extractUsername(token);
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 关键：只允许作者修改
        Post post = postRepository.findByIdAndAuthorId(id, author.getId())
                .orElseThrow(() -> new PostNotFoundException("Post not found or access denied"));

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        Post updatedPost = postRepository.save(post);
        return PostResponse.from(updatedPost);
    }

    public void deletePost(Long id, String token) {
        String username = jwtUtil.extractUsername(token);
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 关键：只允许作者删除
        Post post = postRepository.findByIdAndAuthorId(id, author.getId())
                .orElseThrow(() -> new PostNotFoundException("Post not found or access denied"));

        postRepository.delete(post);
    }
}
