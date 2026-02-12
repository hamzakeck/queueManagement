package dao.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class DatabaseFactoryTest {

    @Test
    void getInstanceReturnsNonNull() {
        DatabaseFactory factory = DatabaseFactory.getInstance();
        assertNotNull(factory);
    }

    @Test
    void getInstanceReturnsSameInstance() {
        DatabaseFactory first = DatabaseFactory.getInstance();
        DatabaseFactory second = DatabaseFactory.getInstance();
        assertSame(first, second, "DatabaseFactory should return the same singleton instance");
    }

    @Test
    void getInstanceIsThreadSafe() throws InterruptedException {
        DatabaseFactory[] results = new DatabaseFactory[2];
        Thread t1 = new Thread(() -> results[0] = DatabaseFactory.getInstance());
        Thread t2 = new Thread(() -> results[1] = DatabaseFactory.getInstance());
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        assertNotNull(results[0]);
        assertSame(results[0], results[1], "Concurrent calls should return the same instance");
    }
}
