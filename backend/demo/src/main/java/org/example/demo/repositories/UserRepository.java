package org.example.demo.repositories;


import org.example.demo.entities.User;

import java.util.List;

public interface UserRepository {
    User findByEmail(String email);
    User findById(Integer id);
    List<User> findAll(int page, int pageSize);
    int countAll();
    User save(User user);
    User update(User user);
    boolean existsByEmail(String email);
}
