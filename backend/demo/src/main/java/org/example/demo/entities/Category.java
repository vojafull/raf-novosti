package org.example.demo.entities;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    private Integer id;
    private String name;
    private String description;
}
