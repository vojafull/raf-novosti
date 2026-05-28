package org.example.demo.entities;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Integer id;
    private String email;
    private String firstName;
    private String lastName;

    private String type;

    private String status;

    private String password;
}
