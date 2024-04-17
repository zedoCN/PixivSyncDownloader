package top.zedo.pixivsyncdownloader.api;

import com.google.gson.JsonObject;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;

import java.io.IOException;
import java.util.Objects;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static top.zedo.pixivsyncdownloader.Database.download;

public class DownloadInfoServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");
        long id = Long.parseLong(Objects.requireNonNullElse(req.getParameter("id"), "-1"));
        int page = Integer.parseInt(Objects.requireNonNullElse(req.getParameter("page"), "0"));

        JsonObject rootObject = new JsonObject();
        rootObject.addProperty("all_total", download.countDocuments());
        rootObject.addProperty("downloaded_total", download.countDocuments(eq("downloaded", true)));
        if (id != -1) {
            Document find = download.find(and(eq("id", id), eq("page", page))).first();
            rootObject.addProperty("find", find != null);
            if (find != null) {
                JsonObject documentObject = new JsonObject();
                documentObject.addProperty("id", find.getLong("id"));
                documentObject.addProperty("page", find.getInteger("page"));
                documentObject.addProperty("name", find.getString("name"));
                documentObject.addProperty("hash", find.getString("hash"));
                documentObject.addProperty("downloaded", find.getBoolean("downloaded"));
                rootObject.add("info", documentObject);
            }
        }

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.getWriter().println(rootObject);
    }
}
