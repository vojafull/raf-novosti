package org.example.demo.repositories;

import org.example.demo.entities.Category;
import org.example.demo.entities.News;
import org.example.demo.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlNewsRepository extends MySqlAbstractRepository implements NewsRepository {
    private News mapRow(ResultSet rs) throws SQLException {
        User author = new User(
                rs.getInt("author_id"),
                rs.getString("author_email"),
                rs.getString("author_first_name"),
                rs.getString("author_last_name"),
                rs.getString("author_type"),
                rs.getString("author_status"),
                null
        );

        Category category = new Category(
                rs.getInt("category_id"),
                rs.getString("category_name"),
                rs.getString("category_description")
        );

        News news = new News(
                rs.getInt("n.id"),
                rs.getString("n.title"),
                rs.getString("n.content"),
                rs.getTimestamp("n.created_at").toLocalDateTime(),
                rs.getInt("n.visit_count"),
                0, 0,
                author,
                category
        );
        return news;
    }

    private static final String BASE_SELECT =
            "SELECT n.id, n.title, n.content, n.created_at, n.visit_count, " +
                    "u.id as author_id, u.email as author_email, " +
                    "u.first_name as author_first_name, u.last_name as author_last_name, " +
                    "u.type as author_type, u.status as author_status, " +
                    "c.id as category_id, c.name as category_name, c.description as category_description " +
                    "FROM news n " +
                    "JOIN users u ON n.author_id = u.id " +
                    "JOIN categories c ON n.category_id = c.id ";

    @Override
    public List<News> findAll(int page, int pageSize) {

        List<News> newsList = new ArrayList<>();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            connection = this.newConnection();

            ps = connection.prepareStatement(
                    BASE_SELECT + "ORDER BY n.created_at DESC LIMIT ? OFFSET ?"
            );
            ps.setInt(1, pageSize);
            ps.setInt(2, (page - 1) * pageSize);

            rs = ps.executeQuery();

            while (rs.next()) {
                newsList.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }

        return newsList;
    }

    @Override
    public int countAll() {
        return countQuery("SELECT COUNT(*) FROM news", null);
    }

    @Override
    public List<News> findByCategory(Integer categoryId, int page, int pageSize) {

        List<News> newsList = new ArrayList<>();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();

            ps = connection.prepareStatement(BASE_SELECT + "WHERE n.category_id = ? ORDER BY n.created_at DESC LIMIT ? OFFSET ?");

            ps.setInt(1, categoryId);
            ps.setInt(2, pageSize);
            ps.setInt(3, (page - 1) * pageSize);

            rs = ps.executeQuery();

            while (rs.next()) {
                newsList.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }

        return newsList;
    }

    @Override
    public int countByCategory(Integer categoryId) {
        return countQuery("SELECT COUNT(*) FROM news WHERE category_id = ?", categoryId);
    }

    @Override
    public List<News> findLatest(int limit) {

        List<News> newsList = new ArrayList<>();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            connection = this.newConnection();

            ps = connection.prepareStatement(BASE_SELECT + "ORDER BY n.created_at DESC LIMIT ?");
            ps.setInt(1, limit);
            rs = ps.executeQuery();

            while (rs.next()) {
                newsList.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }

        return newsList;
    }

    @Override
    public List<News> findMostVisited(int limit, int days) {

        List<News> newsList = new ArrayList<>();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            connection = this.newConnection();

            ps = connection.prepareStatement(
                    BASE_SELECT +
                            "WHERE n.created_at >= DATE_SUB(NOW(), INTERVAL ? DAY) " +
                            "ORDER BY n.visit_count DESC LIMIT ?"
            );

            ps.setInt(1, days);
            ps.setInt(2, limit);

            rs = ps.executeQuery();

            while (rs.next()) {
                newsList.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }

        return newsList;
    }

    @Override
    public List<News> search(String query, int page, int pageSize) {

        List<News> newsList = new ArrayList<>();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();

            String like = "%" + query.toLowerCase() + "%";
            ps = connection.prepareStatement(
                    BASE_SELECT +
                            "JOIN news_tags nt on n.id=nt.news_id " +
                            "JOIN tags t on t.id = nt.tag_id " +
                            "WHERE n.title LIKE ? OR n.content LIKE ? " +
                            "ORDER BY n.created_at DESC LIMIT ? OFFSET ?"
            );

            ps.setString(1, like);
            ps.setString(2, like);
            ps.setInt(3, pageSize);
            ps.setInt(4, (page - 1) * pageSize);

            rs = ps.executeQuery();

            while (rs.next()) {
                newsList.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }

        return newsList;
    }

    @Override
    public int countSearch(String query) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            connection = this.newConnection();

            String like = "%" + query.toLowerCase() + "%";

            ps = connection.prepareStatement(
                    "SELECT COUNT(*) FROM news WHERE LOWER(title) LIKE ? OR LOWER(content) LIKE ?"
            );
            ps.setString(1, like);
            ps.setString(2, like);
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
    public List<News> findByTag(Integer tagId, int page, int pageSize) {

        List<News> newsList = new ArrayList<>();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();

            ps = connection.prepareStatement(
                    BASE_SELECT +
                            "JOIN news_tags nt ON n.id = nt.news_id " +
                            "WHERE nt.tag_id = ? ORDER BY n.created_at DESC LIMIT ? OFFSET ?"
            );
            ps.setInt(1, tagId);
            ps.setInt(2, pageSize);
            ps.setInt(3, (page - 1) * pageSize);

            rs = ps.executeQuery();

            while (rs.next()) {
                newsList.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }

        return newsList;
    }

    @Override
    public int countByTag(Integer tagId) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            connection = this.newConnection();

            ps = connection.prepareStatement("SELECT COUNT(*) FROM news n JOIN news_tags nt ON n.id = nt.news_id WHERE nt.tag_id = ?");
            ps.setInt(1, tagId);
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
    public News findById(Integer id) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();

            ps = connection.prepareStatement(BASE_SELECT + "WHERE n.id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs); closeStatement(ps); closeConnection(connection);
        }

        return null;
    }

    @Override
    public News save(News news) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();

            String[] generatedCols = {"id"};

            ps = connection.prepareStatement("INSERT INTO news (title, content, created_at, visit_count, author_id, category_id) VALUES (?,?,NOW(),0,?,?)", generatedCols);
            ps.setString(1, news.getTitle());
            ps.setString(2, news.getContent());
            ps.setInt(3, news.getAuthor().getId());
            ps.setInt(4, news.getCategory().getId());

            ps.executeUpdate();
            rs = ps.getGeneratedKeys();

            if (rs.next()) {
                news.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }

        return news;
    }

    @Override
    public News update(News news) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {

            connection = this.newConnection();

            ps = connection.prepareStatement("UPDATE news SET title=?, content=?, category_id=? WHERE id=?");
            ps.setString(1, news.getTitle());
            ps.setString(2, news.getContent());
            ps.setInt(3, news.getCategory().getId());
            ps.setInt(4, news.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(ps);
            closeConnection(connection);
        }

        return news;
    }

    @Override
    public void delete(Integer id) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = this.newConnection();

            ps = connection.prepareStatement("DELETE FROM news WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(ps);
            closeConnection(connection);
        }

    }

    @Override
    public boolean hasVisited(Integer newsId, String sessionId) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            connection = this.newConnection();

            ps = connection.prepareStatement("SELECT COUNT(*) FROM news_visits WHERE news_id=? AND session_id=?");
            ps.setInt(1, newsId);
            ps.setString(2, sessionId);

            rs = ps.executeQuery();

            if (rs.next())
                return rs.getInt(1) > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs); closeStatement(ps); closeConnection(connection);
        }

        return false;
    }

    @Override
    public void recordVisit(Integer newsId, String sessionId) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = this.newConnection();

            ps = connection.prepareStatement("INSERT IGNORE INTO news_visits (news_id, session_id) VALUES (?,?)");
            ps.setInt(1, newsId);
            ps.setString(2, sessionId);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(ps);
            closeConnection(connection);
        }

    }

    @Override
    public void incrementVisitCount(Integer newsId) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = this.newConnection();

            ps = connection.prepareStatement("UPDATE news SET visit_count = visit_count + 1 WHERE id=?");
            ps.setInt(1, newsId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(ps);
            closeConnection(connection);
        }

    }

    @Override
    public String getReaction(Integer newsId, String sessionId) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();

            ps = connection.prepareStatement("SELECT reaction FROM news_reactions WHERE news_id=? AND session_id=?");
            ps.setInt(1, newsId);
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
    public void saveReaction(Integer newsId, String sessionId, String reaction) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {

            connection = this.newConnection();

            ps = connection.prepareStatement("INSERT INTO news_reactions (news_id, session_id, reaction) VALUES (?,?,?)");

            ps.setInt(1, newsId);
            ps.setString(2, sessionId);
            ps.setString(3, reaction);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(ps);
            closeConnection(connection);
        }


    }

    @Override
    public void updateReaction(Integer newsId, String sessionId, String reaction) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = this.newConnection();

            ps = connection.prepareStatement("UPDATE news_reactions SET reaction=? WHERE news_id=? AND session_id=?");

            ps.setString(1, reaction);
            ps.setInt(2, newsId);
            ps.setString(3, sessionId);

            ps.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(ps);
            closeConnection(connection);
        }


    }

    @Override
    public void deleteReaction(Integer newsId, String sessionId) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = this.newConnection();

            ps = connection.prepareStatement("DELETE FROM news_reactions WHERE news_id=? AND session_id=?");
            ps.setInt(1, newsId);
            ps.setString(2, sessionId);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(ps);
            closeConnection(connection);
        }
    }

    @Override
    public int countLikes(Integer newsId) {
        return countReactions(newsId, "LIKE");
    }

    @Override
    public int countDislikes(Integer newsId) {
        return countReactions(newsId, "DISLIKE");
    }

    private int countReactions(Integer newsId, String type) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            connection = this.newConnection();

            ps = connection.prepareStatement("SELECT COUNT(*) FROM news_reactions WHERE news_id=? AND reaction=?");

            ps.setInt(1, newsId);
            ps.setString(2, type);

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
    public List<News> findRelated(Integer newsId, int limit) {

        List<News> newsList = new ArrayList<>();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();
            ps = connection.prepareStatement(
                    BASE_SELECT +
                            "JOIN news_tags nt ON n.id = nt.news_id " +
                            "WHERE nt.tag_id IN (SELECT tag_id FROM news_tags WHERE news_id = ?) " +
                            "AND n.id != ? " +
                            "GROUP BY n.id ORDER BY n.created_at DESC LIMIT ?"
            );
            ps.setInt(1, newsId);
            ps.setInt(2, newsId);
            ps.setInt(3, limit);

            rs = ps.executeQuery();

            while (rs.next()) {
                newsList.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs); closeStatement(ps); closeConnection(connection);
        }
        return newsList;
    }

    @Override
    public List<News> findMostReacted(int limit) {
        List<News> newsList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = this.newConnection();
            ps = connection.prepareStatement(
                    "SELECT n.id, n.title, n.content, n.created_at, n.visit_count, " +
                            "u.id as author_id, u.email as author_email, " +
                            "u.first_name as author_first_name, u.last_name as author_last_name, " +
                            "u.type as author_type, u.status as author_status, " +
                            "c.id as category_id, c.name as category_name, c.description as category_description, " +
                            "SUM(CASE WHEN nr.reaction = 'LIKE' THEN 1 ELSE 0 END) as likes, " +
                            "SUM(CASE WHEN nr.reaction = 'DISLIKE' THEN 1 ELSE 0 END) as dislikes " +
                            "FROM news n " +
                            "JOIN users u ON n.author_id = u.id " +
                            "JOIN categories c ON n.category_id = c.id " +
                            "LEFT JOIN news_reactions nr ON n.id = nr.news_id " +
                            "GROUP BY n.id, u.id, c.id " +
                            "ORDER BY (SUM(CASE WHEN nr.reaction = 'LIKE' THEN 1 ELSE 0 END) + SUM(CASE WHEN nr.reaction = 'DISLIKE' THEN 1 ELSE 0 END)) DESC LIMIT ?"
            );
            ps.setInt(1, limit);
            rs = ps.executeQuery();
            while (rs.next()) {
                User author = new User(
                        rs.getInt("author_id"),
                        rs.getString("author_email"),
                        rs.getString("author_first_name"),
                        rs.getString("author_last_name"),
                        rs.getString("author_type"),
                        rs.getString("author_status"),
                        null
                );
                Category category = new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("category_description")
                );
                News news = new News(
                        rs.getInt("n.id"),
                        rs.getString("n.title"),
                        rs.getString("n.content"),
                        rs.getTimestamp("n.created_at").toLocalDateTime(),
                        rs.getInt("n.visit_count"),
                        rs.getInt("likes"),
                        rs.getInt("dislikes"),
                        author,
                        category
                );
                newsList.add(news);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs); closeStatement(ps); closeConnection(connection);
        }
        return newsList;
    }

    private int countQuery(String sql, Integer param) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();

            if (param != null) {
                ps = connection.prepareStatement(sql);
                ps.setInt(1, param);
            } else {
                ps = connection.prepareStatement(sql);
            }

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
}

