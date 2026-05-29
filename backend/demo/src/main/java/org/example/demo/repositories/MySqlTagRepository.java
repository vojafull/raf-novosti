package org.example.demo.repositories;

import org.example.demo.entities.Tag;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlTagRepository extends MySqlAbstractRepository implements TagRepository {

    private Tag mapRow(ResultSet rs) throws SQLException{
        return new Tag(
                rs.getInt("id"),
                rs.getString("name")
        );
    }

    @Override
    public List<Tag> findByNewsId(Integer newsId) {

        List<Tag> tags = new ArrayList<>();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            connection = this.newConnection();

            ps = connection.prepareStatement(
                    "SELECT t.id, t.name FROM tags t " +
                            "JOIN news_tags nt ON t.id = nt.tag_id " +
                            "WHERE nt.news_id = ?"
            );

            ps.setInt(1, newsId);
            rs = ps.executeQuery();

            while (rs.next()) {
                tags.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }

        return tags;
    }

    @Override
    public Tag findByName(String name) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();

            ps = connection.prepareStatement("SELECT * FROM tags WHERE name = ?");
            ps.setString(1, name);
            rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }

        return null;
    }

    @Override
    public Tag findById(Integer id) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();

            ps = connection.prepareStatement("SELECT * FROM tags WHERE id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }


        return null;
    }

    @Override
    public Tag save(Tag tag) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            connection = this.newConnection();

            String[] generatedCols = {"id"};

            ps = connection.prepareStatement("INSERT INTO tags (name) VALUES (?)", generatedCols);

            ps.setString(1, tag.getName());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();

            if (rs.next()) {
                tag.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }

        return tag;
    }

    @Override
    public void addTagToNews(Integer newsId, Integer tagId) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {

            connection = this.newConnection();

            ps = connection.prepareStatement("INSERT IGNORE INTO news_tags (news_id, tag_id) VALUES (?,?)");
            ps.setInt(1, newsId);
            ps.setInt(2, tagId);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(ps);
            closeConnection(connection);
        }

    }

    @Override
    public void removeAllTagsFromNews(Integer newsId) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {

            connection = this.newConnection();

            ps = connection.prepareStatement("DELETE FROM news_tags WHERE news_id = ?");
            ps.setInt(1, newsId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(ps);
            closeConnection(connection);
        }


    }
}

