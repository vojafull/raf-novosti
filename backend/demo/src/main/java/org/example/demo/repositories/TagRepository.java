package org.example.demo.repositories;

import org.example.demo.entities.Tag;

import java.util.List;

public interface TagRepository {
    List<Tag> findByNewsId(Integer newsId);
    Tag findByName(String name);
    Tag save(Tag tag);
    void addTagToNews(Integer newsId, Integer tagId);
    void removeAllTagsFromNews(Integer newsId);
    Tag findById(Integer id);
}
