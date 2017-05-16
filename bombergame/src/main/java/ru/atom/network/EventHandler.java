package ru.atom.network;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

/**
 * Created by vladfedorenko on 02.05.17.
 */

public class EventHandler extends WebSocketAdapter {
    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        //ConnectionPool.getInstance().add(sess, playerToken);
        System.out.println("Socket Connected: " + sess);
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        Broker.getInstance().receive(super.getSession(), message);
        System.out.println("Received TEXT message: " + message);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }
}
