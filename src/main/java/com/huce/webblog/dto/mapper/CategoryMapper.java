package com.huce.webblog.dto.mapper;

import com.huce.webblog.dto.response.CategoryResponse;
import com.huce.webblog.entity.Category;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryMapper {
    public CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .cname(category.getCname())
                .cdesc(category.getCdesc())
                .build();
    }
}
