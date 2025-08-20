package com.huce.webblog.controller;

import com.huce.webblog.dto.ApiResponse;
import com.huce.webblog.dto.request.CommentRequest;
import com.huce.webblog.dto.request.UpdateCommentRequest;
import com.huce.webblog.dto.response.CommentResponse;
import com.huce.webblog.entity.Comment;
import com.huce.webblog.service.ICommentService;
import com.huce.webblog.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blog/comment")
@AllArgsConstructor
public class CommentController {
    private final ICommentService commentService;
    UserService userService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<Comment>> comment(
            @RequestBody CommentRequest commentRequest
    ) {
        String uid = userService.fetchMyInfo().getId();
        Comment comment = commentService.addComment(commentRequest.getParentId(), commentRequest.getContent(), uid, commentRequest.getPid());
        ApiResponse<Comment> apiResponse = ApiResponse.<Comment>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(comment)
                .build();

        return ResponseEntity.ok()
                .body(apiResponse);
    }

    @PutMapping("/update/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest updateCommentRequest) {
        String uid = userService.fetchMyInfo().getId();
        ApiResponse<CommentResponse> apiResponse = ApiResponse.<CommentResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(commentService.updateComment(commentId, uid, updateCommentRequest.getPid(), updateCommentRequest.getContent()))
                .build();

        return ResponseEntity.ok()
                .body(apiResponse);
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> deleteComment(@PathVariable Long commentId) {
        String uid = userService.fetchMyInfo().getId();
        ApiResponse<CommentResponse> apiResponse = ApiResponse.<CommentResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(commentService.deleteComment(commentId, uid))
                .build();

        return ResponseEntity.ok()
                .body(apiResponse);
    }


    @GetMapping("/{pid}")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(@PathVariable String pid) {

        ApiResponse<List<CommentResponse>> apiResponse = ApiResponse.<List<CommentResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(commentService.getComments(pid))
                .build();

        return ResponseEntity.ok()
                .body(apiResponse);
    }
}