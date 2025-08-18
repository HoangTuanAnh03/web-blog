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

public class PostSummaryResponse {
    private String id;
    private String title;
    @JsonIgnore
    private String uid;
    private String cover;
    private String excerpt;
    private SimpInfoUserResponse userResponse;
    private int viewsCount;
    private int commentsCount;
    private boolean hasSensitiveContent;
    private String content;
    private List<String> category;
    private LocalDateTime createdAt;
}