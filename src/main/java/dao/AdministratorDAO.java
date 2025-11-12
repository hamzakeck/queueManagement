package dao;

import models.Administrator;
import java.util.List;

/**
 * DAO Interface for Administrator operations
 */
public interface AdministratorDAO {

    /**
     * Create a new administrator
     * 
     * @param admin The administrator to create
     * @return The ID of the created administrator
     * @throws DAOException if database error occurs
     */
    int create(Administrator admin) throws DAOException;

    /**
     * Find an administrator by ID
     * 
     * @param id The administrator ID
     * @return The administrator or null if not found
     * @throws DAOException if database error occurs
     */
    Administrator findById(int id) throws DAOException;

    /**
     * Find an administrator by email
     * 
     * @param email The administrator email
     * @return The administrator or null if not found
     * @throws DAOException if database error occurs
     */
    Administrator findByEmail(String email) throws DAOException;

    /**
     * Update an administrator
     * 
     * @param admin The administrator with updated information
     * @return true if update successful
     * @throws DAOException if database error occurs
     */
    boolean update(Administrator admin) throws DAOException;

    /**
     * Delete an administrator by ID
     * 
     * @param id The administrator ID
     * @return true if deletion successful
     * @throws DAOException if database error occurs
     */
    boolean delete(int id) throws DAOException;

    /**
     * Get all administrators
     * 
     * @return List of all administrators
     * @throws DAOException if database error occurs
     */
    List<Administrator> findAll() throws DAOException;

    /**
     * Authenticate an administrator
     * 
     * @param email    The administrator email
     * @param password The administrator password
     * @return The administrator if authentication successful, null otherwise
     * @throws DAOException if database error occurs
     */
    Administrator authenticate(String email, String password) throws DAOException;

    /**
     * Check if email already exists
     * 
     * @param email The email to check
     * @return true if email exists
     * @throws DAOException if database error occurs
     */
    boolean emailExists(String email) throws DAOException;
}
