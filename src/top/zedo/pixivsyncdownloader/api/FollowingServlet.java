package top.zedo.pixivsyncdownloader.api;

import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;

import java.io.IOException;
import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;
import static top.zedo.pixivsyncdownloader.Database.following;

public class FollowingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");
        String search = Objects.requireNonNullElse(req.getParameter("search"), "");
        String type = Objects.requireNonNullElse(req.getParameter("type"), "id");
        JsonObject rootObject = new JsonObject();

        Document find = switch (type) {
            case "id" -> following.find(eq("id", Long.parseLong(search))).first();
            case "name" -> following.find(eq("name", search)).first();
            case "account" -> following.find(eq("account", search)).first();
            default -> null;
        };

        rootObject.addProperty("find", find != null);
        if (find != null) {
            JsonObject documentObject = new JsonObject();
            documentObject.addProperty("id", find.getLong("id"));
            documentObject.addProperty("name", find.getString("name"));
            documentObject.addProperty("account", find.getString("account"));
            documentObject.addProperty("sync_time", find.getLong("sync_time"));
            rootObject.add("info", documentObject);
        }

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.getWriter().println(rootObject);
    }


}
