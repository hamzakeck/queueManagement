package dao.impl;

import dao.AdministratorDAO;
import dao.DAOException;
import dao.factory.DatabaseFactory;
import models.Administrator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC Implementation of AdministratorDAO
 */
public class AdministratorDAOImpl implements AdministratorDAO {

    @Override
    public int create(Administrator admin) throws DAOException {
        String sql = "INSERT INTO administrators (first_name, last_name, email, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, admin.getFirstName());
            pstmt.setString(2, admin.getLastName());
            pstmt.setString(3, admin.getEmail());
            pstmt.setString(4, admin.getPassword());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DAOException("Creating administrator failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new DAOException("Creating administrator failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error creating administrator: " + e.getMessage(), e);
        }
    }

    @Override
    public Administrator findById(int id) throws DAOException {
        String sql = "SELECT * FROM administrators WHERE id = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractAdministrator(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding administrator by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Administrator findByEmail(String email) throws DAOException {
        String sql = "SELECT * FROM administrators WHERE email = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractAdministrator(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding administrator by email: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Administrator admin) throws DAOException {
        String sql = "UPDATE administrators SET first_name = ?, last_name = ?, email = ?, password = ? WHERE id = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, admin.getFirstName());
            pstmt.setString(2, admin.getLastName());
            pstmt.setString(3, admin.getEmail());
            pstmt.setString(4, admin.getPassword());
            pstmt.setInt(5, admin.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating administrator: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) throws DAOException {
        String sql = "DELETE FROM administrators WHERE id = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting administrator: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Administrator> findAll() throws DAOException {
        String sql = "SELECT * FROM administrators ORDER BY created_at DESC";
        List<Administrator> administrators = new ArrayList<>();

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                administrators.add(extractAdministrator(rs));
            }
            return administrators;
        } catch (SQLException e) {
            throw new DAOException("Error finding all administrators: " + e.getMessage(), e);
        }
    }

    @Override
    public Administrator authenticate(String email, String password) throws DAOException {
        String sql = "SELECT * FROM administrators WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractAdministrator(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error authenticating administrator: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean emailExists(String email) throws DAOException {
        String sql = "SELECT COUNT(*) FROM administrators WHERE email = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            throw new DAOException("Error checking email existence: " + e.getMessage(), e);
        }
    }

    /**
     * Extract an Administrator object from ResultSet
     */
    private Administrator extractAdministrator(ResultSet rs) throws SQLException {
        Administrator admin = new Administrator();
        admin.setId(rs.getInt("id"));
        admin.setFirstName(rs.getString("first_name"));
        admin.setLastName(rs.getString("last_name"));
        admin.setEmail(rs.getString("email"));
        admin.setPassword(rs.getString("password"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            admin.setCreatedAt(createdAt.toLocalDateTime());
        }

        return admin;
    }
}
