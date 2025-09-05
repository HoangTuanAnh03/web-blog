package com.huce.webblog.controller;

import com.huce.webblog.dto.ApiResponse;
import com.huce.webblog.dto.request.PostRequest;
import com.huce.webblog.dto.request.SearchRequest;
import com.huce.webblog.dto.response.PostResponse;
import com.huce.webblog.dto.response.PostSummaryAIResponse;
import com.huce.webblog.dto.response.PostSummaryResponse;
import com.huce.webblog.dto.response.UserResponse;
import com.huce.webblog.entity.User;
import com.huce.webblog.service.IBlogService;
import com.huce.webblog.service.IPythonService;
import com.huce.webblog.service.UserService;
import com.huce.webblog.util.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/blog/post")
@AllArgsConstructor
public class PostController {
    private final IBlogService postService;
    IPythonService pythonService;
    SecurityUtil securityUtil;
    UserService userService;

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<Object>> create(
            @RequestBody SearchRequest searchRequest
    ) {
        ApiResponse<Object> apiResponse = ApiResponse.<Object>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(pythonService.chat(searchRequest))
                .build();

        return ResponseEntity.ok()
                .body(apiResponse);
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<PostResponse>> create(
            @RequestBody PostRequest postRequest
    ) {
        String uid = userService.fetchMyInfo().getId();
        PostResponse post = postService.create(postRequest, uid);
        ApiResponse<PostResponse> apiResponse = ApiResponse.<PostResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(post)
                .build();

        return ResponseEntity.ok()
                .body(apiResponse);
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<PostSummaryAIResponse>> summary(@RequestParam String pid) {
        PostSummaryAIResponse p = postService.viewSummary(pid);
        ApiResponse<PostSummaryAIResponse> apiResponse = ApiResponse.<PostSummaryAIResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(p)
                .build();

        return ResponseEntity.ok()
                .body(apiResponse);
    }

    @GetMapping("/{pid}")
    public ResponseEntity<ApiResponse<PostResponse>> view(
            @PathVariable(value = "pid") String pid) {
        String uid = userService.fetchMyInfo().getId();
        PostResponse p = postService.view(pid, uid);
        ApiResponse<PostResponse> apiResponse = ApiResponse.<PostResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(p)
                .build();

        return ResponseEntity.ok()
                .body(apiResponse);
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<Page<PostSummaryResponse>>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) List<Long> categories
    ) {
        int size = 12;
        if (categories == null) {
            categories = List.of();
        }
        ApiResponse<Page<PostSummaryResponse>> apiResponse = ApiResponse.<Page<PostSummaryResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(postService.getAllPostSummary(page, size, search, categories))
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("delete/{pid}")
    public ResponseEntity<ApiResponse<PostResponse>> deletePost(
            @PathVariable() String pid
    ) {
        String uid = userService.fetchMyInfo().getId();
        User currentUser = userService.fetchUserById(uid);
        boolean isAdmin = currentUser.getRole().equalsIgnoreCase("admin");

        ApiResponse<PostResponse> apiResponse = ApiResponse.<PostResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(postService.deletePost(pid, isAdmin, uid))
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/user/{uid}")
    public ResponseEntity<ApiResponse<Page<PostSummaryResponse>>> getByUid(
            @RequestParam(defaultValue = "0") int page,
            @PathVariable() String uid
    ) {
        int size = 6;
        ApiResponse<Page<PostSummaryResponse>> apiResponse = ApiResponse.<Page<PostSummaryResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(postService.getByUid(page, 12, uid))
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/getPostSensitive")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getPostSensitive(
            @RequestParam(defaultValue = "0") int page) {
        ApiResponse<Page<PostResponse>> apiResponse = ApiResponse.<Page<PostResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(postService.getPostHasSensitiveContent(true, page, 12))
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
