package dao.impl;

import dao.EmployeeDAO;
import dao.DAOException;
import dao.factory.DatabaseFactory;
import models.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC Implementation of EmployeeDAO
 */
public class EmployeeDAOImpl implements EmployeeDAO {

    @Override
    public int create(Employee employee) throws DAOException {
        String sql = "INSERT INTO employees (first_name, last_name, email, password, agency_id, counter_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseFactory.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getLastName());
            pstmt.setString(3, employee.getEmail());
            pstmt.setString(4, employee.getPassword());
            pstmt.setInt(5, employee.getAgencyId());
            
            if (employee.getCounterId() > 0) {
                pstmt.setInt(6, employee.getCounterId());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("Creating employee failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new DAOException("Creating employee failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error creating employee: " + e.getMessage(), e);
        }
    }

    @Override
    public Employee findById(int id) throws DAOException {
        String sql = "SELECT * FROM employees WHERE id = ?";
        
        try (Connection conn = DatabaseFactory.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractEmployee(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding employee by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Employee findByEmail(String email) throws DAOException {
        String sql = "SELECT * FROM employees WHERE email = ?";
        
        try (Connection conn = DatabaseFactory.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractEmployee(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding employee by email: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Employee employee) throws DAOException {
        String sql = "UPDATE employees SET first_name = ?, last_name = ?, email = ?, password = ?, agency_id = ?, counter_id = ? WHERE id = ?";
        
        try (Connection conn = DatabaseFactory.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getLastName());
            pstmt.setString(3, employee.getEmail());
            pstmt.setString(4, employee.getPassword());
            pstmt.setInt(5, employee.getAgencyId());
            
            if (employee.getCounterId() > 0) {
                pstmt.setInt(6, employee.getCounterId());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }
            
            pstmt.setInt(7, employee.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating employee: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) throws DAOException {
        String sql = "DELETE FROM employees WHERE id = ?";
        
        try (Connection conn = DatabaseFactory.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting employee: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employee> findAll() throws DAOException {
        String sql = "SELECT * FROM employees ORDER BY agency_id, counter_id";
        List<Employee> employees = new ArrayList<>();
        
        try (Connection conn = DatabaseFactory.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                employees.add(extractEmployee(rs));
            }
            return employees;
        } catch (SQLException e) {
            throw new DAOException("Error finding all employees: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employee> findByAgency(int agencyId) throws DAOException {
        String sql = "SELECT * FROM employees WHERE agency_id = ? ORDER BY counter_id";
        List<Employee> employees = new ArrayList<>();
        
        try (Connection conn = DatabaseFactory.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, agencyId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(extractEmployee(rs));
                }
                return employees;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding employees by agency: " + e.getMessage(), e);
        }
    }

    @Override
    public Employee findByAgencyAndCounter(int agencyId, int counterId) throws DAOException {
        String sql = "SELECT * FROM employees WHERE agency_id = ? AND counter_id = ?";
        
        try (Connection conn = DatabaseFactory.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, agencyId);
            pstmt.setInt(2, counterId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractEmployee(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding employee by agency and counter: " + e.getMessage(), e);
        }
    }

    @Override
    public Employee authenticate(String email, String password) throws DAOException {
        String sql = "SELECT * FROM employees WHERE email = ? AND password = ?";
        
        try (Connection conn = DatabaseFactory.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractEmployee(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error authenticating employee: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean emailExists(String email) throws DAOException {
        String sql = "SELECT COUNT(*) FROM employees WHERE email = ?";
        
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
     * Extract an Employee object from ResultSet
     */
    private Employee extractEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setId(rs.getInt("id"));
        employee.setFirstName(rs.getString("first_name"));
        employee.setLastName(rs.getString("last_name"));
        employee.setEmail(rs.getString("email"));
        employee.setPassword(rs.getString("password"));
        employee.setAgencyId(rs.getInt("agency_id"));
        employee.setCounterId(rs.getInt("counter_id"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            employee.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return employee;
    }
}
