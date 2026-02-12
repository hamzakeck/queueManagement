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
import models.Agency;

class AgencyDAOImplTest {

    private AgencyDAOImpl dao;
    private MockedStatic<DatabaseFactory> mockedFactory;
    private DatabaseFactory mockFactoryInstance;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        dao = new AgencyDAOImpl();
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

    private void stubAgencyResultSet() throws SQLException {
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("Test Agency");
        when(mockResultSet.getString("address")).thenReturn("123 Street");
        when(mockResultSet.getString("city")).thenReturn("City");
        when(mockResultSet.getString("phone")).thenReturn("555-1234");
        when(mockResultSet.getInt("total_counters")).thenReturn(5);
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

        Agency agency = new Agency();
        agency.setName("Test Agency");
        agency.setAddress("123 Street");
        agency.setCity("City");
        agency.setPhone("555-1234");
        agency.setTotalCounters(5);

        int id = dao.create(agency);
        assertEquals(42, id);
    }

    @Test
    void createThrowsWhenNoRowsAffected() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        Agency agency = new Agency();
        DAOException ex = assertThrows(DAOException.class, () -> dao.create(agency));
        assertTrue(ex.getMessage().contains("no rows affected"));
    }

    @Test
    void createThrowsWhenNoIdGenerated() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Agency agency = new Agency();
        DAOException ex = assertThrows(DAOException.class, () -> dao.create(agency));
        assertTrue(ex.getMessage().contains("no ID obtained"));
    }

    @Test
    void createThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenThrow(new SQLException("DB error"));

        Agency agency = new Agency();
        assertThrows(DAOException.class, () -> dao.create(agency));
    }

    // ===== FIND BY ID =====
    @Test
    void findByIdReturnsAgency() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        stubAgencyResultSet();

        Agency result = dao.findById(1);
        assertNotNull(result);
        assertEquals("Test Agency", result.getName());
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
    void findByNameReturnsAgency() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        stubAgencyResultSet();

        Agency result = dao.findByName("Test Agency");
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

        Agency agency = new Agency();
        agency.setId(1);
        agency.setName("Updated Agency");

        assertTrue(dao.update(agency));
    }

    @Test
    void updateReturnsFalseWhenNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        Agency agency = new Agency();
        agency.setId(999);

        assertFalse(dao.update(agency));
    }

    @Test
    void updateThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        Agency agency = new Agency();
        agency.setId(1);

        assertThrows(DAOException.class, () -> dao.update(agency));
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
    void findAllReturnsListOfAgencies() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        stubAgencyResultSet();

        List<Agency> result = dao.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void findAllReturnsEmptyListWhenNoData() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        List<Agency> result = dao.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.findAll());
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

        Agency agency = new Agency();
        agency.setId(0);
        agency.setName("New Agency");

        int result = dao.save(agency);
        assertEquals(10, result);
    }

    @Test
    void saveCallsUpdateWhenIdIsNotZero() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Agency agency = new Agency();
        agency.setId(5);
        agency.setName("Existing Agency");

        dao.save(agency);
        verify(mockPreparedStatement).executeUpdate();
    }
}
