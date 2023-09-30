package ru.practicum.ewmmainservice.mapper;

import ru.practicum.ewmmainservice.dto.category.CategoryDto;
import ru.practicum.ewmmainservice.dto.category.NewCategoryDto;
import ru.practicum.ewmmainservice.model.Category;

public class CategoryMapper {

    public static Category toCategoryCreate(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .id(null)
                .name(newCategoryDto.getName())
                .build();
    }

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toCategory(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }
}
