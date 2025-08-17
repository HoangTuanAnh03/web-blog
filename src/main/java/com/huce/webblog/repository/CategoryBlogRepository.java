package com.huce.webblog.repository;

import com.huce.webblog.dto.record.CategoryPostCountDTO;
import com.huce.webblog.entity.CategoryBlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryBlogRepository extends JpaRepository<CategoryBlog, Long> {
    @Query("SELECT new com.huce.webblog.dto.record.CategoryPostCountDTO(cb.category.id, cb.category.cname, cb.category.cdesc, COUNT(cb.post.id)) " +
            "FROM CategoryBlog cb GROUP BY cb.category.cname, cb.category.id, cb.category.cname")
    List<CategoryPostCountDTO> countPostsByCategory();
}