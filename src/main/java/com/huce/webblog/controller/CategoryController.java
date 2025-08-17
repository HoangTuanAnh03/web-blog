package com.huce.webblog.controller;

import com.huce.webblog.dto.ApiResponse;
import com.huce.webblog.dto.request.CreateCategoryRequest;
import com.huce.webblog.dto.response.CategoryResponse;
import com.huce.webblog.service.ICategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blog/category")
@AllArgsConstructor
public class CategoryController {
    ICategoryService categoryService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll() {
        ApiResponse<List<CategoryResponse>> apiResponse = ApiResponse.<List<CategoryResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(categoryService.getAll())
                .build();

        return ResponseEntity.ok()
                .body(apiResponse);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            @RequestBody CreateCategoryRequest createCategoryRequest
    ) {
        CategoryResponse categoryResponse = categoryService.createCategory(createCategoryRequest);
        ApiResponse<CategoryResponse> apiResponse = ApiResponse.<CategoryResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(categoryResponse)
                .build();

        return ResponseEntity.ok()
                .body(apiResponse);
    }

    @DeleteMapping("/delete/{cid}")
    public ResponseEntity<ApiResponse<CategoryResponse>> deleteCategory(
            @PathVariable() Long cid
    ) {
        ApiResponse<CategoryResponse> apiResponse = ApiResponse.<CategoryResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(categoryService.deleteCategory(cid))
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
