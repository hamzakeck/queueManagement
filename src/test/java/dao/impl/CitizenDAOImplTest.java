package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
import models.Citizen;

class CitizenDAOImplTest {

    private CitizenDAOImpl dao;
    private MockedStatic<DatabaseFactory> mockedFactory;
    private DatabaseFactory mockFactoryInstance;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        dao = new CitizenDAOImpl();
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

    private void stubCitizenResultSet() throws SQLException {
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("cin")).thenReturn("AB123456");
        when(mockResultSet.getString("first_name")).thenReturn("John");
        when(mockResultSet.getString("last_name")).thenReturn("Doe");
        when(mockResultSet.getString("email")).thenReturn("john@test.com");
        when(mockResultSet.getString("password")).thenReturn("hashedpassword");
        when(mockResultSet.getString("phone")).thenReturn("555-1234");
    }

    @Test
    void createReturnsGeneratedId() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(42);

        Citizen citizen = new Citizen();
        int id = dao.create(citizen);
        assertEquals(42, id);
    }

    @Test
    void createThrowsWhenNoRowsAffected() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        DAOException ex = assertThrows(DAOException.class, () -> dao.create(new Citizen()));
        assertTrue(ex.getMessage().contains("no rows affected"));
    }

    @Test
    void createThrowsWhenNoIdGenerated() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        DAOException ex = assertThrows(DAOException.class, () -> dao.create(new Citizen()));
        assertTrue(ex.getMessage().contains("no ID obtained"));
    }

    @Test
    void createThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.create(new Citizen()));
    }

    @Test
    void findByIdReturnsCitizen() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        stubCitizenResultSet();

        Citizen result = dao.findById(1);
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
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

    @Test
    void findByCinReturnsCitizen() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        stubCitizenResultSet();

        Citizen result = dao.findByCin("AB123456");
        assertNotNull(result);
    }

    @Test
    void findByCinReturnsNullWhenNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        assertNull(dao.findByCin("UNKNOWN"));
    }

    @Test
    void findByCinThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.findByCin("AB123456"));
    }

    @Test
    void findByEmailReturnsCitizen() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        stubCitizenResultSet();
        assertNotNull(dao.findByEmail("john@test.com"));
    }

    @Test
    void findByEmailReturnsNullWhenNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        assertNull(dao.findByEmail("unknown@test.com"));
    }

    @Test
    void findByEmailThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.findByEmail("test@test.com"));
    }

    @Test
    void updateReturnsTrue() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        Citizen citizen = new Citizen();
        citizen.setId(1);
        assertTrue(dao.update(citizen));
    }

    @Test
    void updateReturnsFalseWhenNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);
        Citizen citizen = new Citizen();
        citizen.setId(999);
        assertFalse(dao.update(citizen));
    }

    @Test
    void updateThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        Citizen citizen = new Citizen();
        citizen.setId(1);
        assertThrows(DAOException.class, () -> dao.update(citizen));
    }

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

    @Test
    void findAllReturnsListOfCitizens() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        stubCitizenResultSet();
        assertEquals(2, dao.findAll().size());
    }

    @Test
    void findAllReturnsEmptyListWhenNoData() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        assertTrue(dao.findAll().isEmpty());
    }

    @Test
    void findAllThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.findAll());
    }

    @Test
    void authenticateReturnsCitizenOnSuccess() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        stubCitizenResultSet();
        assertNotNull(dao.authenticate("john@test.com", "hashedpassword"));
    }

    @Test
    void authenticateReturnsNullOnFailure() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        assertNull(dao.authenticate("john@test.com", "wrongpassword"));
    }

    @Test
    void authenticateThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.authenticate("test@test.com", "pass"));
    }

    @Test
    void emailExistsReturnsTrue() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);
        assertTrue(dao.emailExists("john@test.com"));
    }

    @Test
    void emailExistsReturnsFalse() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(0);
        assertFalse(dao.emailExists("unknown@test.com"));
    }

    @Test
    void emailExistsThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.emailExists("test@test.com"));
    }

    @Test
    void cinExistsReturnsTrue() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);
        assertTrue(dao.cinExists("AB123456"));
    }

    @Test
    void cinExistsReturnsFalse() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(0);
        assertFalse(dao.cinExists("UNKNOWN"));
    }

    @Test
    void cinExistsThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.cinExists("AB123456"));
    }
}
