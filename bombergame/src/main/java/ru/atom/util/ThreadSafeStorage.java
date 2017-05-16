package ru.atom.util;

import ru.atom.gamemechanics.highintities.GameSession;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by sergey on 3/15/17.
 */
public class ThreadSafeStorage {
    private static ConcurrentHashMap<Long, GameSession> map = new ConcurrentHashMap<>();
    private static AtomicLong gameSessionIdGenerator = new AtomicLong();
    public static final Object lock = new Object();

    public static void put(GameSession session) {
        synchronized (lock) {
            map.put(gameSessionIdGenerator.getAndIncrement(), session);
        }
    }

    public static Collection<GameSession> getAll() {
        synchronized (lock) {
            return map.values();
        }
    }

    public static long getCurrentGameSessionId() {
        synchronized (lock) {
            return gameSessionIdGenerator.get();
        }
    }
}
