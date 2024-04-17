package top.zedo.pixivsyncdownloader.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.client.FindIterable;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;

import java.io.IOException;
import java.util.Objects;

import static com.mongodb.client.model.Filters.*;
import static top.zedo.pixivsyncdownloader.Database.illusts;

public class IllustsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");
        String search = Objects.requireNonNullElse(req.getParameter("search"), "");
        String type = Objects.requireNonNullElse(req.getParameter("type"), "id");
        int offset = Integer.parseInt(Objects.requireNonNullElse(req.getParameter("offset"), "0"));
        int num = Integer.parseInt(Objects.requireNonNullElse(req.getParameter("num"), "8"));
        JsonObject rootObject = new JsonObject();

        FindIterable<Document> find = switch (type) {
            case "id" -> illusts.find(eq("id", Long.parseLong(search)));
            case "tag" -> illusts.find(or(in("tags", search), in("translated_tags", search)));
            case "tags" -> illusts.find(or(in("tags", search.split(" ")), in("translated_tags", search.split(" "))));
            case "title" -> illusts.find(eq("title", search));
            case "title_regex" -> illusts.find(regex("title", search));
            case "author" -> illusts.find(eq("author", Long.parseLong(search)));
            case "caption" -> illusts.find(eq("caption", search));
            case "caption_regex" -> illusts.find(regex("caption", search));
            default -> null;
        };

        int count = 0;
        JsonArray array = new JsonArray();
        if (find != null) {
            try (var iterator = find.iterator()) {
                while (iterator.hasNext()) {
                    var document = iterator.next();


                    count++;
                    if (count-1 < offset || count-1 >= offset + num) continue;
                    JsonObject documentObject = new JsonObject();
                    documentObject.addProperty("id", document.getLong("id"));
                    documentObject.addProperty("author", document.getLong("author"));
                    documentObject.addProperty("caption", document.getString("caption"));
                    documentObject.addProperty("title", document.getString("title"));
                    documentObject.addProperty("sync_time", document.getLong("sync_time"));
                    {
                        JsonArray tagsArray = new JsonArray();
                        for (var tag : document.getList("tags", String.class))
                            tagsArray.add(tag);
                        documentObject.add("tags", tagsArray);
                    }
                    {
                        JsonArray tagsArray = new JsonArray();
                        for (var tag : document.getList("translated_tags", String.class))
                            tagsArray.add(tag);
                        documentObject.add("translated_tags", tagsArray);
                    }
                    documentObject.addProperty("pages", document.getList("urls", String.class).size());
                    array.add(documentObject);

                }
            }
        }

        rootObject.addProperty("count", count);
        rootObject.add("illusts", array);

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.getWriter().println(rootObject);
    }
}
