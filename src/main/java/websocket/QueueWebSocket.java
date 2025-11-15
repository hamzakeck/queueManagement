package websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Simple WebSocket for real-time updates
@ServerEndpoint("/queue-updates")
public class QueueWebSocket {

    // List of all connected users
    private static List<Session> allSessions = new ArrayList<>();

    // When someone connects
    @OnOpen
    public void onOpen(Session session) {
        allSessions.add(session);
        System.out.println("New connection! Total: " + allSessions.size());
    }

    // When someone disconnects
    @OnClose
    public void onClose(Session session) {
        allSessions.remove(session);
        System.out.println("Connection closed. Total: " + allSessions.size());
    }

    // If there's an error
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("Error: " + error.getMessage());
        allSessions.remove(session);
    }

    // Send update to everyone
    public static void sendUpdateToEveryone(String message) {
        System.out.println("Sending to everyone: " + message);

        for (int i = 0; i < allSessions.size(); i++) {
            Session session = allSessions.get(i);
            try {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(message);
                }
            } catch (IOException e) {
                System.out.println("Could not send to session");
            }
        }
    }
}
