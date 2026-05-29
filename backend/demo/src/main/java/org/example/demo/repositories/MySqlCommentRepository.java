package org.example.demo.repositories;

import org.example.demo.entities.Comment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlCommentRepository extends MySqlAbstractRepository implements CommentRepository {

    private Comment mapRow(ResultSet rs) throws SQLException {
        return new Comment(
                rs.getInt("id"),
                rs.getString("author_name"),
                rs.getString("content"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getInt("news_id"),
                rs.getInt("likes"),
                rs.getInt("dislikes")
        );
    }

    @Override
    public List<Comment> findByNewsId(Integer newsId, int page, int pageSize) {

        List<Comment> comments = new ArrayList<>();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            connection = this.newConnection();

            ps = connection.prepareStatement("SELECT * FROM comments WHERE news_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?");
            ps.setInt(1, newsId);
            ps.setInt(2, pageSize);
            ps.setInt(3, (page - 1) * pageSize);
            rs = ps.executeQuery();

            while (rs.next()) {
                comments.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }

        return comments;
    }

    @Override
    public int countByNewsId(Integer newsId) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();

            ps = connection.prepareStatement("SELECT COUNT(*) FROM comments WHERE news_id = ?");
            ps.setInt(1, newsId);
            rs = ps.executeQuery();

            if (rs.next())
                return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }

        return 0;
    }

    @Override
    public Comment save(Comment comment) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            connection = this.newConnection();

            String[] generatedCols = {"id"};

            ps = connection.prepareStatement("INSERT INTO comments (author_name, content, created_at, news_id, likes, dislikes) VALUES (?,?,NOW(),?,0,0)",generatedCols);

            ps.setString(1, comment.getAuthorName());
            ps.setString(2, comment.getContent());
            ps.setInt(3, comment.getNewsId());

            ps.executeUpdate();
            rs = ps.getGeneratedKeys();

            if (rs.next()) {
                comment.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }

        return comment;
    }

    @Override
    public String getReaction(Integer commentId, String sessionId) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            connection = this.newConnection();

            ps = connection.prepareStatement("SELECT reaction FROM comment_reactions WHERE comment_id=? AND session_id=?");
            ps.setInt(1, commentId);
            ps.setString(2, sessionId);

            rs = ps.executeQuery();

            if (rs.next())
                return rs.getString("reaction");

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
    public void saveReaction(Integer commentId, String sessionId, String reaction) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {

            connection = this.newConnection();

            ps = connection.prepareStatement("INSERT INTO comment_reactions (comment_id, session_id, reaction) VALUES (?,?,?)");

            ps.setInt(1, commentId);
            ps.setString(2, sessionId);
            ps.setString(3, reaction);

            ps.executeUpdate();

            updateCommentCounts(commentId, connection);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(ps);
            closeConnection(connection);
        }

    }

    @Override
    public void updateReaction(Integer commentId, String sessionId, String reaction) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {

            connection = this.newConnection();

            ps = connection.prepareStatement("UPDATE comment_reactions SET reaction=? WHERE comment_id=? AND session_id=?");

            ps.setString(1, reaction);
            ps.setInt(2, commentId);
            ps.setString(3, sessionId);

            ps.executeUpdate();

            updateCommentCounts(commentId, connection);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(ps);
            closeConnection(connection);
        }

    }

    @Override
    public void deleteReaction(Integer commentId, String sessionId) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = this.newConnection();

            ps = connection.prepareStatement("DELETE FROM comment_reactions WHERE comment_id=? AND session_id=?");
            ps.setInt(1, commentId);
            ps.setString(2, sessionId);

            ps.executeUpdate();

            updateCommentCounts(commentId, connection);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(ps);
            closeConnection(connection);
        }

    }

    @Override
    public int getLikes(Integer commentId) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();
            ps = connection.prepareStatement("SELECT likes FROM comments WHERE id = ?");
            ps.setInt(1, commentId);
            rs = ps.executeQuery();

            if (rs.next())
                return rs.getInt("likes");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }

        return 0;
    }

    @Override
    public int getDislikes(Integer commentId) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();
            ps = connection.prepareStatement("SELECT dislikes FROM comments WHERE id = ?");
            ps.setInt(1, commentId);
            rs = ps.executeQuery();

            if (rs.next())
                return rs.getInt("dislikes");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }

        return 0;
    }

    private void updateCommentCounts(Integer commentId, Connection connection) throws SQLException {

        PreparedStatement ps = null;
        try {

            ps = connection.prepareStatement(
                    "UPDATE comments SET " +
                            "likes = (SELECT COUNT(*) FROM comment_reactions WHERE comment_id=? AND reaction='LIKE'), " +
                            "dislikes = (SELECT COUNT(*) FROM comment_reactions WHERE comment_id=? AND reaction='DISLIKE') " +
                            "WHERE id=?"
            );

            ps.setInt(1, commentId);
            ps.setInt(2, commentId);
            ps.setInt(3, commentId);
            ps.executeUpdate();

        } finally {
            closeStatement(ps);
        }

    }
}

