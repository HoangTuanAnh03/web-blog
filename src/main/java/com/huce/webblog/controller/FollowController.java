package com.huce.webblog.controller;

import com.huce.webblog.advice.exception.BadRequestException;
import com.huce.webblog.dto.ApiResponse;
import com.huce.webblog.dto.response.SimpInfoUserResponse;
import com.huce.webblog.entity.Follow;
import com.huce.webblog.entity.Post;
import com.huce.webblog.repository.PostRepository;
import com.huce.webblog.service.IFollowService;
import com.huce.webblog.service.UserService;
import com.huce.webblog.util.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/blog/follow")
@AllArgsConstructor
public class FollowController {
	IFollowService followService;
	PostRepository postRepository;
	UserService userService;
	SecurityUtil securityUtil;


	@PostMapping("/{followingId}")
	public ResponseEntity<ApiResponse<Follow>> follow(@PathVariable String followingId){
		String uid = userService.fetchMyInfo().getId();
		ApiResponse<Follow> apiResponse = ApiResponse.<Follow>builder()
				.code(HttpStatus.OK.value())
				.message("Success")
				.data(followService.follow(uid, followingId))
				.build();
		return ResponseEntity.ok()
				.body(apiResponse);
	}

	@GetMapping("/{followingId}")
	public ResponseEntity<ApiResponse<Map<String, List<SimpInfoUserResponse>>>> getFollowDetail(@PathVariable(required = true) String followingId){
		Map<String, List<SimpInfoUserResponse>> map = new HashMap<>();
		map.put("follower", followService.followByFollowingId(followingId));
		map.put("following", followService.followByFollowerId(followingId));

		ApiResponse<Map<String, List<SimpInfoUserResponse>>> apiResponse = ApiResponse.<Map<String, List<SimpInfoUserResponse>>>builder()
				.code(HttpStatus.OK.value())
				.message("Success")
				.data(map)
				.build();
		return ResponseEntity.ok()
				.body(apiResponse);
	}

	@GetMapping("/isFollow")
	public ResponseEntity<ApiResponse<Boolean>> isFollow(
			@RequestParam(required = false) String pid,
			@RequestParam(required = false) String userId) {

		String uid = userService.fetchMyInfo().getId();

		String targetUid = null;

		if (pid != null && !pid.isBlank()) {
			Post post = postRepository.findFirstById(pid);
			if (post == null) {
				throw new BadRequestException("PID not found");
			}
			targetUid = post.getUid();
		} else if (userId != null && !userId.isBlank()) {
			targetUid = userId;
		} else {
			throw new BadRequestException("Missing both pid and userId");
		}
		boolean isFollowed = followService.isFollowing(targetUid, uid);
		ApiResponse<Boolean> apiResponse = ApiResponse.<Boolean>builder()
				.code(HttpStatus.OK.value())
				.message("Success")
				.data(isFollowed)
				.build();

		return ResponseEntity.ok(apiResponse);
	}
}
