package ru.practicum.ewmmainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmainservice.model.Category;

public interface CategoriesRepository extends JpaRepository<Category, Long> {

}