package org.example.demo.entities;

import lombok.*;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private Integer id;
    private String authorName;
    private String content;
    private LocalDateTime createdAt;
    private Integer newsId;
    private int likes;
    private int dislikes;
}
