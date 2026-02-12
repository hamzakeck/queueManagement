package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dao.DAOException;
import dao.factory.DatabaseFactory;
import models.Ticket;

class TicketDAOImplTest {

    private TicketDAOImpl dao;
    private MockedStatic<DatabaseFactory> mockedFactory;
    private DatabaseFactory mockFactoryInstance;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        dao = new TicketDAOImpl();

        mockFactoryInstance = mock(DatabaseFactory.class);
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        mockedFactory = Mockito.mockStatic(DatabaseFactory.class);
        mockedFactory.when(DatabaseFactory::getInstance).thenReturn(mockFactoryInstance);
        when(mockFactoryInstance.getConnection()).thenReturn(mockConnection);
    }

    @AfterEach
    void tearDown() {
        mockedFactory.close();
    }

    private void stubTicketResultSet() throws SQLException {
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("ticket_number")).thenReturn("A001");
        when(mockResultSet.getInt("citizen_id")).thenReturn(10);
        when(mockResultSet.getInt("service_id")).thenReturn(2);
        when(mockResultSet.getInt("agency_id")).thenReturn(3);
        when(mockResultSet.getString("status")).thenReturn("WAITING");
        when(mockResultSet.getInt("position")).thenReturn(1);
        when(mockResultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
        when(mockResultSet.getTimestamp("called_at")).thenReturn(null);
        when(mockResultSet.getTimestamp("completed_at")).thenReturn(null);
        when(mockResultSet.getInt("counter_id")).thenReturn(0);
    }

    // ===== CREATE =====
    @Test
    void createReturnsGeneratedId() throws Exception {
        ResultSet generatedKeys = mock(ResultSet.class);
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(42);

        Ticket ticket = new Ticket();
        ticket.setTicketNumber("A001");
        ticket.setCitizenId(1);
        ticket.setServiceId(2);
        ticket.setAgencyId(3);
        ticket.setStatus("WAITING");
        ticket.setPosition(1);

        int id = dao.create(ticket);
        assertEquals(42, id);
    }

    @Test
    void createThrowsWhenNoRowsAffected() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        Ticket ticket = new Ticket();
        assertThrows(DAOException.class, () -> dao.create(ticket));
    }

    @Test
    void createThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenThrow(new SQLException("DB error"));

        Ticket ticket = new Ticket();
        assertThrows(DAOException.class, () -> dao.create(ticket));
    }

    // ===== FIND BY ID =====
    @Test
    void findByIdReturnsTicket() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        stubTicketResultSet();

        Ticket result = dao.findById(1);

        assertNotNull(result);
        assertEquals("A001", result.getTicketNumber());
    }

    @Test
    void findByIdReturnsNullWhenNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertNull(dao.findById(999));
    }

    @Test
    void findByIdThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.findById(1));
    }

    // ===== FIND BY TICKET NUMBER =====
    @Test
    void findByTicketNumberReturnsTicket() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        stubTicketResultSet();

        Ticket result = dao.findByTicketNumber("A001");

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void findByTicketNumberReturnsNullWhenNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertNull(dao.findByTicketNumber("INVALID"));
    }

    @Test
    void findByTicketNumberThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.findByTicketNumber("A001"));
    }

    // ===== UPDATE =====
    @Test
    void updateReturnsTrue() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setStatus("IN_PROGRESS");
        ticket.setCounterId(2);

        assertTrue(dao.update(ticket));
    }

    @Test
    void updateReturnsFalseWhenNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        Ticket ticket = new Ticket();
        ticket.setId(999);

        assertFalse(dao.update(ticket));
    }

    @Test
    void updateThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        Ticket ticket = new Ticket();
        ticket.setId(1);

        assertThrows(DAOException.class, () -> dao.update(ticket));
    }

    // ===== UPDATE STATUS =====
    @Test
    void updateStatusReturnsTrue() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        assertTrue(dao.updateStatus(1, "COMPLETED"));
    }

    @Test
    void updateStatusReturnsFalseWhenNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        assertFalse(dao.updateStatus(999, "COMPLETED"));
    }

    @Test
    void updateStatusThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.updateStatus(1, "COMPLETED"));
    }

    // ===== DELETE =====
    @Test
    void deleteReturnsTrue() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        assertTrue(dao.delete(1));
    }

    @Test
    void deleteReturnsFalseWhenNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        assertFalse(dao.delete(999));
    }

    @Test
    void deleteThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.delete(1));
    }

    // ===== FIND ALL =====
    @Test
    void findAllReturnsListOfTickets() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        stubTicketResultSet();

        List<Ticket> result = dao.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void findAllReturnsEmptyListWhenNoData() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        List<Ticket> result = dao.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.findAll());
    }

    // ===== FIND BY CITIZEN =====
    @Test
    void findByCitizenReturnsListOfTickets() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        stubTicketResultSet();

        List<Ticket> tickets = dao.findByCitizen(1);

        assertEquals(1, tickets.size());
        assertEquals("A001", tickets.get(0).getTicketNumber());
    }

    @Test
    void findByCitizenThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.findByCitizen(1));
    }

    // ===== FIND BY CITIZEN ID =====
    @Test
    void findByCitizenIdReturnsListOfTickets() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        stubTicketResultSet();

        List<Ticket> result = dao.findByCitizenId(1);
        assertEquals(1, result.size());
    }

    @Test
    void findByCitizenIdThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.findByCitizenId(1));
    }

    // ===== FIND ACTIVE BY CITIZEN =====
    @Test
    void findActiveByCitizenReturnsTicket() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        stubTicketResultSet();

        Ticket result = dao.findActiveByCitizen(1);

        assertNotNull(result);
        assertEquals("A001", result.getTicketNumber());
    }

    @Test
    void findActiveByCitizenReturnsNullWhenNoActive() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertNull(dao.findActiveByCitizen(1));
    }

    @Test
    void findActiveByCitizenThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.findActiveByCitizen(1));
    }

    // ===== FIND BY AGENCY AND STATUS =====
    @Test
    void findByAgencyAndStatusReturnsListOfTickets() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        stubTicketResultSet();

        List<Ticket> tickets = dao.findByAgencyAndStatus(1, "WAITING");

        assertEquals(1, tickets.size());
    }

    @Test
    void findByAgencyAndStatusThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.findByAgencyAndStatus(1, "WAITING"));
    }

    // ===== GET WAITING QUEUE =====
    @Test
    void getWaitingQueueReturnsListOfTickets() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        stubTicketResultSet();

        List<Ticket> tickets = dao.getWaitingQueue(1, 2);

        assertEquals(1, tickets.size());
    }

    @Test
    void getWaitingQueueThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.getWaitingQueue(1, 2));
    }

    // ===== GET NEXT TICKET =====
    @Test
    void getNextTicketReturnsTicket() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        stubTicketResultSet();

        Ticket result = dao.getNextTicket(1, 2);

        assertNotNull(result);
    }

    @Test
    void getNextTicketReturnsNullWhenNoTickets() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertNull(dao.getNextTicket(1, 2));
    }

    @Test
    void getNextTicketThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.getNextTicket(1, 2));
    }

    // ===== GENERATE TICKET NUMBER =====
    @Test
    void generateTicketNumberReturnsNumber() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true);
        when(mockResultSet.getString("code")).thenReturn("A");
        when(mockResultSet.getInt(1)).thenReturn(5);

        String ticketNumber = dao.generateTicketNumber(1, 2);

        assertNotNull(ticketNumber);
        assertTrue(ticketNumber.startsWith("A"));
    }

    @Test
    void generateTicketNumberThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.generateTicketNumber(1, 2));
    }

    // ===== GET NEXT POSITION =====
    @Test
    void getNextPositionReturnsPosition() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(5);

        int position = dao.getNextPosition(1, 2);

        assertEquals(6, position);
    }

    @Test
    void getNextPositionThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.getNextPosition(1, 2));
    }

    // ===== ASSIGN TO COUNTER =====
    @Test
    void assignToCounterReturnsTrue() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        assertTrue(dao.assignToCounter(1, 2));
    }

    @Test
    void assignToCounterReturnsFalseWhenNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        assertFalse(dao.assignToCounter(1, 2));
    }

    @Test
    void assignToCounterThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.assignToCounter(1, 2));
    }

    // ===== CALL TICKET =====
    @Test
    void callTicketReturnsTrue() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        assertTrue(dao.callTicket(1, 2));
    }

    @Test
    void callTicketReturnsFalseWhenNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        assertFalse(dao.callTicket(1, 2));
    }

    @Test
    void callTicketThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.callTicket(1, 2));
    }

    // ===== START SERVICE =====
    @Test
    void startServiceReturnsTrue() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        assertTrue(dao.startService(1));
    }

    @Test
    void startServiceReturnsFalseWhenNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        assertFalse(dao.startService(1));
    }

    @Test
    void startServiceThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.startService(1));
    }

    // ===== COMPLETE TICKET =====
    @Test
    void completeTicketReturnsTrue() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        assertTrue(dao.completeTicket(1));
    }

    @Test
    void completeTicketReturnsFalseWhenNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        assertFalse(dao.completeTicket(1));
    }

    @Test
    void completeTicketThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.completeTicket(1));
    }

    // ===== CANCEL TICKET =====
    @Test
    void cancelTicketReturnsTrue() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        assertTrue(dao.cancelTicket(1));
    }

    @Test
    void cancelTicketReturnsFalseWhenNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        assertFalse(dao.cancelTicket(1));
    }

    @Test
    void cancelTicketThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.cancelTicket(1));
    }

    // ===== FIND BY DATE RANGE =====
    @Test
    void findByDateRangeReturnsListOfTickets() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        stubTicketResultSet();

        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<Ticket> tickets = dao.findByDateRange(start, end);

        assertEquals(1, tickets.size());
    }

    @Test
    void findByDateRangeThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        assertThrows(DAOException.class, () -> dao.findByDateRange(start, end));
    }

    // ===== GET TICKET COUNT BY STATUS =====
    @Test
    void getTicketCountByStatusReturnsMap() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("status")).thenReturn("WAITING", "COMPLETED");
        when(mockResultSet.getInt("count")).thenReturn(5, 10);

        Map<String, Integer> result = dao.getTicketCountByStatus(LocalDate.now(), 1);

        assertEquals(2, result.size());
        assertEquals(5, result.get("WAITING"));
        assertEquals(10, result.get("COMPLETED"));
    }

    @Test
    void getTicketCountByStatusThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.getTicketCountByStatus(LocalDate.now(), 1));
    }

    // ===== GET AVERAGE WAITING TIME =====
    @Test
    void getAverageWaitingTimeReturnsTime() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getDouble(1)).thenReturn(15.5);

        double result = dao.getAverageWaitingTime(1, 2, LocalDate.now());

        assertEquals(15.5, result, 0.01);
    }

    @Test
    void getAverageWaitingTimeReturnsZeroWhenNoData() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        double result = dao.getAverageWaitingTime(1, 2, LocalDate.now());

        assertEquals(0.0, result, 0.01);
    }

    @Test
    void getAverageWaitingTimeThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.getAverageWaitingTime(1, 2, LocalDate.now()));
    }

    // ===== GET TODAY TICKET COUNT =====
    @Test
    void getTodayTicketCountReturnsCount() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(25);

        int result = dao.getTodayTicketCount(1);

        assertEquals(25, result);
    }

    @Test
    void getTodayTicketCountThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.getTodayTicketCount(1));
    }

    // ===== GET ESTIMATED WAITING TIME =====
    @Test
    void getEstimatedWaitingTimeReturnsTime() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getDouble(1)).thenReturn(5.0);

        int result = dao.getEstimatedWaitingTime(1, 2, 3);

        assertEquals(15, result);
    }

    @Test
    void getEstimatedWaitingTimeThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.getEstimatedWaitingTime(1, 2, 3));
    }

    // ===== GET CURRENT TICKET FOR EMPLOYEE =====
    @Test
    void getCurrentTicketForEmployeeReturnsTicket() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        stubTicketResultSet();

        Ticket result = dao.getCurrentTicketForEmployee(1);
        assertNotNull(result);
    }

    @Test
    void getCurrentTicketForEmployeeReturnsNullWhenNoTicket() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Ticket result = dao.getCurrentTicketForEmployee(1);
        assertNull(result);
    }

    @Test
    void getCurrentTicketForEmployeeThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.getCurrentTicketForEmployee(1));
    }

    // ===== GET TICKETS BY EMPLOYEE AND STATUS =====
    @Test
    void getTicketsByEmployeeAndStatusReturnsListOfTickets() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        stubTicketResultSet();

        List<Ticket> tickets = dao.getTicketsByEmployeeAndStatus(1, "IN_PROGRESS");

        assertEquals(1, tickets.size());
    }

    @Test
    void getTicketsByEmployeeAndStatusThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.getTicketsByEmployeeAndStatus(1, "IN_PROGRESS"));
    }

    // ===== CALL NEXT TICKET =====
    @Test
    void callNextTicketReturnsTicket() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true);
        when(mockResultSet.getInt("agency_id")).thenReturn(1);
        when(mockResultSet.getInt("counter_id")).thenReturn(2);
        stubTicketResultSet();
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Ticket result = dao.callNextTicket(1);
        assertNotNull(result);
    }

    @Test
    void callNextTicketReturnsNullWhenNoWaiting() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("agency_id")).thenReturn(1);
        when(mockResultSet.getInt("counter_id")).thenReturn(2);

        Ticket result = dao.callNextTicket(1);
        assertNull(result);
    }

    @Test
    void callNextTicketThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.callNextTicket(1));
    }

    // ===== GET AVERAGE SERVICE TIME =====
    @Test
    void getAverageServiceTimeReturnsTime() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getDouble(1)).thenReturn(8.5);
        when(mockResultSet.wasNull()).thenReturn(false);

        double result = dao.getAverageServiceTime(1, 2);

        assertEquals(8.5, result, 0.01);
    }

    @Test
    void getAverageServiceTimeReturnsDefaultWhenNoData() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getDouble(1)).thenReturn(0.0);
        when(mockResultSet.wasNull()).thenReturn(true);

        double result = dao.getAverageServiceTime(1, 2);

        assertEquals(5.0, result, 0.01);
    }

    @Test
    void getAverageServiceTimeThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.getAverageServiceTime(1, 2));
    }

    // ===== GET POSITION IN QUEUE =====
    @Test
    void getPositionInQueueReturnsPosition() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(3);

        int result = dao.getPositionInQueue(1);

        assertEquals(3, result);
    }

    @Test
    void getPositionInQueueReturnsZeroWhenNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        int result = dao.getPositionInQueue(1);

        assertEquals(0, result);
    }

    @Test
    void getPositionInQueueThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.getPositionInQueue(1));
    }

    // ===== UPDATE WITH NULLABLE FIELDS =====
    @Test
    void updateWithNullCounterAndEmployee() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setStatus("WAITING");
        ticket.setCounterId(0);

        assertTrue(dao.update(ticket));
    }

    @Test
    void updateWithNonNullTimestamps() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setStatus("COMPLETED");
        ticket.setCounterId(1);
        ticket.setCalledAt(LocalDateTime.now());
        ticket.setCompletedAt(LocalDateTime.now());

        assertTrue(dao.update(ticket));
    }
}
