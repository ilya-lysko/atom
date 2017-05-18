package ru.atom.network;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import ru.atom.gamemechanics.entities.Action;
import ru.atom.matchmaker.MatchMaker;
import ru.atom.util.JsonHelper;

import java.io.IOException;

import static ru.atom.gamemechanics.highintities.GameSession.playersActions;

/**
 * Created by kinetik on 02.05.17.
 */
public class Broker {
    private static final Logger log = LogManager.getLogger(Broker.class);

    private static final Broker instance = new Broker();
    private final ConnectionPool connectionPool;

    public static Broker getInstance() {
        return instance;
    }

    private Broker() {
        this.connectionPool = ConnectionPool.getInstance();
    }

    public void receive(Session session, @NotNull String msg) {
        log.info("RECEIVED: " + msg);
        Message message = JsonHelper.fromJson(msg, Message.class);
        Topic topic = message.getTopic();
        if (topic.equals(Topic.MOVE)) {
            try {
                DirectionMessage directionMessage = JsonHelper.fromJson(message.getData(), DirectionMessage.class);
                Action action = new Action(Action.Type.MOVE, ConnectionPool.getInstance().getPlayer(session));
                action.setDirection(directionMessage.getDirection());
            } catch (EnumConstantNotPresentException ex) {
                log.error("Bad direction");
            }
        } else if (topic.equals(Topic.PLANT_BOMB)) {
            playersActions.add(new Action(Action.Type.PLANT, ConnectionPool.getInstance().getPlayer(session )));
        } else {
            log.error("Bad data");
        }
    }

    public void send(@NotNull String player, @NotNull Topic topic, @NotNull Object object) {
        String message = JsonHelper.toJson(new Message(topic, JsonHelper.toJson(object)));
        Session session = connectionPool.getSession(player);
        connectionPool.send(session, message);
    }

    public void broadcast(@NotNull Topic topic, @NotNull Object object) throws IOException {
        String message = JsonHelper.toJson(new Message(topic, object.toString()));
        connectionPool.broadcast(message);
    }
}
