package dao;

import models.Service;
import java.util.List;

/**
 * DAO Interface for Service operations
 */
public interface ServiceDAO {

    /**
     * Create a new service
     * 
     * @param service The service to create
     * @return The ID of the created service
     * @throws DAOException if database error occurs
     */
    int create(Service service) throws DAOException;

    /**
     * Save a service (create if new, update if exists)
     * 
     * @param service The service to save
     * @return The ID of the saved service
     * @throws DAOException if database error occurs
     */
    int save(Service service) throws DAOException;

    /**
     * Find a service by ID
     * 
     * @param id The service ID
     * @return The service or null if not found
     * @throws DAOException if database error occurs
     */
    Service findById(int id) throws DAOException;

    /**
     * Find a service by name
     * 
     * @param name The service name
     * @return The service or null if not found
     * @throws DAOException if database error occurs
     */
    Service findByName(String name) throws DAOException;

    /**
     * Update a service
     * 
     * @param service The service with updated information
     * @return true if update successful
     * @throws DAOException if database error occurs
     */
    boolean update(Service service) throws DAOException;

    /**
     * Delete a service by ID
     * 
     * @param id The service ID
     * @return true if deletion successful
     * @throws DAOException if database error occurs
     */
    boolean delete(int id) throws DAOException;

    /**
     * Get all services
     * 
     * @return List of all services
     * @throws DAOException if database error occurs
     */
    List<Service> findAll() throws DAOException;

    /**
     * Get all active services
     * 
     * @return List of active services
     * @throws DAOException if database error occurs
     */
    List<Service> findAllActive() throws DAOException;

    /**
     * Activate or deactivate a service
     * 
     * @param id     The service ID
     * @param active true to activate, false to deactivate
     * @return true if update successful
     * @throws DAOException if database error occurs
     */
    boolean setActive(int id, boolean active) throws DAOException;
}
