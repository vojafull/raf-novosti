package org.example.demo.repositories;

import org.example.demo.entities.News;

import java.util.List;

public interface NewsRepository {
    List<News> findAll(int page, int pageSize);
    int countAll();

    List<News> findByCategory(Integer categoryId, int page, int pageSize);
    int countByCategory(Integer categoryId);

    List<News> findLatest(int limit);

    List<News> findMostVisited(int limit, int days);

    List<News> search(String query, int page, int pageSize);
    int countSearch(String query);

    List<News> findByTag(Integer tagId, int page, int pageSize);
    int countByTag(Integer tagId);

    News findById(Integer id);

    News save(News news);
    News update(News news);
    void delete(Integer id);

    boolean hasVisited(Integer newsId, String sessionId);
    void recordVisit(Integer newsId, String sessionId);
    void incrementVisitCount(Integer newsId);

    String getReaction(Integer newsId, String sessionId);
    void saveReaction(Integer newsId, String sessionId, String reaction);
    void updateReaction(Integer newsId, String sessionId, String reaction);
    void deleteReaction(Integer newsId, String sessionId);

    int countLikes(Integer newsId);
    int countDislikes(Integer newsId);

    List<News> findRelated(Integer newsId, int limit);

    List<News> findMostReacted(int limit);
}
