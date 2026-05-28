package org.example.demo.repositories;

import org.example.demo.entities.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlCategoryRepository extends MySqlAbstractRepository implements CategoryRepository {

    private Category mapRow(ResultSet rs) throws SQLException {
        return new Category(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description")
        );
    }

    @Override
    public List<Category> findAll(int page, int pageSize) {

        List<Category> categories = new ArrayList<>();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();

            ps = connection.prepareStatement("SELECT * FROM categories ORDER BY name LIMIT ? OFFSET ?");
            ps.setInt(1, pageSize);
            ps.setInt(2, (page - 1) * pageSize);
            rs = ps.executeQuery();

            while (rs.next()) {
                categories.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }


        return categories;
    }

    @Override
    public int countAll() {

        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {

            connection = this.newConnection();

            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT COUNT(*) FROM categories");

            if (rs.next())
                return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
            closeConnection(connection);
        }


        return 0;
    }

    @Override
    public Category findById(Integer id) {

        Category category = null;

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();

            ps = connection.prepareStatement("SELECT * FROM categories WHERE id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                category = mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }

        return category;
    }

    @Override
    public Category save(Category category) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();

            String[] generatedCols = {"id"};

            ps = connection.prepareStatement("INSERT INTO categories (name, description) VALUES (?, ?)", generatedCols);
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();

            if (rs.next()) {
                category.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }

        return category;
    }

    @Override
    public Category update(Category category) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {

            connection = this.newConnection();

            ps = connection.prepareStatement("UPDATE categories SET name=?, description=? WHERE id=?");
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setInt(3, category.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(ps);
            closeConnection(connection);
        }

        return category;
    }

    @Override
    public void delete(Integer id) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {

            connection = this.newConnection();

            ps = connection.prepareStatement("DELETE FROM categories WHERE id=?");
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
    public boolean existsByName(String name) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();

            ps = connection.prepareStatement("SELECT COUNT(*) FROM categories WHERE name = ?");
            ps.setString(1, name);
            rs = ps.executeQuery();

            if (rs.next())
                return rs.getInt(1) > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }

        return false;
    }

    @Override
    public boolean hasNews(Integer categoryId) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            connection = this.newConnection();

            ps = connection.prepareStatement("SELECT COUNT(*) FROM news WHERE category_id = ?");
            ps.setInt(1, categoryId);
            rs = ps.executeQuery();

            if (rs.next())
                return rs.getInt(1) > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }

        return false;
    }
}
