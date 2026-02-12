package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

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
import models.Employee;

class EmployeeDAOImplTest {

    private EmployeeDAOImpl dao;
    private MockedStatic<DatabaseFactory> mockedFactory;
    private DatabaseFactory mockFactoryInstance;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        dao = new EmployeeDAOImpl();
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

    private void stubEmployeeResultSet() throws SQLException {
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("first_name")).thenReturn("John");
        when(mockResultSet.getString("last_name")).thenReturn("Doe");
        when(mockResultSet.getString("email")).thenReturn("john@test.com");
        when(mockResultSet.getString("password")).thenReturn("hashedpassword");
        when(mockResultSet.getInt("agency_id")).thenReturn(1);
        when(mockResultSet.getInt("counter_id")).thenReturn(2);
        when(mockResultSet.wasNull()).thenReturn(false);
        when(mockResultSet.getInt("service_id")).thenReturn(3);
    }

    @Test
    void createReturnsGeneratedId() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(42);

        Employee employee = new Employee();
        employee.setCounterId(2);
        assertEquals(42, dao.create(employee));
    }

    @Test
    void createWithNullCounterId() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(42);

        Employee employee = new Employee();
        employee.setCounterId(0);
        dao.create(employee);
        // counter_id is at position 7 in create
        verify(mockPreparedStatement).setNull(eq(7), eq(Types.INTEGER));
    }

    @Test
    void createThrowsWhenNoRowsAffected() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        DAOException ex = assertThrows(DAOException.class, () -> dao.create(new Employee()));
        assertTrue(ex.getMessage().contains("no rows affected"));
    }

    @Test
    void createThrowsWhenNoIdGenerated() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        DAOException ex = assertThrows(DAOException.class, () -> dao.create(new Employee()));
        assertTrue(ex.getMessage().contains("no ID obtained"));
    }

    @Test
    void createThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.create(new Employee()));
    }

    @Test
    void findByIdReturnsEmployee() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        stubEmployeeResultSet();

        Employee result = dao.findById(1);
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
    void findByEmailReturnsEmployee() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        stubEmployeeResultSet();
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
        Employee employee = new Employee();
        employee.setId(1);
        employee.setCounterId(2);
        assertTrue(dao.update(employee));
    }

    @Test
    void updateWithNullCounterId() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        Employee employee = new Employee();
        employee.setId(1);
        employee.setCounterId(0);
        assertTrue(dao.update(employee));
        // counter_id is at position 7 in update
        verify(mockPreparedStatement).setNull(eq(7), eq(Types.INTEGER));
    }

    @Test
    void updateReturnsFalseWhenNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);
        Employee employee = new Employee();
        employee.setId(999);
        assertFalse(dao.update(employee));
    }

    @Test
    void updateThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        Employee employee = new Employee();
        employee.setId(1);
        assertThrows(DAOException.class, () -> dao.update(employee));
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
    void findAllReturnsListOfEmployees() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        stubEmployeeResultSet();
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
    void findByAgencyReturnsListOfEmployees() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        stubEmployeeResultSet();
        assertEquals(1, dao.findByAgency(1).size());
    }

    @Test
    void findByAgencyReturnsEmptyListWhenNoEmployees() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        assertTrue(dao.findByAgency(999).isEmpty());
    }

    @Test
    void findByAgencyThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));
        assertThrows(DAOException.class, () -> dao.findByAgency(1));
    }

    @Test
    void authenticateReturnsEmployeeOnSuccess() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        stubEmployeeResultSet();
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
    void findByIdHandlesNullCounter() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("first_name")).thenReturn("John");
        when(mockResultSet.getString("last_name")).thenReturn("Doe");
        when(mockResultSet.getString("email")).thenReturn("john@test.com");
        when(mockResultSet.getString("password")).thenReturn("hashedpassword");
        when(mockResultSet.getInt("agency_id")).thenReturn(1);
        when(mockResultSet.getInt("counter_id")).thenReturn(0);
        when(mockResultSet.wasNull()).thenReturn(true);
        when(mockResultSet.getInt("service_id")).thenReturn(3);

        Employee result = dao.findById(1);
        assertNotNull(result);
        assertEquals(0, result.getCounterId());
    }
}
