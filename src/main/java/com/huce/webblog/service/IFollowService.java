package com.huce.webblog.service;

import com.huce.webblog.dto.response.SimpInfoUserResponse;
import com.huce.webblog.entity.Follow;

import java.util.List;

public interface IFollowService {
    Long countFollowByFollowerId(String followerId);
    Long countFollowByFollowingId(String followingId);

    Follow follow(String followerId, String followingId);

    boolean isFollowing(String authorId, String uid);

    List<SimpInfoUserResponse> followByFollowerId(String followerId);

    List<SimpInfoUserResponse> followByFollowingId(String followingId);
}
