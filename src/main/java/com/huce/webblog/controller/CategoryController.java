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
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll(){
        ApiResponse<List<CategoryResponse>> apiResponse = ApiResponse.<List<CategoryResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(categoryService.getAll())
                .build();

        return ResponseEntity.ok()
                .body(apiResponse);
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            @RequestBody CreateCategoryRequest createCategoryRequest,
            @RequestHeader(value = "X-Auth-User-Authorities", required = false, defaultValue = "") String role
    ){
        boolean isAdmin = role.equalsIgnoreCase("role_admin");
        CategoryResponse categoryResponse = categoryService.createCategory(createCategoryRequest, isAdmin);
        ApiResponse<CategoryResponse> apiResponse = ApiResponse.<CategoryResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(categoryResponse)
                .build();

        return ResponseEntity.ok()
                .body(apiResponse);
    }

    @DeleteMapping("/{cid}")
    public ResponseEntity<ApiResponse<CategoryResponse>> deleteCategory(
            @PathVariable() Long cid,
            @RequestHeader(value = "X-Auth-User-Authorities", required = false, defaultValue = "") String role
    ){
        boolean isAdmin = role.equalsIgnoreCase("role_admin");
        ApiResponse<CategoryResponse> apiResponse = ApiResponse.<CategoryResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(categoryService.deleteCategory(cid, isAdmin))
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
