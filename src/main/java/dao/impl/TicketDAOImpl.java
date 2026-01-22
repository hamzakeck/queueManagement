package dao.impl;

import dao.DAOException;
import dao.TicketDAO;
import dao.factory.DatabaseFactory;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Ticket;

/**
 * JDBC Implementation of TicketDAO (Most complex DAO with queue management
 * logic)
 */
public class TicketDAOImpl implements TicketDAO {

    @Override
    public int create(Ticket ticket) throws DAOException {
        String sql = "INSERT INTO tickets (ticket_number, citizen_id, service_id, agency_id, status, position) VALUES (?, ?, ?, ?, ?, ?)";

        // Retry up to 3 times in case of duplicate key error
        int maxRetries = 3;
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try (Connection conn = DatabaseFactory.getInstance().getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                // If this is a retry, regenerate the ticket number
                if (attempt > 0) {
                    String newTicketNumber = generateTicketNumber(ticket.getAgencyId(), ticket.getServiceId());
                    ticket.setTicketNumber(newTicketNumber);
                }

                pstmt.setString(1, ticket.getTicketNumber());
                pstmt.setInt(2, ticket.getCitizenId());
                pstmt.setInt(3, ticket.getServiceId());
                pstmt.setInt(4, ticket.getAgencyId());
                pstmt.setString(5, ticket.getStatus());
                pstmt.setInt(6, ticket.getPosition());

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new DAOException("Creating ticket failed, no rows affected.");
                }

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    } else {
                        throw new DAOException("Creating ticket failed, no ID obtained.");
                    }
                }
            } catch (SQLException e) {
                // Check if it's a duplicate key error
                if (e.getMessage().contains("Duplicate entry") && attempt < maxRetries - 1) {
                    // Retry with a new ticket number
                    continue;
                }
                throw new DAOException("Error creating ticket: " + e.getMessage(), e);
            }
        }
        throw new DAOException("Failed to create ticket after " + maxRetries + " attempts");
    }

    @Override
    public Ticket findById(int id) throws DAOException {
        String sql = "SELECT * FROM tickets WHERE id = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractTicket(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding ticket by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Ticket findByTicketNumber(String ticketNumber) throws DAOException {
        String sql = "SELECT * FROM tickets WHERE ticket_number = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ticketNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractTicket(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding ticket by number: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Ticket ticket) throws DAOException {
        String sql = "UPDATE tickets SET ticket_number = ?, citizen_id = ?, service_id = ?, agency_id = ?, " +
                "status = ?, position = ?, counter_id = ?, called_at = ?, completed_at = ? WHERE id = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ticket.getTicketNumber());
            pstmt.setInt(2, ticket.getCitizenId());
            pstmt.setInt(3, ticket.getServiceId());
            pstmt.setInt(4, ticket.getAgencyId());
            pstmt.setString(5, ticket.getStatus());
            pstmt.setInt(6, ticket.getPosition());

            if (ticket.getCounterId() > 0) {
                pstmt.setInt(7, ticket.getCounterId());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }

            if (ticket.getCalledAt() != null) {
                pstmt.setTimestamp(8, Timestamp.valueOf(ticket.getCalledAt()));
            } else {
                pstmt.setNull(8, Types.TIMESTAMP);
            }

            if (ticket.getCompletedAt() != null) {
                pstmt.setTimestamp(9, Timestamp.valueOf(ticket.getCompletedAt()));
            } else {
                pstmt.setNull(9, Types.TIMESTAMP);
            }

            pstmt.setInt(10, ticket.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating ticket: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(int ticketId, String status) throws DAOException {
        String sql = "UPDATE tickets SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, ticketId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating ticket status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) throws DAOException {
        String sql = "DELETE FROM tickets WHERE id = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting ticket: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Ticket> findAll() throws DAOException {
        String sql = "SELECT * FROM tickets ORDER BY created_at DESC";
        List<Ticket> tickets = new ArrayList<>();

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                tickets.add(extractTicket(rs));
            }
            return tickets;
        } catch (SQLException e) {
            throw new DAOException("Error finding all tickets: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Ticket> findByCitizen(int citizenId) throws DAOException {
        String sql = "SELECT * FROM tickets WHERE citizen_id = ? ORDER BY created_at DESC";
        List<Ticket> tickets = new ArrayList<>();

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, citizenId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(extractTicket(rs));
                }
                return tickets;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding tickets by citizen: " + e.getMessage(), e);
        }
    }

    @Override
    public Ticket findActiveByCitizen(int citizenId) throws DAOException {
        String sql = "SELECT * FROM tickets WHERE citizen_id = ? AND status IN ('WAITING', 'CALLED', 'IN_PROGRESS') ORDER BY created_at DESC LIMIT 1";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, citizenId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractTicket(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding active ticket by citizen: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Ticket> findByAgencyAndStatus(int agencyId, String status) throws DAOException {
        String sql = "SELECT * FROM tickets WHERE agency_id = ? AND status = ? ORDER BY position, created_at";
        List<Ticket> tickets = new ArrayList<>();

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, agencyId);
            pstmt.setString(2, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(extractTicket(rs));
                }
                return tickets;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding tickets by agency and status: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Ticket> getWaitingQueue(int agencyId, int serviceId) throws DAOException {
        String sql = "SELECT * FROM tickets WHERE agency_id = ? AND service_id = ? AND status = 'WAITING' ORDER BY position, created_at";
        List<Ticket> tickets = new ArrayList<>();

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, agencyId);
            pstmt.setInt(2, serviceId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(extractTicket(rs));
                }
                return tickets;
            }
        } catch (SQLException e) {
            throw new DAOException("Error getting waiting queue: " + e.getMessage(), e);
        }
    }

    @Override
    public Ticket getNextTicket(int agencyId, int serviceId) throws DAOException {
        String sql;
        if (serviceId > 0) {
            sql = "SELECT * FROM tickets WHERE agency_id = ? AND service_id = ? AND status = 'WAITING' ORDER BY position, created_at LIMIT 1";
        } else {
            sql = "SELECT * FROM tickets WHERE agency_id = ? AND status = 'WAITING' ORDER BY position, created_at LIMIT 1";
        }

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, agencyId);
            if (serviceId > 0) {
                pstmt.setInt(2, serviceId);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractTicket(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error getting next ticket: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateTicketNumber(int agencyId, int serviceId) throws DAOException {
        // Ticket number format: ServiceLetter + ThreeDigitNumber
        // Example: A001, A002, B001, etc.

        // Get service letter (A-Z based on service ID)
        String serviceLetter = String.valueOf((char) ('A' + ((serviceId - 1) % 26)));

        // Get the highest ticket number for this service at this agency today
        String sql = "SELECT IFNULL(MAX(CAST(SUBSTRING(ticket_number, 2) AS UNSIGNED)), 0) FROM tickets " +
                "WHERE agency_id = ? AND service_id = ? AND DATE(created_at) = CURDATE() " +
                "AND ticket_number LIKE ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, agencyId);
            pstmt.setInt(2, serviceId);
            pstmt.setString(3, serviceLetter + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int maxNumber = rs.getInt(1);
                    int nextNumber = maxNumber + 1;
                    return serviceLetter + String.format("%03d", nextNumber);
                }
                return serviceLetter + "001";
            }
        } catch (SQLException e) {
            throw new DAOException("Error generating ticket number: " + e.getMessage(), e);
        }
    }

    @Override
    public int getNextPosition(int agencyId, int serviceId) throws DAOException {
        String sql = "SELECT COALESCE(MAX(position), 0) + 1 FROM tickets WHERE agency_id = ? AND service_id = ? AND status = 'WAITING'";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, agencyId);
            pstmt.setInt(2, serviceId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 1;
            }
        } catch (SQLException e) {
            throw new DAOException("Error getting next position: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean assignToCounter(int ticketId, int counterId) throws DAOException {
        String sql = "UPDATE tickets SET counter_id = ? WHERE id = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, counterId);
            pstmt.setInt(2, ticketId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error assigning ticket to counter: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean callTicket(int ticketId, int counterId) throws DAOException {
        String sql = "UPDATE tickets SET status = 'CALLED', counter_id = ?, called_at = NOW() WHERE id = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, counterId);
            pstmt.setInt(2, ticketId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error calling ticket: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean startService(int ticketId) throws DAOException {
        String sql = "UPDATE tickets SET status = 'IN_PROGRESS' WHERE id = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ticketId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error starting service on ticket: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean completeTicket(int ticketId) throws DAOException {
        String sql = "UPDATE tickets SET status = 'COMPLETED', completed_at = NOW() WHERE id = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ticketId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error completing ticket: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean cancelTicket(int ticketId) throws DAOException {
        String sql = "UPDATE tickets SET status = 'CANCELLED' WHERE id = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ticketId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error cancelling ticket: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Ticket> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws DAOException {
        String sql = "SELECT * FROM tickets WHERE created_at BETWEEN ? AND ? ORDER BY created_at DESC";
        List<Ticket> tickets = new ArrayList<>();

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(extractTicket(rs));
                }
                return tickets;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding tickets by date range: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Integer> getTicketCountByStatus(LocalDate date, int agencyId) throws DAOException {
        String sql;
        if (agencyId > 0) {
            sql = "SELECT status, COUNT(*) as count FROM tickets WHERE DATE(created_at) = ? AND agency_id = ? GROUP BY status";
        } else {
            sql = "SELECT status, COUNT(*) as count FROM tickets WHERE DATE(created_at) = ? GROUP BY status";
        }

        Map<String, Integer> statusCounts = new HashMap<>();

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(date));
            if (agencyId > 0) {
                pstmt.setInt(2, agencyId);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    statusCounts.put(rs.getString("status"), rs.getInt("count"));
                }
                return statusCounts;
            }
        } catch (SQLException e) {
            throw new DAOException("Error getting ticket count by status: " + e.getMessage(), e);
        }
    }

    @Override
    public double getAverageWaitingTime(int serviceId, int agencyId, LocalDate date) throws DAOException {
        String sql;
        if (date != null) {
            sql = "SELECT AVG(TIMESTAMPDIFF(MINUTE, created_at, called_at)) as avg_time " +
                    "FROM tickets WHERE service_id = ? AND agency_id = ? AND DATE(created_at) = ? " +
                    "AND status IN ('CALLED', 'IN_PROGRESS', 'COMPLETED') AND called_at IS NOT NULL";
        } else {
            sql = "SELECT AVG(TIMESTAMPDIFF(MINUTE, created_at, called_at)) as avg_time " +
                    "FROM tickets WHERE service_id = ? AND agency_id = ? " +
                    "AND status IN ('CALLED', 'IN_PROGRESS', 'COMPLETED') AND called_at IS NOT NULL";
        }

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, serviceId);
            pstmt.setInt(2, agencyId);
            if (date != null) {
                pstmt.setDate(3, Date.valueOf(date));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_time");
                }
                return 0.0;
            }
        } catch (SQLException e) {
            throw new DAOException("Error getting average waiting time: " + e.getMessage(), e);
        }
    }

    @Override
    public int getTodayTicketCount(int agencyId) throws DAOException {
        String sql = "SELECT COUNT(*) FROM tickets WHERE agency_id = ? AND DATE(created_at) = CURDATE()";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, agencyId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DAOException("Error getting today's ticket count: " + e.getMessage(), e);
        }
    }

    @Override
    public int getEstimatedWaitingTime(int agencyId, int serviceId, int position) throws DAOException {
        // Get the service estimated time
        String sql = "SELECT estimated_time FROM services WHERE id = ?";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, serviceId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int estimatedTime = rs.getInt("estimated_time");
                    // Multiply by position to get total estimated waiting time
                    return estimatedTime * position;
                }
                return 15 * position; // Default 15 minutes per position
            }
        } catch (SQLException e) {
            throw new DAOException("Error estimating waiting time: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Ticket> findByCitizenId(int citizenId) throws DAOException {
        String sql = "SELECT * FROM tickets WHERE citizen_id = ? ORDER BY created_at DESC";
        List<Ticket> tickets = new ArrayList<>();

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, citizenId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(extractTicket(rs));
                }
                return tickets;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding tickets by citizen ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Ticket getCurrentTicketForEmployee(int employeeId) throws DAOException {
        // Get employee's counter_id first
        String counterSql = "SELECT counter_id FROM employees WHERE id = ?";
        int counterId = 0;

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(counterSql)) {

            pstmt.setInt(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    counterId = rs.getInt("counter_id");
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error getting employee counter: " + e.getMessage(), e);
        }

        if (counterId == 0) {
            return null;
        }

        // Get current ticket being served at this counter
        String ticketSql = "SELECT * FROM tickets WHERE counter_id = ? AND status = 'IN_PROGRESS' LIMIT 1";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(ticketSql)) {

            pstmt.setInt(1, counterId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractTicket(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error getting current ticket for employee: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Ticket> getTicketsByEmployeeAndStatus(int employeeId, String status) throws DAOException {
        List<Ticket> tickets = new ArrayList<>();

        // Get employee's agency_id and service_id
        String employeeSql = "SELECT agency_id, service_id FROM employees WHERE id = ?";
        int agencyId = 0;
        int serviceId = 0;

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(employeeSql)) {

            pstmt.setInt(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    agencyId = rs.getInt("agency_id");
                    serviceId = rs.getInt("service_id");
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error getting employee info: " + e.getMessage(), e);
        }

        if (agencyId == 0 || serviceId == 0) {
            return tickets;
        }

        // Get tickets for this service at this agency with the given status
        String ticketSql = "SELECT * FROM tickets WHERE agency_id = ? AND service_id = ? AND status = ? ORDER BY position, created_at";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(ticketSql)) {

            pstmt.setInt(1, agencyId);
            pstmt.setInt(2, serviceId);
            pstmt.setString(3, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(extractTicket(rs));
                }
                return tickets;
            }
        } catch (SQLException e) {
            throw new DAOException("Error getting tickets by employee and status: " + e.getMessage(), e);
        }
    }

    @Override
    public Ticket callNextTicket(int employeeId) throws DAOException {
        // Get employee info
        String employeeSql = "SELECT agency_id, service_id, counter_id FROM employees WHERE id = ?";
        int agencyId = 0;
        int serviceId = 0;
        int counterId = 0;

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(employeeSql)) {

            pstmt.setInt(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    agencyId = rs.getInt("agency_id");
                    serviceId = rs.getInt("service_id");
                    counterId = rs.getInt("counter_id");
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error getting employee info: " + e.getMessage(), e);
        }

        if (agencyId == 0 || serviceId == 0) {
            return null;
        }

        // Get the next waiting ticket
        Ticket nextTicket = getNextTicket(agencyId, serviceId);

        if (nextTicket != null && counterId > 0) {
            // Update ticket to IN_PROGRESS and assign to counter
            String updateSql = "UPDATE tickets SET status = 'IN_PROGRESS', counter_id = ?, called_at = NOW() WHERE id = ?";

            try (Connection conn = DatabaseFactory.getInstance().getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(updateSql)) {

                pstmt.setInt(1, counterId);
                pstmt.setInt(2, nextTicket.getId());
                pstmt.executeUpdate();

                // Refresh ticket data
                return findById(nextTicket.getId());
            } catch (SQLException e) {
                throw new DAOException("Error calling next ticket: " + e.getMessage(), e);
            }
        }

        return null;
    }

    @Override
    public double getAverageServiceTime(int serviceId, int agencyId) throws DAOException {
        // Get last 20 completed tickets and calculate average time
        String sql = "SELECT called_at, completed_at FROM tickets " +
                "WHERE service_id = ? AND agency_id = ? " +
                "AND status = 'COMPLETED' " +
                "AND called_at IS NOT NULL AND completed_at IS NOT NULL " +
                "ORDER BY completed_at DESC LIMIT 20";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, serviceId);
            pstmt.setInt(2, agencyId);

            int totalMinutes = 0;
            int count = 0;

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp calledAt = rs.getTimestamp("called_at");
                    Timestamp completedAt = rs.getTimestamp("completed_at");

                    if (calledAt != null && completedAt != null) {
                        // Calculate difference in minutes
                        long diffMillis = completedAt.getTime() - calledAt.getTime();
                        int minutes = (int) (diffMillis / 60000);
                        totalMinutes += minutes;
                        count++;
                    }
                }
            }

            // Return average, or default 5 minutes if no data
            if (count > 0) {
                return (double) totalMinutes / count;
            } else {
                return 5.0; // Default 5 minutes
            }

        } catch (SQLException e) {
            throw new DAOException("Error calculating average service time: " + e.getMessage(), e);
        }
    }

    @Override
    public int getPositionInQueue(int ticketId) throws DAOException {
        // First get the ticket info
        Ticket ticket = findById(ticketId);
        if (ticket == null || !"WAITING".equals(ticket.getStatus())) {
            return 0; // Not waiting, no position
        }

        // Count tickets created before this one with same service/agency
        String sql = "SELECT COUNT(*) as position FROM tickets " +
                "WHERE service_id = ? AND agency_id = ? " +
                "AND status = 'WAITING' " +
                "AND created_at < (SELECT created_at FROM tickets WHERE id = ?)";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ticket.getServiceId());
            pstmt.setInt(2, ticket.getAgencyId());
            pstmt.setInt(3, ticketId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("position");
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DAOException("Error getting position in queue: " + e.getMessage(), e);
        }
    }

    /**
     * Extract a Ticket object from ResultSet
     */
    private Ticket extractTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getInt("id"));
        ticket.setTicketNumber(rs.getString("ticket_number"));
        ticket.setCitizenId(rs.getInt("citizen_id"));
        ticket.setServiceId(rs.getInt("service_id"));
        ticket.setAgencyId(rs.getInt("agency_id"));
        ticket.setStatus(rs.getString("status"));
        ticket.setPosition(rs.getInt("position"));
        ticket.setCounterId(rs.getInt("counter_id"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            ticket.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp calledAt = rs.getTimestamp("called_at");
        if (calledAt != null) {
            ticket.setCalledAt(calledAt.toLocalDateTime());
        }

        Timestamp completedAt = rs.getTimestamp("completed_at");
        if (completedAt != null) {
            ticket.setCompletedAt(completedAt.toLocalDateTime());
        }

        return ticket;
    }
}
