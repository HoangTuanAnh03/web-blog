package com.huce.webblog.repository;

import com.huce.webblog.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Follow findFirstByFollowerIdAndFollowingId(String followerId, String followingId);
    Long countFollowByFollowerId(String followerId);
    Long countFollowByFollowingId(String followingId);

    List<Follow> findAllByFollowingId(String followingId);

    List<Follow> findAllByFollowerId(String followerId);
}