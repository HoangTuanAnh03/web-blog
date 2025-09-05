package com.huce.webblog.service;

import com.huce.webblog.dto.request.SearchRequest;
import com.huce.webblog.dto.response.FilterResponse;

public interface IPythonService {
    public FilterResponse filterContent(String content);

    public String summaryContent(String content);

    public Object saveVector(String content, String pid, String title);

    public Object chat(SearchRequest searchRequest);

    public Object deleteVector(String pid);
}
