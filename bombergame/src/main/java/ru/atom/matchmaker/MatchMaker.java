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

/**
 * Created by kinetik on 12.05.17.
 */
public class MatchMaker implements Runnable {
    private static final Logger log = LogManager.getLogger(MatchMakerServlet.class);

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
                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                RequestBody body = RequestBody.create(
                        mediaType,
                        String.format("token=%s", candidates)
                );

                String requestUrl = "localhost:8090";
                Request request = new Request.Builder()
                        .url(requestUrl)
                        .post(body)
                        .addHeader("content-type", "application/x-www-form-urlencoded")
                        .build();

                OkHttpClient client = new OkHttpClient();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                log.info(response.code());

                candidates.clear();
            }
        }
    }
}
