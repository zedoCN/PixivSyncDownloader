package top.zedo.pixivsyncdownloader.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;
import top.zedo.pixivsyncdownloader.Database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class DatabaseServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject rootObject = new JsonObject();
        JsonArray availableDbs = new JsonArray();
        JsonArray allDbs = new JsonArray();

        for (var dbName : Database.mongoClient.listDatabaseNames()) {
            MongoDatabase db = Database.mongoClient.getDatabase(dbName);
            if (!dbName.equals("admin") && !dbName.equals("config") && !dbName.equals("local")) {
                allDbs.add(dbName);
                if (db.listCollectionNames().into(new ArrayList<>()).contains("pixiv"))
                    availableDbs.add(dbName);
            }
        }

        rootObject.add("available", availableDbs);
        rootObject.add("all", allDbs);

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.getWriter().println(rootObject);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject rootObject = new JsonObject();
        String operation = Objects.requireNonNull(req.getParameter("operation"));
        String name = Objects.requireNonNull(req.getParameter("name"));

        rootObject.addProperty("result", true);
        try {
            if (name.equals("admin") || name.equals("config") || name.equals("local")) {
                throw new RuntimeException("不能操作此数据库！");
            }
            MongoDatabase db = Database.mongoClient.getDatabase(name);
            switch (operation) {
                case "create" -> {
                    MongoCollection<Document> pixiv = db.getCollection("pixiv");
                    pixiv.insertOne(new Document("creationDate", System.currentTimeMillis()));

                    MongoCollection<Document> illusts = db.getCollection("illusts");
                    illusts.createIndex(new Document("author", 1), new IndexOptions().unique(true));
                    illusts.createIndex(new Document("id", 1).append("update", 1), new IndexOptions());

                    MongoCollection<Document> following = db.getCollection("following");
                    following.createIndex(new Document("account", 1).append("id", 1), new IndexOptions().unique(true));
                    following.createIndex(new Document("name", 1).append("update", 1), new IndexOptions());

                    MongoCollection<Document> download = db.getCollection("download");
                    download.createIndex(new Document("name", 1).append("id", 1), new IndexOptions().unique(true));
                    download.createIndex(new Document("downloaded", 1).append("id", 1).append("page", 1).append("hash", 1), new IndexOptions());

                    rootObject.addProperty("message", "创建成功");
                }
                case "delete" -> {
                    if (!db.listCollectionNames().into(new ArrayList<>()).contains("pixiv"))
                        throw new RuntimeException("只能删除创建的数据库！");
                    db.drop();
                    rootObject.addProperty("message", "删除成功");
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
