package com.huce.webblog.controller;

import com.huce.webblog.dto.ApiResponse;
import com.huce.webblog.dto.request.PostRequest;
import com.huce.webblog.dto.response.PostResponse;
import com.huce.webblog.dto.response.PostSummaryAIResponse;
import com.huce.webblog.dto.response.PostSummaryResponse;
import com.huce.webblog.service.IBlogService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blog/post")
@AllArgsConstructor
public class PostController {
    private final IBlogService postService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<PostResponse>> create(
            @RequestBody PostRequest postRequest,
            @RequestHeader("X-Auth-User-Id") String uid
    ) {
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
    public ResponseEntity<ApiResponse<PostResponse>> view(@PathVariable(value = "pid") String pid, @RequestHeader(value = "X-Auth-User-Id", required = false) String uid) {
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

    @DeleteMapping("/{pid}")
    public ResponseEntity<ApiResponse<PostResponse>> deletePost(
            @PathVariable() String pid,
            @RequestHeader(value = "X-Auth-User-Id", required = false, defaultValue = "") String uid,
            @RequestHeader(value = "X-Auth-User-Authorities", required = false, defaultValue = "") String role
    ) {
        boolean isAdmin = role.equalsIgnoreCase("role_admin");
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
}
