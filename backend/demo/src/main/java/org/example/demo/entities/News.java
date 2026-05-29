package org.example.demo.entities;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class News {

    private Integer id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private int visitCount;
    private int likes;
    private int dislikes;

    private User author;

    private Category category;

    private List<Tag> tags;

    private List<Comment> comments;

    public News(Integer id, String title, String content, LocalDateTime createdAt,
                int visitCount, int likes, int dislikes, User author, Category category) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.visitCount = visitCount;
        this.likes = likes;
        this.dislikes = dislikes;
        this.author = author;
        this.category = category;
    }
}