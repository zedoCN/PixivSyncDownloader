package top.zedo.pixivsyncdownloader;

import com.google.gson.Gson;
import top.zedo.pixivsyncdownloader.pixiv.PixivData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Config {
    private static final Gson GSON = new Gson();
    public static final ConfigData DATA;
    private static final Path CONFIG_FILE_PATH = Path.of("./config.json");
    private static final Thread SHUTDOWN_HOOK = new Thread(Config::save);

    static {
        ConfigData configData = null;
        if (Files.exists(CONFIG_FILE_PATH)) {
            try (BufferedReader reader = Files.newBufferedReader(CONFIG_FILE_PATH, StandardCharsets.UTF_8)) {
                configData = GSON.fromJson(reader, ConfigData.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        DATA = Objects.requireNonNullElse(configData, new ConfigData());
        Runtime.getRuntime().addShutdownHook(SHUTDOWN_HOOK);
    }

    /**
     * 保存配置
     */
    public static void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_FILE_PATH, StandardCharsets.UTF_8)) {
            writer.write(GSON.toJson(DATA));
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ConfigData {
        /**
         * 账号列表
         */
        public Map<Long, PixivData.Auth> accountList;
        /**
         * 同步关注画师的账号id
         */
        public Set<Long> syncFollowing;
        public Set<Long> syncFavorite;
        public Set<Long> participateInDownloads;
        /**
         * 数据库地址
         */
        public String databaseConnectionString;
        /**
         * 工作数据库名
         */
        public String workDatabaseName;
        /**
         * 存储库路径
         */
        public String repositoryPath;
        /**
         * 服务器监听端口
         */
        public int serverPort;

        public ConfigData() {
            accountList = new HashMap<>();
            syncFollowing = new HashSet<>();
            syncFavorite = new HashSet<>();
            participateInDownloads = new HashSet<>();
            databaseConnectionString = "";
            workDatabaseName = "pixiv";
            repositoryPath = "./repository";
            serverPort = 8080;
        }
    }
}
