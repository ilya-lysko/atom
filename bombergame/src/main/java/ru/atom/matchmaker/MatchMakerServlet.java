package ru.atom.matchmaker;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.atom.hibernate.LoginEntity;
import ru.atom.hibernate.RegistredEntity;
import ru.atom.hibernate.UserGameResult;
import ru.atom.hibernate.UserGameResultDao;
import ru.atom.network.TokenStorage;
import ru.atom.network.UserStorage;
import ru.atom.util.ThreadSafeQueue;
import ru.atom.util.ThreadSafeStorage;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;

/**
 * Created by ilysk on 16.04.17.
 */
@Path("/")
public class MatchMakerServlet {
    private static final Logger log = LogManager.getLogger(MatchMakerServlet.class);

    @POST
    @Path("/join")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/plain")
    public static Response join(@HeaderParam(HttpHeaders.AUTHORIZATION) String tokenParam) {
        String token = tokenParam.substring("Bearer".length()).trim();
        if (token == null) {
            log.info("no token in request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            Long longToken = Long.parseLong(token);
            LoginEntity user = TokenStorage.getByToken(longToken);
            log.info("user \"" + user.getUser().getLogin() + "\" joined game");
            ThreadSafeQueue.getInstance().offer(user.getToken());
            String gameurl = "http://localhost:8090/gs/" + ThreadSafeStorage.getCurrentGameSessionId();
            return Response.ok(gameurl).build();
        }
    }

    @POST
    @Path("/finish")
    @Consumes("application/x-www-form-urlencoded")
    public static Response finish(@FormParam("gameresult") String gameResult) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> map;

            // convert JSON string to Map
            map = mapper.readValue(gameResult, new TypeReference<Map<String, Object>>(){});

            Integer gameId = new Integer(map.get("id").toString());

            Map<String, Object> gameResultMapBody;
            gameResultMapBody = (Map<String, Object>) map.get("result");

            for (Object o : gameResultMapBody.entrySet()) {
                Map.Entry pair = (Map.Entry) o;
                String userString = (String) pair.getKey();
                if (TokenStorage.getLoginByName(userString) != null) {
                    RegistredEntity user = UserStorage.getByName(userString);
                    UserGameResult userGameResult = new UserGameResult(gameId, user, (int) pair.getValue());
                    UserGameResultDao.saveGameResults(userGameResult);
                    log.info("user " + userString + " finished game id#" + gameId
                            + " with score " + pair.getValue().toString());
                } else {
                    log.info("No logined user: " + userString);
                }
            }

        } catch (JsonGenerationException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (JsonMappingException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (NullPointerException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok().build();
    }
}
