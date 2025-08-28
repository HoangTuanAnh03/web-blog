package com.huce.webblog.repository;

import com.huce.webblog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, String>, JpaSpecificationExecutor<Post> {
    List<Post> findByTitleIgnoreCaseContainingOrRawContentIgnoreCaseContaining(String title, String content);

    Page<Post> findAllByHasSensitiveContent(Boolean hasSensitiveContent,Pageable pageable);

    Post findFirstById(String pid);

    Post findFirstByIdAndIsDeletedFalse(String pid);

    @Query(value = """
			SELECT DISTINCT p.*
			FROM category_blog cp1
			JOIN category_blog cp2 ON cp1.cid = cp2.cid
			JOIN post p ON p.id = cp2.pid
			WHERE cp1.pid = :pid
			  AND cp2.pid != :pid
			  AND p.is_deleted = false
			ORDER BY RAND()
			LIMIT 2
			""", nativeQuery = true)
    List<Post> findRelatedPosts(@Param("pid") String pid);

    Long countPostByHasSensitiveContentIsTrueAndIsDeletedFalse();
    Long countPostByIsDeletedFalse();


    @Query("SELECT MONTH(p.createdAt) as month, YEAR(p.createdAt) as year, COUNT(p) as count " +
            "FROM Post p " +
            "WHERE p.createdAt >= :startDate AND p.isDeleted = false " +
            "GROUP BY YEAR(p.createdAt), MONTH(p.createdAt) " +
            "ORDER BY YEAR(p.createdAt), MONTH(p.createdAt)")
    List<Object[]> countPostsByMonthInLastSixMonths(LocalDateTime startDate);


    Page<Post> findByUidAndIsDeletedFalse(String uid, Pageable pageable);
}