package dao.impl;

import dao.ServiceDAO;
import dao.DAOException;
import dao.factory.DatabaseFactory;
import models.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC Implementation of ServiceDAO
 */
public class ServiceDAOImpl implements ServiceDAO {

    @Override
    public int create(Service service) throws DAOException {
        String sql = "INSERT INTO services (name, description, estimated_time, active) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, service.getName());
            pstmt.setString(2, service.getDescription());
            pstmt.setInt(3, service.getEstimatedTime());
            pstmt.setBoolean(4, service.isActive());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DAOException("Creating service failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new DAOException("Creating service failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error creating service: " + e.getMessage(), e);
        }
    }

    @Override
    public int save(Service service) throws DAOException {
        if (service.getId() > 0) {
            update(service);
            return service.getId();
        } else {
            return create(service);
        }
    }

    @Override
    public Service findById(int id) throws DAOException {
        String sql = "SELECT * FROM services WHERE id = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractService(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding service by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Service findByName(String name) throws DAOException {
        String sql = "SELECT * FROM services WHERE name = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractService(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding service by name: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Service service) throws DAOException {
        String sql = "UPDATE services SET name = ?, description = ?, estimated_time = ?, active = ? WHERE id = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, service.getName());
            pstmt.setString(2, service.getDescription());
            pstmt.setInt(3, service.getEstimatedTime());
            pstmt.setBoolean(4, service.isActive());
            pstmt.setInt(5, service.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating service: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) throws DAOException {
        String sql = "DELETE FROM services WHERE id = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting service: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Service> findAll() throws DAOException {
        String sql = "SELECT * FROM services ORDER BY name";
        List<Service> services = new ArrayList<>();

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                services.add(extractService(rs));
            }
            return services;
        } catch (SQLException e) {
            throw new DAOException("Error finding all services: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Service> findAllActive() throws DAOException {
        String sql = "SELECT * FROM services WHERE active = TRUE ORDER BY name";
        List<Service> services = new ArrayList<>();

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                services.add(extractService(rs));
            }
            return services;
        } catch (SQLException e) {
            throw new DAOException("Error finding active services: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean setActive(int id, boolean active) throws DAOException {
        String sql = "UPDATE services SET active = ? WHERE id = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, active);
            pstmt.setInt(2, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error setting service active status: " + e.getMessage(), e);
        }
    }

    /**
     * Extract a Service object from ResultSet
     */
    private Service extractService(ResultSet rs) throws SQLException {
        Service service = new Service();
        service.setId(rs.getInt("id"));
        service.setName(rs.getString("name"));
        service.setDescription(rs.getString("description"));
        service.setEstimatedTime(rs.getInt("estimated_time"));
        service.setActive(rs.getBoolean("active"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            service.setCreatedAt(createdAt.toLocalDateTime());
        }

        return service;
    }
}
