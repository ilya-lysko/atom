package ru.atom.matchmaker;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Created by ilysk on 16.04.17.
 */
public class MatchMakerServer {
    private static Server jettyServer;

    public static void start(boolean isTest) throws Exception {
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/mm");

        jettyServer = new Server(8090);
        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.packages",
                "ru.atom.matchmaker"
        );

        if (isTest) {
            jettyServer.start();
        } else {
            try {
                jettyServer.start();
                jettyServer.join();
            } finally {
                jettyServer.destroy();
            }
        }
    }

    public static void finish() {
        try {
            jettyServer.destroy();
        } catch (Exception ignored) {
            //nothing
        }
    }

    private static ContextHandler createResourceContext() {
        ContextHandler context = new ContextHandler();
        context.setContextPath("/gs/0");
        ResourceHandler handler = new ResourceHandler();
        handler.setWelcomeFiles(new String[]{"index.html"});

        String serverRoot = MatchMakerServer.class.getResource("").toString();
        handler.setResourceBase(serverRoot);
        context.setHandler(handler);
        return context;
    }

    public static void main(String[] args) throws Exception {
        MatchMakerServer.start(false);
        Thread matchMakerService = new Thread(new MatchMaker());
        matchMakerService.start();
    }
}
