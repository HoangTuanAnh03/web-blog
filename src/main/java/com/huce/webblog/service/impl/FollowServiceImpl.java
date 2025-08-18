package com.huce.webblog.service.impl;

import com.huce.webblog.dto.response.SimpInfoUserResponse;
import com.huce.webblog.entity.Follow;
import com.huce.webblog.repository.FollowRepository;
import com.huce.webblog.service.IFollowService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FollowServiceImpl implements IFollowService {
    UserServiceImpl userService;
    FollowRepository followRepository;
    @Override
    public Long countFollowByFollowerId(String followerId) {
        return followRepository.countFollowByFollowerId(followerId);
    }

    @Override
    public Long countFollowByFollowingId(String followingId) {
        return followRepository.countFollowByFollowingId(followingId);
    }

    @Override
    public List<SimpInfoUserResponse> followByFollowerId(String followerId){
        List<Follow> follows = followRepository.findAllByFollowerId(followerId);

        List<String> uids = follows.stream().map(Follow::getFollowingId).distinct().toList();
        return userService.fetchUserByIdIn(new ArrayList<>(uids));
    }

    @Override
    public List<SimpInfoUserResponse> followByFollowingId(String followingId){
        List<Follow> follows = followRepository.findAllByFollowingId(followingId);

        List<String> uids = follows.stream().map(Follow::getFollowerId).distinct().toList();
        return userService.fetchUserByIdIn(new ArrayList<>(uids));
    }

    @Override
    public Follow follow(String followerId, String followingId) {
        Follow follow = followRepository.findFirstByFollowerIdAndFollowingId(followerId, followingId);
        if(follow != null){
            followRepository.delete(follow);
            return follow;
        }
        return followRepository.save(Follow.builder().followerId(followerId).followingId(followingId).build());
    }

    @Override
    public boolean isFollowing(String authorId, String uid) {
        return followRepository.findFirstByFollowerIdAndFollowingId(uid, authorId) != null;
    }
}
