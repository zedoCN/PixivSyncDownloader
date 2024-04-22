package top.zedo.pixivsyncdownloader;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class Database {

    public static MongoClient mongoClient;
    public static MongoDatabase database;
    public static MongoCollection<Document> download;
    public static MongoCollection<Document> following;
    public static MongoCollection<Document> illusts;

    static {
        changeConnectionString(Config.DATA.databaseConnectionString);
        changeDatabase(Config.DATA.workDatabaseName);
    }

    public static void changeConnectionString(String name) {
        System.out.println("连接数据库");
        if (name != null) {
            Config.DATA.databaseConnectionString = name;
            Config.save();
        }
        if (mongoClient != null)
            mongoClient.close();
        mongoClient = MongoClients.create(Config.DATA.databaseConnectionString);
        changeDatabase(null);
        System.out.println("数据库连接成功");
    }
    
    public static void changeDatabase(String name) {
        if (name != null) {
            Config.DATA.workDatabaseName = name;
            Config.save();
        }
        database = mongoClient.getDatabase(Config.DATA.workDatabaseName);
        download = getCollection("download");
        following = getCollection("following");
        illusts = getCollection("illusts");
    }


    public static MongoCollection<Document> getCollection(String name) {
        return database.getCollection(name);
    }

}
