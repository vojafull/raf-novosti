package org.example.demo.repositories;

import org.example.demo.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class MySqlUserRepository extends MySqlAbstractRepository implements UserRepository {
    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("type"),
                rs.getString("status"),
                rs.getString("password")
        );
    }

    @Override
    public User findByEmail(String email) {

        User user = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();

            ps = connection.prepareStatement("SELECT * FROM users WHERE email = ?");

            ps.setString(1, email);
            rs = ps.executeQuery();

            if (rs.next()) {
                user = mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }


        return user;
    }

    @Override
    public User findById(Integer id) {

        User user = null;

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            connection = this.newConnection();

            ps = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                user = mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }

        return user;
    }

    @Override
    public List<User> findAll(int page, int pageSize) {

        List<User> users = new ArrayList<>();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = this.newConnection();

            ps = connection.prepareStatement("SELECT * FROM users LIMIT ? OFFSET ?");

            ps.setInt(1, pageSize);
            ps.setInt(2, (page - 1) * pageSize);
            rs = ps.executeQuery();

            while (rs.next()) {
                users.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }

        return users;
    }

    @Override
    public int countAll() {

        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {

            connection = this.newConnection();

            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT COUNT(*) FROM users");

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
    public User save(User user) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            connection = this.newConnection();

            String[] generatedCols = {"id"};

            ps = connection.prepareStatement("INSERT INTO users (email, first_name, last_name, type, status, password) VALUES (?,?,?,?,?,?)",generatedCols);

            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setString(4, user.getType());
            ps.setString(5, user.getStatus() != null ? user.getStatus() : "ACTIVE");
            ps.setString(6, user.getPassword());

            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(connection);
        }


        return user;

    }

    @Override
    public User update(User user) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {

            connection = this.newConnection();

            ps = connection.prepareStatement("UPDATE users SET email=?, first_name=?, last_name=?, type=?, status=? WHERE id=?");


            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setString(4, user.getType());
            ps.setString(5, user.getStatus());
            ps.setInt(6, user.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(ps);
            closeConnection(connection);
        }


        return user;
    }

    @Override
    public boolean existsByEmail(String email) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            connection = this.newConnection();

            ps = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE email = ?");

            ps.setString(1, email);

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







