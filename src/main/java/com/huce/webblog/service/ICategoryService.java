package com.huce.webblog.service;

import com.huce.webblog.dto.request.CreateCategoryRequest;
import com.huce.webblog.dto.response.CategoryResponse;

import java.util.List;

public interface ICategoryService {
    public List<CategoryResponse> getAll();
    public CategoryResponse findByCid(Long cid);
    CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest);

    CategoryResponse deleteCategory(Long cid);

}
