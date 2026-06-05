package org.example.demo.services;

import org.example.demo.entities.*;
import org.example.demo.repositories.CategoryRepository;
import org.example.demo.repositories.CommentRepository;
import org.example.demo.repositories.NewsRepository;
import org.example.demo.repositories.TagRepository;
import org.example.demo.requests.CreateCommentRequest;
import org.example.demo.requests.CreateNewsRequest;
import org.example.demo.requests.ReactionRequest;
import org.example.demo.util.ServiceResponse;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NewsService {

    @Inject
    private NewsRepository newsRepository;
    @Inject
    private TagRepository tagRepository;
    @Inject
    private CategoryRepository categoryRepository;
    @Inject
    private CommentRepository commentRepository;

    public Map<String, Object> getAll(int page, int pageSize) {

        List<News> newsList = newsRepository.findAll(page, pageSize);

        newsList.forEach(n -> n.setTags(tagRepository.findByNewsId(n.getId())));

        return buildPageResponse(newsList, newsRepository.countAll(), page, pageSize);
    }

    public Map<String, Object> getByCategory(Integer categoryId, int page, int pageSize) {

        List<News> newsList = newsRepository.findByCategory(categoryId, page, pageSize);

        newsList.forEach(n -> n.setTags(tagRepository.findByNewsId(n.getId())));

        return buildPageResponse(newsList, newsRepository.countByCategory(categoryId), page, pageSize);
    }

    public Map<String, Object> getById(Integer newsId, String sessionId) {

        News news = newsRepository.findById(newsId);

        if (sessionId == null || sessionId.isEmpty())
            return ServiceResponse.error("Session ID je obavezan", 400);

        if (news == null)
            return ServiceResponse.error("Vest nije pronadjena", 404);

        news.setTags(tagRepository.findByNewsId(newsId));
        news.setComments(commentRepository.findByNewsId(newsId, 1, 10));
        news.setLikes(newsRepository.countLikes(newsId));
        news.setDislikes(newsRepository.countDislikes(newsId));

        if (!newsRepository.hasVisited(newsId, sessionId)) {

            newsRepository.recordVisit(newsId, sessionId);

            newsRepository.incrementVisitCount(newsId);

            news.setVisitCount(news.getVisitCount() + 1);
        }

        Map<String, Object> response = new HashMap<>();

        response.put("news", news);
        response.put("related", newsRepository.findRelated(newsId, 3));

        response.put("userReaction", newsRepository.getReaction(newsId, sessionId));

        return response;
    }

    public Map<String, Object> create(CreateNewsRequest request, User author) {

        if (request.getTitle() == null || request.getTitle().isBlank() ||
                request.getContent() == null || request.getContent().isBlank() || request.getCategoryId() == null)

            return ServiceResponse.error("Naslov, sadrzaj i kategorija su obavezni", 400);

        Category category = categoryRepository.findById(request.getCategoryId());

        if (category == null)
            return ServiceResponse.error("Kategorija nije pronadjena", 404);

        News news = new News();
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());
        news.setAuthor(author);
        news.setCategory(category);

        News saved = newsRepository.save(news);

        if (request.getTagNames() != null) {
            List<Tag> tags = processTags(request.getTagNames(), saved.getId());
            saved.setTags(tags);
        }

        return ServiceResponse.success("news", saved);
    }

    public Map<String, Object> update(Integer newsId, CreateNewsRequest request, User currentUser) {

        if (request.getTitle() == null || request.getTitle().isBlank() ||
                request.getContent() == null || request.getContent().isBlank() || request.getCategoryId() == null)

            return ServiceResponse.error("Naslov, sadrzaj i kategorija su obavezni", 400);

        News existing = newsRepository.findById(newsId);

        if (existing == null)
            return ServiceResponse.error("Vest nije pronadjena", 404);

        boolean isAdmin = "ADMIN".equals(currentUser.getType());
        boolean isAuthor = existing.getAuthor().getId().equals(currentUser.getId());

        if (!isAdmin && !isAuthor)
            return ServiceResponse.error("Nemate dozvolu da menjate ovu vest", 403);

        Category category = categoryRepository.findById(request.getCategoryId());

        if (category == null)
            return ServiceResponse.error("Kategorija nije pronadjena", 404);

        existing.setTitle(request.getTitle());
        existing.setContent(request.getContent());
        existing.setCategory(category);

        newsRepository.update(existing);

        tagRepository.removeAllTagsFromNews(newsId);

        if (request.getTagNames() != null) {
            List<Tag> tags = processTags(request.getTagNames(), newsId);
            existing.setTags(tags);
        }

        return ServiceResponse.success("news", existing);
    }

    public Map<String, Object> delete(Integer newsId, User currentUser) {

        News existing = newsRepository.findById(newsId);

        if (existing == null)
            return ServiceResponse.error("Vest nije pronadjena", 404);

        boolean isAdmin = "ADMIN".equals(currentUser.getType());
        boolean isAuthor = existing.getAuthor().getId().equals(currentUser.getId());

        if (!isAdmin && !isAuthor)
            return ServiceResponse.error("Nemate dozvolu da obrisete ovu vest", 403);

        newsRepository.delete(newsId);
        return ServiceResponse.success("success", true);
    }

    public List<News> getLatest(int limit) {

        List<News> newsList = newsRepository.findLatest(limit);

        newsList.forEach(n -> n.setTags(tagRepository.findByNewsId(n.getId())));

        return newsList;
    }

    public List<News> getMostVisited(int limit, int days) {

        List<News> newsList = newsRepository.findMostVisited(limit, days);

        newsList.forEach(n -> n.setTags(tagRepository.findByNewsId(n.getId())));

        return newsList;
    }

    public Map<String, Object> search(String query, int page, int pageSize) {

        if (query == null || query.trim().isEmpty()) {

            Map<String, Object> empty = new HashMap<>();

            empty.put("items", new ArrayList<>());
            empty.put("total", 0);

            return empty;
        }

        List<News> newsList = newsRepository.search(query.trim(), page, pageSize);

        newsList.forEach(n -> n.setTags(tagRepository.findByNewsId(n.getId())));

        return buildPageResponse(newsList, newsRepository.countSearch(query.trim()), page, pageSize);
    }

    public Map<String, Object> getByTag(Integer tagId, int page, int pageSize) {

        List<News> newsList = newsRepository.findByTag(tagId, page, pageSize);

        newsList.forEach(n -> n.setTags(tagRepository.findByNewsId(n.getId())));

        return buildPageResponse(newsList, newsRepository.countByTag(tagId), page, pageSize);
    }

    public List<News> getMostReacted(int limit) {
        return newsRepository.findMostReacted(limit);
    }

    public Map<String, Object> addComment(Integer newsId, CreateCommentRequest request) {

        if (request.getAuthorName() == null || request.getAuthorName().isBlank() ||
                request.getContent() == null || request.getContent().isBlank())

            return ServiceResponse.error("Ime autora i tekst komentara su obavezni", 400);


        if (newsRepository.findById(newsId) == null)
            return ServiceResponse.error("Vest nije pronadjena", 404);

        Comment comment = new Comment();
        comment.setAuthorName(request.getAuthorName());
        comment.setContent(request.getContent());
        comment.setNewsId(newsId);

        return ServiceResponse.success("comment", commentRepository.save(comment));
    }

    public Map<String, Object> getComments(Integer newsId, int page, int pageSize, String sessionId) {
        List<Comment> comments = commentRepository.findByNewsId(newsId, page, pageSize);

        List<Map<String, Object>> commentsWithReactions = comments.stream().map(comment -> {
            Map<String, Object> c = new HashMap<>();
            c.put("id", comment.getId());
            c.put("authorName", comment.getAuthorName());
            c.put("content", comment.getContent());
            c.put("createdAt", comment.getCreatedAt());
            c.put("likes", commentRepository.getLikes(comment.getId()));
            c.put("dislikes", commentRepository.getDislikes(comment.getId()));
            c.put("userReaction", sessionId != null ? commentRepository.getReaction(comment.getId(), sessionId) : null);
            return c;
        }).collect(Collectors.toList());

        int total = commentRepository.countByNewsId(newsId);
        Map<String, Object> response = new HashMap<>();
        response.put("items", commentsWithReactions);
        response.put("totalPages", (int) Math.ceil((double) total / pageSize));
        return response;
    }

    public Map<String, Object> reactToNews(Integer newsId, String sessionId, ReactionRequest request) {

        if (sessionId == null || sessionId.isBlank())
            return ServiceResponse.error("Session ID je obavezan", 400);

        if (request.getReaction() == null || request.getReaction().isBlank())
            return ServiceResponse.error("Reakcija je obavezna", 400);

        if (!request.getReaction().equals("LIKE") && !request.getReaction().equals("DISLIKE"))
            return ServiceResponse.error("Reakcija mora biti LIKE ili DISLIKE", 400);

        if (newsRepository.findById(newsId) == null)
            return ServiceResponse.error("Vest nije pronadjena", 404);

        String existing = newsRepository.getReaction(newsId, sessionId);

        if (existing == null)
            newsRepository.saveReaction(newsId, sessionId, request.getReaction());
        else if (existing.equals(request.getReaction()))
            newsRepository.deleteReaction(newsId, sessionId);
        else
            newsRepository.updateReaction(newsId, sessionId, request.getReaction());

        Map<String, Object> response = new HashMap<>();

        response.put("likes", newsRepository.countLikes(newsId));
        response.put("dislikes", newsRepository.countDislikes(newsId));
        response.put("userReaction", newsRepository.getReaction(newsId, sessionId));
        return response;
    }

    public Map<String, Object> reactToComment(Integer commentId, String sessionId, ReactionRequest request) {

        if (sessionId == null || sessionId.isBlank())
            return ServiceResponse.error("Session ID je obavezan", 400);

        if (request.getReaction() == null || request.getReaction().isBlank())
            return ServiceResponse.error("Reakcija je obavezna", 400);

        if (!request.getReaction().equals("LIKE") && !request.getReaction().equals("DISLIKE"))
            return ServiceResponse.error("Reakcija mora biti LIKE ili DISLIKE", 400);

        String existing = commentRepository.getReaction(commentId, sessionId);

        if (existing == null)
            commentRepository.saveReaction(commentId, sessionId, request.getReaction());
        else if (existing.equals(request.getReaction()))
            commentRepository.deleteReaction(commentId, sessionId);
        else
            commentRepository.updateReaction(commentId, sessionId, request.getReaction());

        Map<String, Object> response = new HashMap<>();
        response.put("likes", commentRepository.getLikes(commentId));
        response.put("dislikes", commentRepository.getDislikes(commentId));
        response.put("userReaction", commentRepository.getReaction(commentId, sessionId));
        return response;
    }

    private List<Tag> processTags(List<String> tagNames, Integer newsId) {

        List<Tag> tags = new ArrayList<>();

        for (String tagName : tagNames) {

            if (tagName == null || tagName.trim().isEmpty()) continue;

            String name = tagName.trim().toLowerCase();

            Tag tag = tagRepository.findByName(name);

            if (tag == null)
                tag = tagRepository.save(new Tag(null, name));

            tagRepository.addTagToNews(newsId, tag.getId());

            tags.add(tag);
        }
        return tags;
    }

    private Map<String, Object> buildPageResponse(List<?> items, int total, int page, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        return result;
    }
}