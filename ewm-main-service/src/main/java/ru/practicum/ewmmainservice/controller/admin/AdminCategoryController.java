package ru.practicum.ewmmainservice.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmainservice.dto.category.CategoryDto;
import ru.practicum.ewmmainservice.dto.category.NewCategoryDto;
import ru.practicum.ewmmainservice.service.CategoryService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "admin/categories")
public class AdminCategoryController {

    CategoryService categoryService;

    @Autowired
    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Admin POST request to create Category");
        return categoryService.createCategory(newCategoryDto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@Valid @RequestBody CategoryDto categoryDto, @PathVariable Long catId) {
        log.info("Admin PATCH request to update Category by ID= {}", catId);
        return categoryService.updateCategory(categoryDto, catId);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createCategory(@PathVariable Long catId) {
        log.info("Admin DELETE request to delete Category by ID= {}", catId);
        categoryService.deleteCategory(catId);
    }
}
