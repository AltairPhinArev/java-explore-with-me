package ru.practicum.ewmmainservice.service.impl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import ru.practicum.ewmmainservice.dto.category.CategoryDto;
import ru.practicum.ewmmainservice.dto.category.NewCategoryDto;
import ru.practicum.ewmmainservice.errorhandling.exceptions.ConflictException;
import ru.practicum.ewmmainservice.errorhandling.exceptions.NotFoundException;
import ru.practicum.ewmmainservice.errorhandling.exceptions.ValidationException;
import ru.practicum.ewmmainservice.mapper.CategoryMapper;
import ru.practicum.ewmmainservice.repository.CategoriesRepository;
import ru.practicum.ewmmainservice.service.CategoryService;
import ru.practicum.ewmmainservice.service.EventService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    CategoriesRepository categoriesRepository;

    EventService eventService;

    @Lazy
    @Autowired
    public CategoryServiceImpl(CategoriesRepository categoriesRepository, EventService eventService) {
        this.categoriesRepository = categoriesRepository;
        this.eventService = eventService;
    }

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        try {
            if (newCategoryDto.getName() == null || newCategoryDto.getName().isEmpty()
                    || newCategoryDto.getName().isBlank()) {
                throw new ValidationException("Incorrectly made request.");
            } else {
                return CategoryMapper.toCategoryDto(
                        categoriesRepository
                                .save(CategoryMapper.toCategoryCreate(newCategoryDto)));
            }
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage(), e);
        }
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDtoToUpd, Long catId) {

        CategoryDto categoryDto = CategoryMapper.toCategoryDto(categoriesRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with ID + " + catId + " doesn't exist")));

        if (categoryDtoToUpd.getId() == null) {
            categoryDtoToUpd.setId(categoryDto.getId());
        }

        if (categoryDtoToUpd.getName() == null) {
            categoryDtoToUpd.setName(categoryDto.getName());
        }
        CategoryDto category;

        try {
            category   = CategoryMapper.toCategoryDto(categoriesRepository.save(CategoryMapper.toCategory(categoryDtoToUpd)));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage(), e);
        }
        return category;
    }

    @Override
    public void deleteCategory(Long catId) {
        if (eventService.getByCategoryId(catId).isEmpty() && checkCategory(catId)) {
                    categoriesRepository.deleteById(catId);
        } else {
            throw new ConflictException("dsd");
        }
    }

    @Override
    public List<CategoryDto> getAll(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from, size);
        return categoriesRepository.findAll(pageRequest).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        return CategoryMapper.toCategoryDto(categoriesRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with ID + " + catId + " doesn't exist")));
    }

    @Override
    public Boolean checkCategory(Long catId) {
        return categoriesRepository.existsById(catId);
    }
}