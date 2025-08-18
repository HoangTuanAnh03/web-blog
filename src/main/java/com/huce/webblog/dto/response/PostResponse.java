package com.huce.webblog.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
    private String id;
    @JsonIgnore
    private String uid;
    private SimpInfoUserResponse userResponse;
    private String title;
    private String content;
    private String cover;
    private int viewsCount;
    private int commentsCount;
    private String summaryAi;
    private boolean hasSensitiveContent;
    private String rawContent;
    private List<CommentResponse> comments;
    private List<String> category;
    private List<String> hashtags;
    private List<PostSummaryResponse> relatedPosts;
    private LocalDateTime createdAt;
}