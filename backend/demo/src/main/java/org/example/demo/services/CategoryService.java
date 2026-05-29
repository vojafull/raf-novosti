package org.example.demo.services;

import org.example.demo.entities.Category;
import org.example.demo.repositories.CategoryRepository;
import org.example.demo.requests.CreateCategoryRequest;
import org.example.demo.util.ServiceResponse;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryService {

    @Inject
    private CategoryRepository categoryRepository;

    public Map<String, Object> getAll(int page, int pageSize) {

        List<Category> categories = categoryRepository.findAll(page, pageSize);
        int total = categoryRepository.countAll();

        Map<String, Object> result = new HashMap<>();
        result.put("categories", categories);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        return result;
    }

    public Map<String, Object> findById(Integer id) {

        Category category = categoryRepository.findById(id);

        if (category == null)
            return ServiceResponse.error("Kategorija nije pronadjena", 404);

        return ServiceResponse.success("category", category);
    }

    public Map<String, Object> create(CreateCategoryRequest request) {

        if (request.getName() == null || request.getName().isBlank() ||
                request.getDescription() == null || request.getDescription().isBlank())

            return ServiceResponse.error("Ime i opis su obavezni", 400);

        if (categoryRepository.existsByName(request.getName()))
            return ServiceResponse.error("Kategorija sa ovim imenom vec postoji", 409);

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return ServiceResponse.success("category", categoryRepository.save(category));
    }

    public Map<String, Object> update(Integer id, CreateCategoryRequest request) {

        if (request.getName() == null || request.getName().isBlank() ||
                request.getDescription() == null || request.getDescription().isBlank())

            return ServiceResponse.error("Ime i opis su obavezni", 400);

        Category existing = categoryRepository.findById(id);
        if (existing == null)
            return ServiceResponse.error("Kategorija nije pronadjena", 404);

        if (!existing.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName()))
            return ServiceResponse.error("Kategorija sa ovim imenom vec postoji", 409);

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        return ServiceResponse.success("category", categoryRepository.update(existing));
    }

    public Map<String, Object> delete(Integer id) {

        Category existing = categoryRepository.findById(id);
        if (existing == null)
            return ServiceResponse.error("Kategorija nije pronadjena", 404);

        if (categoryRepository.hasNews(id))
            return ServiceResponse.error("Nije moguce obrisati kategoriju koja sadrzi vesti", 409);

        categoryRepository.delete(id);
        return ServiceResponse.success("success", true);
    }
}