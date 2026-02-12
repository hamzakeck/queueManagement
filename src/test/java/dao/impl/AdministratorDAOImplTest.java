package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
import models.Administrator;

class AdministratorDAOImplTest {

    private AdministratorDAOImpl dao;
    private MockedStatic<DatabaseFactory> mockedFactory;
    private DatabaseFactory mockFactoryInstance;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        dao = new AdministratorDAOImpl();
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

    @Test
    void createReturnsGeneratedId() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(42);

        Administrator admin = new Administrator();
        admin.setFirstName("John");
        admin.setLastName("Doe");
        admin.setEmail("john@test.com");
        admin.setPassword("pass");

        int id = dao.create(admin);

        assertEquals(42, id);
        verify(mockPreparedStatement).setString(1, "John");
        verify(mockPreparedStatement).setString(2, "Doe");
        verify(mockPreparedStatement).setString(3, "john@test.com");
        verify(mockPreparedStatement).setString(4, "pass");
    }

    @Test
    void createThrowsWhenNoRowsAffected() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        Administrator admin = new Administrator();
        admin.setFirstName("A");
        admin.setLastName("B");
        admin.setEmail("a@b.com");
        admin.setPassword("p");

        DAOException ex = assertThrows(DAOException.class, () -> dao.create(admin));
        assertTrue(ex.getMessage().contains("no rows affected"));
    }

    @Test
    void createThrowsWhenNoIdGenerated() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Administrator admin = new Administrator();
        admin.setFirstName("A");
        admin.setLastName("B");
        admin.setEmail("a@b.com");
        admin.setPassword("p");

        DAOException ex = assertThrows(DAOException.class, () -> dao.create(admin));
        assertTrue(ex.getMessage().contains("no ID obtained"));
    }

    @Test
    void createThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenThrow(new SQLException("DB error"));

        Administrator admin = new Administrator();
        admin.setFirstName("A");
        admin.setLastName("B");
        admin.setEmail("a@b.com");
        admin.setPassword("p");

        DAOException ex = assertThrows(DAOException.class, () -> dao.create(admin));
        assertTrue(ex.getMessage().contains("Error creating administrator"));
    }

    @Test
    void findByIdReturnsAdmin() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("first_name")).thenReturn("John");
        when(mockResultSet.getString("last_name")).thenReturn("Doe");
        when(mockResultSet.getString("email")).thenReturn("john@test.com");
        when(mockResultSet.getString("password")).thenReturn("pass");
        when(mockResultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

        Administrator result = dao.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John", result.getFirstName());
        verify(mockPreparedStatement).setInt(1, 1);
    }

    @Test
    void findByIdReturnsNullWhenNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Administrator result = dao.findById(999);

        assertNull(result);
    }

    @Test
    void findByIdThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Connection failed"));

        assertThrows(DAOException.class, () -> dao.findById(1));
    }

    @Test
    void findByEmailReturnsAdmin() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("first_name")).thenReturn("Jane");
        when(mockResultSet.getString("last_name")).thenReturn("Doe");
        when(mockResultSet.getString("email")).thenReturn("jane@test.com");
        when(mockResultSet.getString("password")).thenReturn("pass");
        when(mockResultSet.getTimestamp("created_at")).thenReturn(null);

        Administrator result = dao.findByEmail("jane@test.com");

        assertNotNull(result);
        assertEquals("jane@test.com", result.getEmail());
        assertNull(result.getCreatedAt());
    }

    @Test
    void findByEmailReturnsNullWhenNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertNull(dao.findByEmail("notfound@test.com"));
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

        Administrator admin = new Administrator();
        admin.setId(1);
        admin.setFirstName("Updated");
        admin.setLastName("Admin");
        admin.setEmail("updated@test.com");
        admin.setPassword("newpass");

        boolean result = dao.update(admin);

        assertTrue(result);
        verify(mockPreparedStatement).setString(1, "Updated");
        verify(mockPreparedStatement).setInt(5, 1);
    }

    @Test
    void updateReturnsFalseWhenNoRowsAffected() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        Administrator admin = new Administrator();
        admin.setId(999);

        boolean result = dao.update(admin);

        assertFalse(result);
    }

    @Test
    void updateThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Update failed"));

        Administrator admin = new Administrator();
        admin.setId(1);

        assertThrows(DAOException.class, () -> dao.update(admin));
    }

    @Test
    void deleteReturnsTrue() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = dao.delete(1);

        assertTrue(result);
        verify(mockPreparedStatement).setInt(1, 1);
    }

    @Test
    void deleteReturnsFalseWhenNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        boolean result = dao.delete(999);

        assertFalse(result);
    }

    @Test
    void deleteThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.delete(1));
    }

    @Test
    void findAllReturnsListOfAdmins() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("first_name")).thenReturn("Admin1", "Admin2");
        when(mockResultSet.getString("last_name")).thenReturn("Last1", "Last2");
        when(mockResultSet.getString("email")).thenReturn("a1@test.com", "a2@test.com");
        when(mockResultSet.getString("password")).thenReturn("p1", "p2");
        when(mockResultSet.getTimestamp("created_at")).thenReturn(null);

        List<Administrator> result = dao.findAll();

        assertEquals(2, result.size());
        assertEquals("Admin1", result.get(0).getFirstName());
        assertEquals("Admin2", result.get(1).getFirstName());
    }

    @Test
    void findAllReturnsEmptyListWhenNoAdmins() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        List<Administrator> result = dao.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.findAll());
    }

    @Test
    void authenticateReturnsAdminOnSuccess() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("first_name")).thenReturn("John");
        when(mockResultSet.getString("last_name")).thenReturn("Doe");
        when(mockResultSet.getString("email")).thenReturn("john@test.com");
        when(mockResultSet.getString("password")).thenReturn("pass");
        when(mockResultSet.getTimestamp("created_at")).thenReturn(null);

        Administrator result = dao.authenticate("john@test.com", "pass");

        assertNotNull(result);
        verify(mockPreparedStatement).setString(1, "john@test.com");
        verify(mockPreparedStatement).setString(2, "pass");
    }

    @Test
    void authenticateReturnsNullOnFailure() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Administrator result = dao.authenticate("wrong@test.com", "wrong");

        assertNull(result);
    }

    @Test
    void authenticateThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.authenticate("a@b.com", "p"));
    }

    @Test
    void emailExistsReturnsTrue() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        boolean result = dao.emailExists("exists@test.com");

        assertTrue(result);
    }

    @Test
    void emailExistsReturnsFalse() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(0);

        boolean result = dao.emailExists("notexists@test.com");

        assertFalse(result);
    }

    @Test
    void emailExistsReturnsFalseWhenNoResultRow() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        boolean result = dao.emailExists("test@test.com");

        assertFalse(result);
    }

    @Test
    void emailExistsThrowsDAOExceptionOnSQLError() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(DAOException.class, () -> dao.emailExists("test@test.com"));
    }
}
