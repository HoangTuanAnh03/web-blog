package com.huce.webblog.repository;

import com.huce.webblog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findFirstById(Long id);
    List<Category> findAllByIsDeletedFalse();
    Boolean existsByCnameAndIsDeleted(String cname, boolean isDeleted);
    List<Category> findByIdIn(List<Long> cids);
    Category findFirstByIdAndIsDeletedFalse(Long cid);

}