package com.huce.webblog.service;

import com.huce.webblog.dto.response.FilterResponse;

public interface IPythonService {
    public FilterResponse filterContent(String content);

    public String summaryContent(String content);
}
