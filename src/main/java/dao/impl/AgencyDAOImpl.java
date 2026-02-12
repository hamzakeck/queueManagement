package dao.impl;

import dao.AgencyDAO;
import dao.DAOException;
import dao.factory.DatabaseFactory;
import models.Agency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC Implementation of AgencyDAO
 */
public class AgencyDAOImpl implements AgencyDAO {

    @Override
    public int create(Agency agency) throws DAOException {
        String sql = "INSERT INTO agencies (name, address, city, phone, total_counters) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, agency.getName());
            pstmt.setString(2, agency.getAddress());
            pstmt.setString(3, agency.getCity());
            pstmt.setString(4, agency.getPhone());
            pstmt.setInt(5, agency.getTotalCounters());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DAOException("Creating agency failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new DAOException("Creating agency failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error creating agency: " + e.getMessage(), e);
        }
    }

    @Override
    public int save(Agency agency) throws DAOException {
        if (agency.getId() > 0) {
            update(agency);
            return agency.getId();
        } else {
            return create(agency);
        }
    }

    @Override
    public Agency findById(int id) throws DAOException {
        String sql = "SELECT id, name, address, city, phone, total_counters, created_at FROM agencies WHERE id = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractAgency(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding agency by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Agency findByName(String name) throws DAOException {
        String sql = "SELECT id, name, address, city, phone, total_counters, created_at FROM agencies WHERE name = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractAgency(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding agency by name: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Agency agency) throws DAOException {
        String sql = "UPDATE agencies SET name = ?, address = ?, city = ?, phone = ?, total_counters = ? WHERE id = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, agency.getName());
            pstmt.setString(2, agency.getAddress());
            pstmt.setString(3, agency.getCity());
            pstmt.setString(4, agency.getPhone());
            pstmt.setInt(5, agency.getTotalCounters());
            pstmt.setInt(6, agency.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating agency: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) throws DAOException {
        String sql = "DELETE FROM agencies WHERE id = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting agency: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Agency> findAll() throws DAOException {
        String sql = "SELECT id, name, address, city, phone, total_counters, created_at FROM agencies ORDER BY city, name";
        List<Agency> agencies = new ArrayList<>();

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                agencies.add(extractAgency(rs));
            }
            return agencies;
        } catch (SQLException e) {
            throw new DAOException("Error finding all agencies: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Agency> findByCity(String city) throws DAOException {
        String sql = "SELECT id, name, address, city, phone, total_counters, created_at FROM agencies WHERE city = ? ORDER BY name";
        List<Agency> agencies = new ArrayList<>();

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, city);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    agencies.add(extractAgency(rs));
                }
                return agencies;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding agencies by city: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> getAllCities() throws DAOException {
        String sql = "SELECT DISTINCT city FROM agencies ORDER BY city";
        List<String> cities = new ArrayList<>();

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                cities.add(rs.getString("city"));
            }
            return cities;
        } catch (SQLException e) {
            throw new DAOException("Error finding all cities: " + e.getMessage(), e);
        }
    }

    /**
     * Extract an Agency object from ResultSet
     */
    private Agency extractAgency(ResultSet rs) throws SQLException {
        Agency agency = new Agency();
        agency.setId(rs.getInt("id"));
        agency.setName(rs.getString("name"));
        agency.setAddress(rs.getString("address"));
        agency.setCity(rs.getString("city"));
        agency.setPhone(rs.getString("phone"));
        agency.setTotalCounters(rs.getInt("total_counters"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            agency.setCreatedAt(createdAt.toLocalDateTime());
        }

        return agency;
    }
}
