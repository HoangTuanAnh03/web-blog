package com.huce.webblog.service;

import com.huce.webblog.dto.record.CategoryPostCountDTO;
import com.huce.webblog.dto.request.PostRequest;
import com.huce.webblog.dto.response.PostResponse;
import com.huce.webblog.dto.response.PostSummaryAIResponse;
import com.huce.webblog.dto.response.PostSummaryResponse;
import com.huce.webblog.dto.response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface IBlogService {
    Page<PostSummaryResponse> getAllPostSummary(int page, int size, String search, List<Long> categories);

    PostResponse create(PostRequest postRequest, String uid);

    PostResponse view(String pid, String uid);

    PostSummaryAIResponse viewSummary(String pid);

    Map<String, Long> postStas();

    List<Map<String, Object>> getPostCountByMonthInLastSixMonths();

    List<CategoryPostCountDTO> getCategoryPostStas();

    PostResponse deletePost(String pid, boolean isAdmin, String uid);

    Page<PostSummaryResponse> getByUid(int page, int size, String uid);
    Page<PostResponse> getPostHasSensitiveContent(Boolean hasSensitiveContent, int page, int size);

}
