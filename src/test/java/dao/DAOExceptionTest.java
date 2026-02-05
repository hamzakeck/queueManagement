package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class DAOExceptionTest {

    @Test
    void constructorWithMessageOnly() {
        String message = "Test error message";
        DAOException exception = new DAOException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void constructorWithMessageAndCause() {
        String message = "Test error message";
        Throwable cause = new RuntimeException("Root cause");
        DAOException exception = new DAOException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void constructorWithCauseOnly() {
        Throwable cause = new RuntimeException("Root cause");
        DAOException exception = new DAOException(cause);
        
        assertEquals(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("Root cause"));
    }

    @Test
    void exceptionIsThrowable() {
        DAOException exception = new DAOException("Test");
        assertTrue(exception instanceof Exception);
    }

    @Test
    void exceptionCanBeCaught() {
        assertThrows(DAOException.class, () -> {
            throw new DAOException("Test exception");
        });
    }

    @Test
    void exceptionWithNullMessage() {
        DAOException exception = new DAOException((String) null);
        assertNull(exception.getMessage());
    }

    @Test
    void exceptionWithEmptyMessage() {
        DAOException exception = new DAOException("");
        assertEquals("", exception.getMessage());
    }

    @Test
    void chainedExceptions() {
        Exception root = new RuntimeException("Root");
        Exception middle = new Exception("Middle", root);
        DAOException top = new DAOException("Top", middle);
        
        assertEquals("Top", top.getMessage());
        assertEquals(middle, top.getCause());
        assertEquals(root, top.getCause().getCause());
    }
}
