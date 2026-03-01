package com.example.blog_system.repository;

import com.example.blog_system.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAll(Pageable pageable);//重写findAll为分页查询方法，Pageable pageable：包含分页信息（页码、每页大小、排序规则）
    //Page<Post>：不仅包含当前页数据，还包含总页数、总数等元信息
    // 可选：按作者查询（用于权限校验）
    Optional<Post> findByIdAndAuthorId(Long postId, Long authorId);

    // 按标题模糊搜索（分页）
    Page<Post> findByTitleContaining(String keyword, Pageable pageable);

    // 按作者 ID 查询所有文章（分页）
    Page<Post> findByAuthorId(Long authorId, Pageable pageable);

    // 统计某作者的文章数
    long countByAuthorId(Long authorId);
}
//JpaRepository 是 Spring Data JPA 提供的核心接口,继承后，自动获得 所有 CRUD 方法