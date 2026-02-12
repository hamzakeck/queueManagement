package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dao.DAOException;
import dao.factory.DatabaseFactory;
import models.Service;

class ServiceDAOImplTest {

    private ServiceDAOImpl dao;
    private MockedStatic<DatabaseFactory> mockedFactory;
    private DatabaseFactory mockFactoryInstance;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        dao = new ServiceDAOImpl();
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

    private void stubServiceResultSet() throws SQLException {
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("Test Service");
        when(mockResultSet.getString("description")).thenReturn("Test Description");
        when(mockResultSet.getInt("estimated_time")).thenReturn(15);
        when(mockResultSet.getBoolean("active")).thenReturn(true);
    }

    // ===== CREATE =====
    @Test
    void createReturnsGeneratedId() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(42);

        Service service = new Service();
        service.setName("Test Service");
        service.setDescription("Test Description");
        service.setEstimatedTime(15);
        service.setActive(true);

        int id = dao.create(service);
        assertEquals(42, id);
    }

    @Test
    void createThrowsWhenNoRowsAffected() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        Service service = new Service();
        DAOException ex = assertThrows(DAOException.class, () -> dao.create(service));
        assertTrue(ex.getMessage().contains("no rows affected"));
    }

    @Test
    void createThrowsWhenNoIdGenerated() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Service service = new Service();
        DAOException ex = assertThrows(DAOException.class, () -> dao.create(service));
        assertTrue(ex.getMessage().contains("no ID obtained"));
    }

    @Test
    void createThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenThrow(new SQLException("DB error"));

        Service service = new Service();
        assertThrows(DAOException.class, () -> dao.create(service));
    }

    // ===== FIND BY ID =====
    @Test
    void findByIdReturnsService() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        stubServiceResultSet();

        Service result = dao.findById(1);
        assertNotNull(result);
        assertEquals("Test Service", result.getName());
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

    // ===== FIND BY NAME =====
    @Test
    void findByNameReturnsService() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        stubServiceResultSet();

        Service result = dao.findByName("Test Service");
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void findByNameReturnsNullWhenNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertNull(dao.findByName("Unknown"));
    }

    @Test
    void findByNameThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.findByName("Test"));
    }

    // ===== UPDATE =====
    @Test
    void updateReturnsTrue() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Service service = new Service();
        service.setId(1);
        service.setName("Updated Service");

        assertTrue(dao.update(service));
    }

    @Test
    void updateReturnsFalseWhenNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        Service service = new Service();
        service.setId(999);

        assertFalse(dao.update(service));
    }

    @Test
    void updateThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        Service service = new Service();
        service.setId(1);

        assertThrows(DAOException.class, () -> dao.update(service));
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
    void findAllReturnsListOfServices() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        stubServiceResultSet();

        List<Service> result = dao.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void findAllReturnsEmptyListWhenNoData() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        List<Service> result = dao.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.findAll());
    }

    // ===== FIND ALL ACTIVE SERVICES =====
    @Test
    void findAllActiveReturnsListOfActiveServices() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        stubServiceResultSet();

        List<Service> result = dao.findAllActive();
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
    }

    @Test
    void findAllActiveReturnsEmptyListWhenNoActiveServices() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        List<Service> result = dao.findAllActive();
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllActiveThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.findAllActive());
    }

    // ===== SAVE (create vs update) =====
    @Test
    void saveCallsCreateWhenIdIsZero() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(10);

        Service service = new Service();
        service.setId(0);
        service.setName("New Service");

        int result = dao.save(service);
        assertEquals(10, result);
    }

    @Test
    void saveCallsUpdateWhenIdIsNotZero() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Service service = new Service();
        service.setId(5);
        service.setName("Existing Service");

        dao.save(service);
        verify(mockPreparedStatement).executeUpdate();
    }
}
