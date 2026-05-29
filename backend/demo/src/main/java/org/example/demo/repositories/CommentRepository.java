package org.example.demo.repositories;

import org.example.demo.entities.Comment;

import java.util.List;

public interface CommentRepository {
    List<Comment> findByNewsId(Integer newsId, int page, int pageSize);
    int countByNewsId(Integer newsId);
    Comment save(Comment comment);

    String getReaction(Integer commentId, String sessionId);
    void saveReaction(Integer commentId, String sessionId, String reaction);
    void updateReaction(Integer commentId, String sessionId, String reaction);
    void deleteReaction(Integer commentId, String sessionId);
    int getLikes(Integer commentId);
    int getDislikes(Integer commentId);
}
