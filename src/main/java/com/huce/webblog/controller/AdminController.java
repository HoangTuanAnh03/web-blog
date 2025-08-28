package com.huce.webblog.controller;

import com.huce.webblog.dto.ApiResponse;
import com.huce.webblog.dto.record.CategoryPostCountDTO;
import com.huce.webblog.service.IBlogService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/blog/admin")
@AllArgsConstructor
public class AdminController {
    IBlogService blogService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> dashboard() {

        Map<String, Object> res = new HashMap<>();
        res.put("pie_chart", blogService.postStas());
        res.put("bar_chart", blogService.getPostCountByMonthInLastSixMonths());
        res.put("table", blogService.getCategoryPostStas());
        ApiResponse<Map<String, Object>> apiResponse = ApiResponse.<Map<String, Object>>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(res)
                .build();
        return ResponseEntity.ok()
                .body(apiResponse);
    }

    @GetMapping("/category")
    public ResponseEntity<ApiResponse<List<CategoryPostCountDTO>>> category() {

        ApiResponse<List<CategoryPostCountDTO>> apiResponse = ApiResponse.<List<CategoryPostCountDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(blogService.getCategoryPostStas())
                .build();
        return ResponseEntity.ok()
                .body(apiResponse);
    }
}
