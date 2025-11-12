package dao.impl;

import dao.TicketDAO;
import dao.DAOException;
import dao.factory.DatabaseFactory;
import models.Ticket;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC Implementation of TicketDAO (Most complex DAO with queue management
 * logic)
 */
public class TicketDAOImpl implements TicketDAO {

    @Override
    public int create(Ticket ticket) throws DAOException {
        String sql = "INSERT INTO tickets (ticket_number, citizen_id, service_id, agency_id, status, position) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
            throw new DAOException("Error creating ticket: " + e.getMessage(), e);
        }
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

        // Get today's count for this service at this agency
        String sql = "SELECT COUNT(*) FROM tickets WHERE agency_id = ? AND service_id = ? AND DATE(created_at) = CURDATE()";

        try (Connection conn = DatabaseFactory.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, agencyId);
            pstmt.setInt(2, serviceId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1) + 1;
                    return serviceLetter + String.format("%03d", count);
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
