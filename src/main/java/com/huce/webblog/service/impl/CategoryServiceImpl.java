package com.huce.webblog.service.impl;

import com.huce.webblog.advice.exception.BadRequestException;
import com.huce.webblog.dto.mapper.CategoryMapper;
import com.huce.webblog.dto.request.CreateCategoryRequest;
import com.huce.webblog.dto.response.CategoryResponse;
import com.huce.webblog.entity.Category;
import com.huce.webblog.repository.CategoryRepository;
import com.huce.webblog.service.ICategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements ICategoryService {

    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAllByIsDeletedFalse()
                .stream()
                .map(this.categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse findByCid(Long cid) {
        return categoryMapper.toCategoryResponse(categoryRepository.findFirstById(cid));
    }

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest, boolean isAdmin) {
        if (isAdmin) {
            String cname = createCategoryRequest.getName();
            if (categoryRepository.existsByCnameAndIsDeleted(cname, false)) {
                throw new BadRequestException("Category existed");
            }

            Category c = Category.builder()
                    .cname(cname)
                    .cdesc(createCategoryRequest.getDesc())
                    .build();

            return categoryMapper.toCategoryResponse(categoryRepository.save(c));
        }
        throw new BadRequestException("Not access");
    }

    @Override
    public CategoryResponse deleteCategory(Long cid, boolean isAdmin) {
        Category c = categoryRepository.findFirstByIdAndIsDeletedFalse(cid);
        if (c == null) throw new BadRequestException("Category not found");
        if (c.isDeleted()) {
            throw new BadRequestException("Category already deleted");
        }
        if (isAdmin) {
            c.setDeleted(true);
            categoryRepository.save(c);
            return categoryMapper.toCategoryResponse(c);
        }
        throw new BadRequestException("Not access");
    }

}
