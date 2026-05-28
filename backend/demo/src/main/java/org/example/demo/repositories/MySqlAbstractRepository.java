package org.example.demo.repositories;
import java.sql.*;
import java.util.Optional;

public abstract class MySqlAbstractRepository {
    public MySqlAbstractRepository() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected Connection newConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://" + this.getHost() + ":" + this.getPort()
                        + "/" + this.getDatabaseName()
                        + "?serverTimezone=UTC",
                this.getUsername(),
                this.getPassword()
        );
    }

    protected String getHost()         { return "localhost"; }
    protected int    getPort()         { return 3306; }
    protected String getDatabaseName() { return "raf_novosti"; }
    protected String getUsername()     { return "root"; }
    protected String getPassword()     { return "root"; }

    protected void closeStatement(Statement statement) {
        try {
            if (statement != null) statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null) resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void closeConnection(Connection connection) {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
