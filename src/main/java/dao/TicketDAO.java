package dao;

import models.Ticket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DAO Interface for Ticket operations (Most complex DAO)
 */
public interface TicketDAO {
    
    /**
     * Create a new ticket
     * @param ticket The ticket to create
     * @return The ID of the created ticket
     * @throws DAOException if database error occurs
     */
    int create(Ticket ticket) throws DAOException;
    
    /**
     * Find a ticket by ID
     * @param id The ticket ID
     * @return The ticket or null if not found
     * @throws DAOException if database error occurs
     */
    Ticket findById(int id) throws DAOException;
    
    /**
     * Find a ticket by ticket number
     * @param ticketNumber The unique ticket number
     * @return The ticket or null if not found
     * @throws DAOException if database error occurs
     */
    Ticket findByTicketNumber(String ticketNumber) throws DAOException;
    
    /**
     * Update a ticket
     * @param ticket The ticket with updated information
     * @return true if update successful
     * @throws DAOException if database error occurs
     */
    boolean update(Ticket ticket) throws DAOException;
    
    /**
     * Update ticket status
     * @param ticketId The ticket ID
     * @param status The new status
     * @return true if update successful
     * @throws DAOException if database error occurs
     */
    boolean updateStatus(int ticketId, String status) throws DAOException;
    
    /**
     * Delete a ticket by ID
     * @param id The ticket ID
     * @return true if deletion successful
     * @throws DAOException if database error occurs
     */
    boolean delete(int id) throws DAOException;
    
    /**
     * Get all tickets
     * @return List of all tickets
     * @throws DAOException if database error occurs
     */
    List<Ticket> findAll() throws DAOException;
    
    /**
     * Get tickets by citizen ID
     * @param citizenId The citizen ID
     * @return List of tickets for the citizen
     * @throws DAOException if database error occurs
     */
    List<Ticket> findByCitizen(int citizenId) throws DAOException;
    
    /**
     * Get citizen's active ticket (WAITING, CALLED, IN_PROGRESS)
     * @param citizenId The citizen ID
     * @return The active ticket or null
     * @throws DAOException if database error occurs
     */
    Ticket findActiveByCitizen(int citizenId) throws DAOException;
    
    /**
     * Get tickets by agency and status
     * @param agencyId The agency ID
     * @param status The ticket status
     * @return List of tickets
     * @throws DAOException if database error occurs
     */
    List<Ticket> findByAgencyAndStatus(int agencyId, String status) throws DAOException;
    
    /**
     * Get waiting tickets for an agency and service (ordered by position)
     * @param agencyId The agency ID
     * @param serviceId The service ID
     * @return List of waiting tickets in order
     * @throws DAOException if database error occurs
     */
    List<Ticket> getWaitingQueue(int agencyId, int serviceId) throws DAOException;
    
    /**
     * Get the next ticket in queue for a service at an agency
     * @param agencyId The agency ID
     * @param serviceId The service ID (optional, pass 0 for any service)
     * @return The next ticket or null if queue is empty
     * @throws DAOException if database error occurs
     */
    Ticket getNextTicket(int agencyId, int serviceId) throws DAOException;
    
    /**
     * Generate a unique ticket number for an agency and service
     * @param agencyId The agency ID
     * @param serviceId The service ID
     * @return A unique ticket number (e.g., "A001", "B023")
     * @throws DAOException if database error occurs
     */
    String generateTicketNumber(int agencyId, int serviceId) throws DAOException;
    
    /**
     * Get the next position in queue for an agency and service
     * @param agencyId The agency ID
     * @param serviceId The service ID
     * @return The next position number
     * @throws DAOException if database error occurs
     */
    int getNextPosition(int agencyId, int serviceId) throws DAOException;
    
    /**
     * Assign ticket to a counter
     * @param ticketId The ticket ID
     * @param counterId The counter ID
     * @return true if assignment successful
     * @throws DAOException if database error occurs
     */
    boolean assignToCounter(int ticketId, int counterId) throws DAOException;
    
    /**
     * Call the next ticket (change status to CALLED)
     * @param ticketId The ticket ID
     * @param counterId The counter ID
     * @return true if successful
     * @throws DAOException if database error occurs
     */
    boolean callTicket(int ticketId, int counterId) throws DAOException;
    
    /**
     * Start service on a ticket (change status to IN_PROGRESS)
     * @param ticketId The ticket ID
     * @return true if successful
     * @throws DAOException if database error occurs
     */
    boolean startService(int ticketId) throws DAOException;
    
    /**
     * Complete a ticket (change status to COMPLETED)
     * @param ticketId The ticket ID
     * @return true if successful
     * @throws DAOException if database error occurs
     */
    boolean completeTicket(int ticketId) throws DAOException;
    
    /**
     * Cancel a ticket (change status to CANCELLED)
     * @param ticketId The ticket ID
     * @return true if successful
     * @throws DAOException if database error occurs
     */
    boolean cancelTicket(int ticketId) throws DAOException;
    
    /**
     * Get tickets by date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of tickets in date range
     * @throws DAOException if database error occurs
     */
    List<Ticket> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws DAOException;
    
    /**
     * Get count of tickets by status for a date
     * @param date The date
     * @param agencyId The agency ID (optional, pass 0 for all agencies)
     * @return Map of status -> count
     * @throws DAOException if database error occurs
     */
    Map<String, Integer> getTicketCountByStatus(LocalDate date, int agencyId) throws DAOException;
    
    /**
     * Get average waiting time for a service at an agency
     * @param serviceId The service ID
     * @param agencyId The agency ID
     * @param date The date (optional, pass null for all time)
     * @return Average waiting time in minutes
     * @throws DAOException if database error occurs
     */
    double getAverageWaitingTime(int serviceId, int agencyId, LocalDate date) throws DAOException;
    
    /**
     * Get count of tickets for today by agency
     * @param agencyId The agency ID
     * @return Count of today's tickets
     * @throws DAOException if database error occurs
     */
    int getTodayTicketCount(int agencyId) throws DAOException;
    
    /**
     * Get estimated waiting time for a position
     * @param agencyId The agency ID
     * @param serviceId The service ID
     * @param position The position in queue
     * @return Estimated waiting time in minutes
     * @throws DAOException if database error occurs
     */
    int getEstimatedWaitingTime(int agencyId, int serviceId, int position) throws DAOException;
}
