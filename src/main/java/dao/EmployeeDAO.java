package dao;

import models.Employee;
import java.util.List;

/**
 * DAO Interface for Employee operations
 */
public interface EmployeeDAO {

    /**
     * Create a new employee
     * 
     * @param employee The employee to create
     * @return The ID of the created employee
     * @throws DAOException if database error occurs
     */
    int create(Employee employee) throws DAOException;

    /**
     * Find an employee by ID
     * 
     * @param id The employee ID
     * @return The employee or null if not found
     * @throws DAOException if database error occurs
     */
    Employee findById(int id) throws DAOException;

    /**
     * Find an employee by email
     * 
     * @param email The employee email
     * @return The employee or null if not found
     * @throws DAOException if database error occurs
     */
    Employee findByEmail(String email) throws DAOException;

    /**
     * Update an employee
     * 
     * @param employee The employee with updated information
     * @return true if update successful
     * @throws DAOException if database error occurs
     */
    boolean update(Employee employee) throws DAOException;

    /**
     * Delete an employee by ID
     * 
     * @param id The employee ID
     * @return true if deletion successful
     * @throws DAOException if database error occurs
     */
    boolean delete(int id) throws DAOException;

    /**
     * Get all employees
     * 
     * @return List of all employees
     * @throws DAOException if database error occurs
     */
    List<Employee> findAll() throws DAOException;

    /**
     * Get employees by agency ID
     * 
     * @param agencyId The agency ID
     * @return List of employees in the agency
     * @throws DAOException if database error occurs
     */
    List<Employee> findByAgency(int agencyId) throws DAOException;

    /**
     * Find employee by agency and counter
     * 
     * @param agencyId  The agency ID
     * @param counterId The counter ID
     * @return The employee or null if not found
     * @throws DAOException if database error occurs
     */
    Employee findByAgencyAndCounter(int agencyId, int counterId) throws DAOException;

    /**
     * Authenticate an employee
     * 
     * @param email    The employee email
     * @param password The employee password
     * @return The employee if authentication successful, null otherwise
     * @throws DAOException if database error occurs
     */
    Employee authenticate(String email, String password) throws DAOException;

    /**
     * Check if email already exists
     * 
     * @param email The email to check
     * @return true if email exists
     * @throws DAOException if database error occurs
     */
    boolean emailExists(String email) throws DAOException;
}
