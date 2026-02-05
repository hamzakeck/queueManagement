package websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

// Simple WebSocket for real-time updates
@ServerEndpoint("/queue-updates")
public class QueueWebSocket {

    private static final Logger LOGGER = Logger.getLogger(QueueWebSocket.class.getName());

    // List of all connected users
    private static List<Session> allSessions = new ArrayList<>();

    // When someone connects
    @OnOpen
    public void onOpen(Session session) {
        allSessions.add(session);
        LOGGER.log(Level.INFO, "New connection! Total: {0}", allSessions.size());
    }

    // When someone disconnects
    @OnClose
    public void onClose(Session session) {
        allSessions.remove(session);
        LOGGER.log(Level.INFO, "Connection closed. Total: {0}", allSessions.size());
    }

    // If there's an error
    @OnError
    public void onError(Session session, Throwable error) {
        LOGGER.log(Level.WARNING, "WebSocket error: {0}", error.getMessage());
        allSessions.remove(session);
    }

    // Send update to everyone
    public static void sendUpdateToEveryone(String message) {
        LOGGER.log(Level.FINE, "Sending to everyone: {0}", message);

        for (int i = 0; i < allSessions.size(); i++) {
            Session session = allSessions.get(i);
            try {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(message);
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Could not send to session", e);
            }
        }
    }
}
