package org.example.demo.repositories;

import org.example.demo.entities.Category;

import java.util.List;

public interface CategoryRepository {
    List<Category> findAll(int page, int pageSize);
    int countAll();
    Category findById(Integer id);
    Category save(Category category);
    Category update(Category category);
    void delete(Integer id);
    boolean existsByName(String name);
    boolean hasNews(Integer categoryId);
}
