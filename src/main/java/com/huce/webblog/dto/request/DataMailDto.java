package com.huce.webblog.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataMailDto {
    private String to;
    private String subject;
    private Map<String, Object> props;
}