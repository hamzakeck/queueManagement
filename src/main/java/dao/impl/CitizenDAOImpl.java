package dao.impl;

import dao.CitizenDAO;
import dao.DAOException;
import dao.factory.DatabaseFactory;
import models.Citizen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC Implementation of CitizenDAO
 */
public class CitizenDAOImpl implements CitizenDAO {

    @Override
    public int create(Citizen citizen) throws DAOException {
        String sql = "INSERT INTO citizens (first_name, last_name, email, phone, cin, password) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseFactory.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, citizen.getFirstName());
            pstmt.setString(2, citizen.getLastName());
            pstmt.setString(3, citizen.getEmail());
            pstmt.setString(4, citizen.getPhone());
            pstmt.setString(5, citizen.getCin());
            pstmt.setString(6, citizen.getPassword());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("Creating citizen failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new DAOException("Creating citizen failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error creating citizen: " + e.getMessage(), e);
        }
    }

    @Override
    public Citizen findById(int id) throws DAOException {
        String sql = "SELECT * FROM citizens WHERE id = ?";
        
        try (Connection conn = DatabaseFactory.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractCitizen(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding citizen by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Citizen findByEmail(String email) throws DAOException {
        String sql = "SELECT * FROM citizens WHERE email = ?";
        
        try (Connection conn = DatabaseFactory.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractCitizen(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding citizen by email: " + e.getMessage(), e);
        }
    }

    @Override
    public Citizen findByCin(String cin) throws DAOException {
        String sql = "SELECT * FROM citizens WHERE cin = ?";
        
        try (Connection conn = DatabaseFactory.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cin);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractCitizen(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding citizen by CIN: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Citizen citizen) throws DAOException {
        String sql = "UPDATE citizens SET first_name = ?, last_name = ?, email = ?, phone = ?, cin = ?, password = ? WHERE id = ?";
        
        try (Connection conn = DatabaseFactory.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, citizen.getFirstName());
            pstmt.setString(2, citizen.getLastName());
            pstmt.setString(3, citizen.getEmail());
            pstmt.setString(4, citizen.getPhone());
            pstmt.setString(5, citizen.getCin());
            pstmt.setString(6, citizen.getPassword());
            pstmt.setInt(7, citizen.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating citizen: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) throws DAOException {
        String sql = "DELETE FROM citizens WHERE id = ?";
        
        try (Connection conn = DatabaseFactory.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting citizen: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Citizen> findAll() throws DAOException {
        String sql = "SELECT * FROM citizens ORDER BY created_at DESC";
        List<Citizen> citizens = new ArrayList<>();
        
        try (Connection conn = DatabaseFactory.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                citizens.add(extractCitizen(rs));
            }
            return citizens;
        } catch (SQLException e) {
            throw new DAOException("Error finding all citizens: " + e.getMessage(), e);
        }
    }

    @Override
    public Citizen authenticate(String email, String password) throws DAOException {
        String sql = "SELECT * FROM citizens WHERE email = ? AND password = ?";
        
        try (Connection conn = DatabaseFactory.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractCitizen(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error authenticating citizen: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean emailExists(String email) throws DAOException {
        String sql = "SELECT COUNT(*) FROM citizens WHERE email = ?";
        
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

    @Override
    public boolean cinExists(String cin) throws DAOException {
        String sql = "SELECT COUNT(*) FROM citizens WHERE cin = ?";
        
        try (Connection conn = DatabaseFactory.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cin);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            throw new DAOException("Error checking CIN existence: " + e.getMessage(), e);
        }
    }

    /**
     * Extract a Citizen object from ResultSet
     */
    private Citizen extractCitizen(ResultSet rs) throws SQLException {
        Citizen citizen = new Citizen();
        citizen.setId(rs.getInt("id"));
        citizen.setFirstName(rs.getString("first_name"));
        citizen.setLastName(rs.getString("last_name"));
        citizen.setEmail(rs.getString("email"));
        citizen.setPhone(rs.getString("phone"));
        citizen.setCin(rs.getString("cin"));
        citizen.setPassword(rs.getString("password"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            citizen.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return citizen;
    }
}
