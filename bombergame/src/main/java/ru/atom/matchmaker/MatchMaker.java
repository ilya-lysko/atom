package ru.atom.matchmaker;

import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.atom.gamemechanics.highintities.GameSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import ru.atom.util.ThreadSafeQueue;
import ru.atom.util.ThreadSafeStorage;

/**
 * Created by kinetik on 12.05.17.
 */
public class MatchMaker implements Runnable {
    private static final Logger log = LogManager.getLogger(MatchMaker.class);
    private static GameSession gameSession = new GameSession();

    public static GameSession getGameSession() {
        return gameSession;
    }

    @Override
    public void run() {
        log.info("Started");
        List<String> candidates = new ArrayList<>(GameSession.PLAYERS_IN_GAME);
        while (!Thread.currentThread().isInterrupted()) {
            try {
                candidates.add(
                        ThreadSafeQueue.getInstance().poll(10_000, TimeUnit.SECONDS)
                );
            } catch (InterruptedException e) {
                log.warn("Timeout reached");
            }

            if (candidates.size() == GameSession.PLAYERS_IN_GAME) {
//                gameSession.start();
//                log.info("Game started!");
                ThreadSafeStorage.put(gameSession);

                candidates.clear();
            }
        }
    }
}
