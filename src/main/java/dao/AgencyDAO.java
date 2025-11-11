package dao;

import models.Agency;
import java.util.List;

/**
 * DAO Interface for Agency operations
 */
public interface AgencyDAO {
    
    /**
     * Create a new agency
     * @param agency The agency to create
     * @return The ID of the created agency
     * @throws DAOException if database error occurs
     */
    int create(Agency agency) throws DAOException;
    
    /**
     * Find an agency by ID
     * @param id The agency ID
     * @return The agency or null if not found
     * @throws DAOException if database error occurs
     */
    Agency findById(int id) throws DAOException;
    
    /**
     * Find an agency by name
     * @param name The agency name
     * @return The agency or null if not found
     * @throws DAOException if database error occurs
     */
    Agency findByName(String name) throws DAOException;
    
    /**
     * Update an agency
     * @param agency The agency with updated information
     * @return true if update successful
     * @throws DAOException if database error occurs
     */
    boolean update(Agency agency) throws DAOException;
    
    /**
     * Delete an agency by ID
     * @param id The agency ID
     * @return true if deletion successful
     * @throws DAOException if database error occurs
     */
    boolean delete(int id) throws DAOException;
    
    /**
     * Get all agencies
     * @return List of all agencies
     * @throws DAOException if database error occurs
     */
    List<Agency> findAll() throws DAOException;
    
    /**
     * Get agencies by city
     * @param city The city name
     * @return List of agencies in the city
     * @throws DAOException if database error occurs
     */
    List<Agency> findByCity(String city) throws DAOException;
    
    /**
     * Get all distinct cities
     * @return List of cities
     * @throws DAOException if database error occurs
     */
    List<String> getAllCities() throws DAOException;
}
