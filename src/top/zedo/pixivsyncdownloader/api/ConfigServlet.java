package top.zedo.pixivsyncdownloader.api;

import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import top.zedo.pixivsyncdownloader.Config;
import top.zedo.pixivsyncdownloader.Database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ConfigServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject rootObject = new JsonObject();
        rootObject.addProperty("database_connection_string", Config.DATA.databaseConnectionString);
        rootObject.addProperty("work_database_name", Config.DATA.workDatabaseName);
        rootObject.addProperty("repository_path", Config.DATA.repositoryPath);
        rootObject.addProperty("thread_count", Config.DATA.threadCount);

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.getWriter().println(rootObject);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject rootObject = new JsonObject();
        rootObject.addProperty("result", true);
        rootObject.addProperty("message", "设置成功。");
        try {
            String type = Objects.requireNonNull(req.getParameter("type"));
            String value = Objects.requireNonNull(req.getParameter("value"));
            switch (type) {
                case "database_connection_string" -> {
                    Database.changeConnectionString(value);
                }
                case "work_database_name" -> {
                    Database.changeDatabase(value);
                }
                case "repository_path" -> {
                    Path path = Path.of(value);
                    if (!Files.exists(path))
                        Files.createDirectories(path);
                }
                case "thread_count" -> {
                    Config.DATA.threadCount = Integer.parseInt(value);
                    Config.save();
                }
            }
        } catch (Exception e) {
            rootObject.addProperty("result", false);
            rootObject.addProperty("message", e.getMessage());
        }

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.getWriter().println(rootObject);
    }
}
