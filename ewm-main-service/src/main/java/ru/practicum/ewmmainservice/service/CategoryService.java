package ru.practicum.ewmmainservice.service;

import ru.practicum.ewmmainservice.dto.category.CategoryDto;
import ru.practicum.ewmmainservice.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(CategoryDto categoryDto, Long catId);

    void deleteCategory(Long catId);

    List<CategoryDto> getAll(Integer from , Integer size);

    Boolean checkCategory(Long catId);

    CategoryDto getCategoryById(Long catId);
}
