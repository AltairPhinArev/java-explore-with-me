package ru.practicum.ewmmainservice.controller.pub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmainservice.dto.category.CategoryDto;
import ru.practicum.ewmmainservice.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/categories")
public class PublicCategoriesController {

    CategoryService categoryService;

    @Autowired
    public PublicCategoriesController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDto> getAll(@RequestParam (defaultValue = "0") @PositiveOrZero Integer from,
                                    @RequestParam (defaultValue = "10") @Positive Integer size) {
        log.info("PUBLIC GET request to get all Categories by page parameters from/size= {}/{}", from, size);
        return categoryService.getAll(from, size);
    }

    @GetMapping("{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        log.info("PUBLIC GET request to get Category by ID= {}", catId);
        return categoryService.getCategoryById(catId);
    }
}
