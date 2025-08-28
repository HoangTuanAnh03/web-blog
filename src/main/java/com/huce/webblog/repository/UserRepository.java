package com.huce.webblog.repository;

import com.huce.webblog.entity.Category;
import com.huce.webblog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndActive(String email, Boolean active);

    Optional<User> findFirstByEmailAndActiveAndIsLocked(String email, Boolean active, Boolean lock);

    Optional<User> findByIdAndActive(String id, Boolean active);

    List<User> findByIdIn(List<String> ids);
    Page<User> findAllByRole(String role, Pageable pageable);

}
