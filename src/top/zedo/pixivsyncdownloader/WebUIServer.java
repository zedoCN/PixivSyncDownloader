package top.zedo.pixivsyncdownloader;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import top.zedo.pixivsyncdownloader.api.*;

public class WebUIServer {


    /**
     * 启动服务器
     *
     * @param port 端口
     */
    public static void start(int port) {
        Server server = new Server(port);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("./web/dist");
        resourceHandler.setDirectoriesListed(true);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD,OPTIONS");
        context.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        context.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

        context.addServlet(new ServletHolder(new DownloadInfoServlet()), "/api/download");
        context.addServlet(new ServletHolder(new FollowingServlet()), "/api/following");
        context.addServlet(new ServletHolder(new IllustsServlet()), "/api/illusts");
        context.addServlet(new ServletHolder(new ImageServlet()), "/api/image");
        context.addServlet(new ServletHolder(new AccountServlet()), "/api/account");
        context.addServlet(new ServletHolder(new ConfigServlet()), "/api/config");
        context.addServlet(new ServletHolder(new DatabaseServlet()), "/api/database");


        HandlerCollection handlers = new HandlerCollection();
        handlers.addHandler(resourceHandler);
        handlers.addHandler(context);

        server.setHandler(handlers);

        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
