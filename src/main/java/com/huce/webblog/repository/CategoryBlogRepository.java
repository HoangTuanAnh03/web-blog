package com.huce.webblog.repository;

import com.huce.webblog.entity.CategoryBlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryBlogRepository extends JpaRepository<CategoryBlog, Long> {

}