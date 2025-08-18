package com.huce.webblog.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    @JsonIgnore
    private String userId;
    private SimpInfoUserResponse userResponse;
    private int leftValue;
    private int rightValue;
    private List<CommentResponse> replies = new ArrayList<>();
    private LocalDateTime createdAt;
}
