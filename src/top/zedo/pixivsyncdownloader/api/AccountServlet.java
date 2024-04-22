package top.zedo.pixivsyncdownloader.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import top.zedo.pixivsyncdownloader.Config;
import top.zedo.pixivsyncdownloader.pixiv.PixivAPI;
import top.zedo.pixivsyncdownloader.pixiv.PixivData;
import top.zedo.pixivsyncdownloader.pixiv.PkceUtil;
import top.zedo.pixivsyncdownloader.utils.RetryableTask;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Set;

public class AccountServlet extends HttpServlet {
    private static String verify;
    private static String challenge;

    public static void update() {
        verify = PkceUtil.generateCodeVerifier();
        try {
            challenge = PkceUtil.generateCodeChallenge(verify);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject rootObject = new JsonObject();
        JsonArray accountJsonArray = new JsonArray();
        rootObject.add("accounts", accountJsonArray);
        for (var account : Config.DATA.accountList.values()) {
            JsonObject accountJson = new JsonObject();
            accountJson.addProperty("name", account.user.name);
            accountJson.addProperty("account", account.user.account);
            accountJson.addProperty("mail_address", account.user.mail_address);
            accountJson.addProperty("id", account.user.id);
            accountJson.addProperty("sync_following", Config.DATA.syncFollowing.contains(account.user.id));
            accountJson.addProperty("sync_favorite", Config.DATA.syncFavorite.contains(account.user.id));
            accountJson.addProperty("participate_in_downloads", Config.DATA.participateInDownloads.contains(account.user.id));

            accountJsonArray.add(accountJson);
        }
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.getWriter().println(rootObject);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String operation = Objects.requireNonNullElse(req.getParameter("operation"), "add");
        boolean isLogin = false;
        PixivAPI pixivAPI = new PixivAPI();

        JsonObject rootObject = new JsonObject();
        switch (operation) {
            case "add" -> {
                isLogin = true;

                String v = req.getParameter("token");
                if (v != null) {
                    pixivAPI.setRefreshToken(Objects.requireNonNull(req.getParameter("token")));
                } else {
                    pixivAPI.setWebLogin(verify, challenge, Objects.requireNonNull(req.getParameter("code")));
                }
            }
            case "remove" -> {
                long id = Long.parseLong(Objects.requireNonNull(req.getParameter("id")));
                boolean result = Config.DATA.accountList.remove(id) != null;
                rootObject.addProperty("result", result);
                rootObject.addProperty("message", result ? "移除成功。" : "不存在此账号！");
            }
            case "web_login" -> {
                update();
                String url = PixivAPI.getWebLoginUrl(challenge);
                rootObject.addProperty("message", url);
            }
            case "set" -> {
                String type = Objects.requireNonNull(req.getParameter("type"));
                boolean value = Boolean.parseBoolean(Objects.requireNonNullElse(req.getParameter("value"), "false"));
                long id = Long.parseLong(Objects.requireNonNull(req.getParameter("id")));
                PixivData.Auth auth = Config.DATA.accountList.get(id);


                boolean result = auth != null;
                rootObject.addProperty("result", result);
                rootObject.addProperty("message", result ? "设置成功。" : "不存在此账号！");
                if (result) {
                    Set<Long> set = switch (type) {
                        case "sync_following" -> Config.DATA.syncFollowing;
                        case "sync_favorite" -> Config.DATA.syncFollowing;
                        case "participate_in_downloads" -> Config.DATA.participateInDownloads;
                        default -> throw new IllegalStateException("Unexpected value: " + type);
                    };
                    if (value)
                        set.add(auth.user.id);
                    else
                        set.remove(auth.user.id);

                }
            }
            case "check"->{
                long id = Long.parseLong(Objects.requireNonNull(req.getParameter("id")));
                pixivAPI.setRefreshToken(Config.DATA.accountList.get(id).response.refresh_token);
                RetryableTask task = new RetryableTask(5, 500) {
                    @Override
                    protected void runTask() {
                        PixivData.Auth auth = pixivAPI.auth();
                        //直接覆盖，就当更新账号信息
                        Config.DATA.accountList.put(auth.user.id, auth);
                        rootObject.addProperty("result", true);
                        rootObject.addProperty("message", "可以登录。");
                    }

                    @Override
                    protected void retry(int retries, Exception e) {
                        System.err.println("重试: " + retries);
                    }

                    @Override
                    protected void failed(Exception e) {
                        rootObject.addProperty("result", false);
                        rootObject.addProperty("message", "无法登录！");
                    }
                };
                task.run();
            }
        }

        if (isLogin) {
            RetryableTask task = new RetryableTask(5, 500) {
                @Override
                protected void runTask() {
                    PixivData.Auth auth = pixivAPI.auth();
                    boolean contained = Config.DATA.accountList.containsKey(auth.user.id);
                    //直接覆盖，就当更新账号信息
                    Config.DATA.accountList.put(auth.user.id, auth);
                    rootObject.addProperty("result", !contained);
                    if (contained) {
                        rootObject.addProperty("message", "已有此账号！");
                    } else {
                        rootObject.addProperty("message", "添加成功。");
                    }
                }

                @Override
                protected void retry(int retries, Exception e) {
                    System.err.println("重试: " + retries);
                }

                @Override
                protected void failed(Exception e) {
                    rootObject.addProperty("result", false);
                    rootObject.addProperty("message", "无法登录！");
                }
            };
            task.run();
        }

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.getWriter().println(rootObject);
    }

}
