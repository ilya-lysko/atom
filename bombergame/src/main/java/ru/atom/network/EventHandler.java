package ru.atom.network;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.atom.gamemechanics.highintities.GameSession;
import ru.atom.matchmaker.MatchMaker;

import java.io.IOException;

/**
 * Created by vladfedorenko on 02.05.17.
 */

public class EventHandler extends WebSocketAdapter {
    private static final Logger log = LogManager.getLogger(EventHandler.class);

    private static GameSession gameSession = new GameSession();

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        int playerId = MatchMaker.getGameSession().getPlayerCountAndIncrement() + 666;
        if (playerId <= 669) {
            String playerName = "Player_" + playerId;
            ConnectionPool.getInstance().add(super.getSession(), playerName);
            Broker.getInstance().send(playerName, Topic.POSSESS, playerId);
            log.info("POSSES sended to " + playerName);
            try {
                gameSession.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info("Socket Connected: {}", super.getSession());
        } else {
            log.info("Only 4 players can be connected");
            super.getSession().close();
        }
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        log.info("Received TEXT message: " + message);
        Broker.getInstance().receive(super.getSession(), message);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        log.info("Socket Closed: [" + statusCode + "] " + reason);
        ConnectionPool.getInstance().remove(super.getSession());
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
        log.warn(cause);
    }
}
