package dao;

import models.Citizen;
import java.util.List;

/**
 * DAO Interface for Citizen operations
 */
public interface CitizenDAO {

    /**
     * Create a new citizen
     * 
     * @param citizen The citizen to create
     * @return The ID of the created citizen
     * @throws DAOException if database error occurs
     */
    int create(Citizen citizen) throws DAOException;

    /**
     * Find a citizen by ID
     * 
     * @param id The citizen ID
     * @return The citizen or null if not found
     * @throws DAOException if database error occurs
     */
    Citizen findById(int id) throws DAOException;

    /**
     * Find a citizen by email
     * 
     * @param email The citizen email
     * @return The citizen or null if not found
     * @throws DAOException if database error occurs
     */
    Citizen findByEmail(String email) throws DAOException;

    /**
     * Find a citizen by CIN (National ID)
     * 
     * @param cin The citizen CIN
     * @return The citizen or null if not found
     * @throws DAOException if database error occurs
     */
    Citizen findByCin(String cin) throws DAOException;

    /**
     * Update a citizen
     * 
     * @param citizen The citizen with updated information
     * @return true if update successful
     * @throws DAOException if database error occurs
     */
    boolean update(Citizen citizen) throws DAOException;

    /**
     * Delete a citizen by ID
     * 
     * @param id The citizen ID
     * @return true if deletion successful
     * @throws DAOException if database error occurs
     */
    boolean delete(int id) throws DAOException;

    /**
     * Get all citizens
     * 
     * @return List of all citizens
     * @throws DAOException if database error occurs
     */
    List<Citizen> findAll() throws DAOException;

    /**
     * Authenticate a citizen
     * 
     * @param email    The citizen email
     * @param password The citizen password
     * @return The citizen if authentication successful, null otherwise
     * @throws DAOException if database error occurs
     */
    Citizen authenticate(String email, String password) throws DAOException;

    /**
     * Check if email already exists
     * 
     * @param email The email to check
     * @return true if email exists
     * @throws DAOException if database error occurs
     */
    boolean emailExists(String email) throws DAOException;

    /**
     * Check if CIN already exists
     * 
     * @param cin The CIN to check
     * @return true if CIN exists
     * @throws DAOException if database error occurs
     */
    boolean cinExists(String cin) throws DAOException;
}
