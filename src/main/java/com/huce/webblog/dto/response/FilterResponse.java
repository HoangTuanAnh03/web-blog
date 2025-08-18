package com.huce.webblog.dto.response;


import lombok.Data;

@Data
public class FilterResponse {
    private String filtered_content;
    private boolean has_sensitive_content;
}
