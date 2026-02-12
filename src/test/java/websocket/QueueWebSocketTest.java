package websocket;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Extension;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

class QueueWebSocketTest {

    private QueueWebSocket webSocket;

    @BeforeEach
    void setUp() throws Exception {
        webSocket = new QueueWebSocket();
        clearSessions();
    }

    @AfterEach
    void tearDown() throws Exception {
        clearSessions();
    }

    private void clearSessions() throws Exception {
        Field field = QueueWebSocket.class.getDeclaredField("allSessions");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<Session> sessions = (List<Session>) field.get(null);
        sessions.clear();
    }

    @SuppressWarnings("unchecked")
    private List<Session> getSessions() throws Exception {
        Field field = QueueWebSocket.class.getDeclaredField("allSessions");
        field.setAccessible(true);
        return (List<Session>) field.get(null);
    }

    @Test
    void onOpenAddsSession() throws Exception {
        StubSession session = new StubSession(true);

        webSocket.onOpen(session);

        assertTrue(getSessions().contains(session), "Session should be in the list after onOpen");
    }

    @Test
    void onCloseRemovesSession() throws Exception {
        StubSession session = new StubSession(true);
        webSocket.onOpen(session);

        webSocket.onClose(session);

        assertFalse(getSessions().contains(session), "Session should be removed after onClose");
    }

    @Test
    void onErrorRemovesSession() throws Exception {
        StubSession session = new StubSession(true);
        webSocket.onOpen(session);

        webSocket.onError(session, new RuntimeException("Test error"));

        assertFalse(getSessions().contains(session), "Session should be removed after onError");
    }

    @Test
    void sendUpdateToEveryoneBroadcastsToOpenSessions() throws Exception {
        StubSession session1 = new StubSession(true);
        StubSession session2 = new StubSession(true);

        webSocket.onOpen(session1);
        webSocket.onOpen(session2);

        QueueWebSocket.sendUpdateToEveryone("queue_update");

        assertEquals("queue_update", session1.getBasicRemoteStub().getLastSentText());
        assertEquals("queue_update", session2.getBasicRemoteStub().getLastSentText());
    }

    @Test
    void sendUpdateSkipsClosedSessions() throws Exception {
        StubSession openSession = new StubSession(true);
        StubSession closedSession = new StubSession(false);

        webSocket.onOpen(openSession);
        webSocket.onOpen(closedSession);

        QueueWebSocket.sendUpdateToEveryone("update");

        assertEquals("update", openSession.getBasicRemoteStub().getLastSentText());
        assertEquals(null, closedSession.getBasicRemoteStub().getLastSentText());
    }

    @Test
    void sendUpdateHandlesIOException() throws Exception {
        StubSession session = new StubSession(true);
        session.getBasicRemoteStub().setThrowOnSend(true);

        webSocket.onOpen(session);

        assertDoesNotThrow(() -> QueueWebSocket.sendUpdateToEveryone("test"));
    }

    @Test
    void sendUpdateWithEmptySessionList() {
        assertDoesNotThrow(() -> QueueWebSocket.sendUpdateToEveryone("test"));
    }

    @Test
    void onOpenIncreasesSessionCount() throws Exception {
        StubSession s1 = new StubSession(true);
        StubSession s2 = new StubSession(true);

        webSocket.onOpen(s1);
        assertEquals(1, getSessions().size());

        webSocket.onOpen(s2);
        assertEquals(2, getSessions().size());
    }

    // ---- Stub implementations to avoid Mockito module issues on Java 25 ----

    /** Minimal stub for RemoteEndpoint.Basic that records sent text */
    private static class StubBasicRemote implements RemoteEndpoint.Basic {
        private String lastSentText;
        private boolean throwOnSend;

        String getLastSentText() {
            return lastSentText;
        }

        void setThrowOnSend(boolean flag) {
            this.throwOnSend = flag;
        }

        @Override
        public void sendText(String text) throws IOException {
            if (throwOnSend)
                throw new IOException("Stub send failure");
            this.lastSentText = text;
        }

        @Override
        public void sendBinary(java.nio.ByteBuffer data) {
            // Stub - binary sending not needed for text-based WebSocket tests
        }

        @Override
        public void sendObject(Object data) throws jakarta.websocket.EncodeException {
            // Stub - object encoding not used in these tests
        }

        @Override
        public void sendText(String partialMessage, boolean isLast) {
            // Stub - partial text sending not needed for test scenarios
        }

        @Override
        public void sendBinary(java.nio.ByteBuffer partialByte, boolean isLast) {
            // Stub - partial binary sending not needed for test scenarios
        }

        @Override
        public void setBatchingAllowed(boolean allowed) {
            // Stub implementation for testing - method not needed for test scenarios
        }

        @Override
        public boolean getBatchingAllowed() {
            return false;
        }

        @Override
        public void flushBatch() {
            // Stub - batch flushing not needed for test scenarios
        }

        @Override
        public void sendPing(java.nio.ByteBuffer applicationData) {
            // Stub - ping/pong not used in these tests
        }

        @Override
        public void sendPong(java.nio.ByteBuffer applicationData) {
            // Stub - ping/pong not used in these tests
        }

        @Override
        public java.io.OutputStream getSendStream() {
            return null;
        }

        @Override
        public java.io.Writer getSendWriter() {
            return null;
        }
    }

    /**
     * Minimal stub for jakarta.websocket.Session with only isOpen and
     * getBasicRemote implemented
     */
    private static class StubSession implements Session {
        private final boolean open;
        private final StubBasicRemote basicRemote = new StubBasicRemote();

        StubSession(boolean open) {
            this.open = open;
        }

        StubBasicRemote getBasicRemoteStub() {
            return basicRemote;
        }

        @Override
        public boolean isOpen() {
            return open;
        }

        @Override
        public RemoteEndpoint.Basic getBasicRemote() {
            return basicRemote;
        }

        // Unused methods — return defaults
        @Override
        public WebSocketContainer getContainer() {
            return null;
        }

        @Override
        public void addMessageHandler(MessageHandler handler) {
            // Stub - message handlers not needed for send-only tests
        }

        @Override
        public Set<MessageHandler> getMessageHandlers() {
            return Set.of();
        }

        @Override
        public void removeMessageHandler(MessageHandler handler) {
            // Stub - message handlers not used in these tests
        }

        @Override
        public String getProtocolVersion() {
            return "";
        }

        @Override
        public String getNegotiatedSubprotocol() {
            return "";
        }

        @Override
        public List<Extension> getNegotiatedExtensions() {
            return List.of();
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public long getMaxIdleTimeout() {
            return 0;
        }

        @Override
        public void setMaxIdleTimeout(long ms) {
            // Stub - timeout configuration not needed for test scenarios
        }

        @Override
        public void setMaxBinaryMessageBufferSize(int length) {
            // Stub - buffer size configuration not needed for tests
        }

        @Override
        public int getMaxBinaryMessageBufferSize() {
            return 0;
        }

        @Override
        public void setMaxTextMessageBufferSize(int length) {
            // Stub - buffer size configuration not needed for tests
        }

        @Override
        public int getMaxTextMessageBufferSize() {
            return 0;
        }

        @Override
        public RemoteEndpoint.Async getAsyncRemote() {
            return null;
        }

        @Override
        public String getId() {
            return "stub-" + hashCode();
        }

        @Override
        public void close() {
            // Stub - close handling not needed for these tests
        }

        @Override
        public void close(CloseReason closeReason) {
            // Stub - close handling not needed for these tests
        }

        @Override
        public URI getRequestURI() {
            return null;
        }

        @Override
        public Map<String, List<String>> getRequestParameterMap() {
            return Map.of();
        }

        @Override
        public String getQueryString() {
            return "";
        }

        @Override
        public Map<String, String> getPathParameters() {
            return Map.of();
        }

        @Override
        public Map<String, Object> getUserProperties() {
            return Map.of();
        }

        @Override
        public Principal getUserPrincipal() {
            return null;
        }

        @Override
        public Set<Session> getOpenSessions() {
            return Set.of();
        }

        @Override
        public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Whole<T> handler) {
            // Stub - typed message handlers not used in these tests
        }

        @Override
        public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Partial<T> handler) {
            // Stub - typed partial handlers not used in these tests
        }
    }
}
