package top.zedo.pixivsyncdownloader.pixiv;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.BrotliDecompressingEntity;
import org.apache.hc.client5.http.entity.GzipDecompressingEntity;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.http.message.HeaderGroup;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PixivAPI {
    private static final String URL = "https://app-api.pixiv.net";
    private static final String AUTH_URL = "https://oauth.secure.pixiv.net";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final String CLIENT_ID = "MOBrBDS8blbauoSck0ZfDbtuzpyT";
    private static final String CLIENT_SECRET = "lsACyCD94FhDUtGTXi3QzcFE2uU1hqtDaKeqrdwj";
    private static final String HASH_SECRET = "28c1fdd170a5204386cb1313c7077b34f83e4aaf4aa829ce78c231e05b0bae2c";
    private static final HttpHost PROXY = new HttpHost("127.0.0.1", 10809);
    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom().setProxy(PROXY).build();

    private static final String LOGIN_HEAD = "https://app-api.pixiv.net/web/v1/login?code_challenge=";
    private static final String LOGIN_END = "&code_challenge_method=S256&client=pixiv-android";
    private static final HeaderGroup HEADER_GROUP = new HeaderGroup() {
        {
            setHeader(new BasicHeader("Host", "app-api.pixiv.net"));
            setHeader(new BasicHeader("Accept-Language", "zh-cn"));
            setHeader(new BasicHeader("Accept", "*/*"));
            setHeader(new BasicHeader("App-OS", "ios"));
            setHeader(new BasicHeader("App-OS-Version", "14.6"));
            setHeader(new BasicHeader("App-Version", "6.0.8"));
            setHeader(new BasicHeader("User-Agent", "PixivIOSApp/7.13.3 (iOS 14.6; iPhone13,2)"));
            setHeader(new BasicHeader("Accept-Encoding", "br, gzip, deflate"));
            setHeader(new BasicHeader("referer", URL + "/"));

        }
    };
    private String accessToken;
    private String refreshToken;
    private String username;
    private String password;
    private String verify;
    private String challenge;
    private String code;
    private PixivData.User user;

    public PixivData.User getUser() {
        return user;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    private static String generateClientTime() {
        ZonedDateTime utcTime = ZonedDateTime.now().withZoneSameInstant(java.time.ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss+00:00");
        return utcTime.format(formatter);
    }

    private static String generateClientHash(String clientTime) {
        // 将 clientTime 与 hashSecret 连接并计算 MD5 哈希值
        String data = clientTime + HASH_SECRET;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hashBytes = md.digest(data.getBytes());

        // 将字节数组转换为十六进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }


    public void setWebLogin(String verify, String challenge, String code) {
        this.verify = verify;
        this.challenge = challenge;
        this.code = code;
    }

    public static String getWebLoginUrl(String challenge) {
        return LOGIN_HEAD + challenge + LOGIN_END;
    }

    public void webLogin() {
        verify = PkceUtil.generateCodeVerifier();
        try {
            challenge = PkceUtil.generateCodeChallenge(verify);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        var url = LOGIN_HEAD + challenge + LOGIN_END;
        System.out.println("登录地址为: " + url);
        Scanner scanner = new Scanner(System.in);
        System.out.print("输入浏览器返回的(pixiv://account/login?code=...): ");
        String input = scanner.nextLine();
        code = input.substring(input.indexOf("code=") + 5, input.lastIndexOf("&"));
    }

    /**
     * 设置访问令牌
     *
     * @param refreshToken 刷新令牌
     */
    public void setRefreshToken(String refreshToken) {
        this.accessToken = null;
        this.refreshToken = refreshToken;
    }

    /**
     * 设置账号
     *
     * @param username 用户名
     * @param password 密码
     */
    public void setAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }


    /**
     * 刷新访问令牌
     *
     * @return 令牌信息
     */
    public PixivData.Auth auth() {
        List<NameValuePair> formData = new ArrayList<>();
        formData.add(new BasicNameValuePair("client_id", CLIENT_ID));
        formData.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
        https:
//accounts.pixiv.net/post-redirect?return_to=https%3A%2F%2Fapp-api.pixiv.net%2Fweb%2Fv1%2Fusers%2Fauth%2Fpixiv%2Fstart%3Fcode_challenge%3DW0rbTsTE67FJ8pLt_CiYlruun7FROdiPg4OXJWwQ-mE%26code_challenge_method%3DS256%26client%3Dpixiv-android%26via%3Dlogin
        if (username != null && password != null) {
            formData.add(new BasicNameValuePair("get_secure_url", "1"));
            formData.add(new BasicNameValuePair("grant_type", "password"));
            formData.add(new BasicNameValuePair("username", username));
            formData.add(new BasicNameValuePair("password", password));
        } else if (verify != null && challenge != null) {
            formData.add(new BasicNameValuePair("grant_type", "authorization_code"));
            formData.add(new BasicNameValuePair("code", code));
            formData.add(new BasicNameValuePair("code_verifier", verify));
            formData.add(new BasicNameValuePair("redirect_uri", "https://app-api.pixiv.net/web/v1/users/auth/pixiv/callback"));
            formData.add(new BasicNameValuePair("include_policy", "true"));
        } else {
            formData.add(new BasicNameValuePair("get_secure_url", "1"));
            formData.add(new BasicNameValuePair("grant_type", "refresh_token"));
            formData.add(new BasicNameValuePair("refresh_token", refreshToken));
        }


        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formData);

        HttpPost request = new HttpPost(AUTH_URL + "/auth/token");
        request.setEntity(entity);
        request.setHeaders(HEADER_GROUP.getHeaders());
        request.setHeader("Host", "oauth.secure.pixiv.net");
        String localTime = generateClientTime();
        request.setHeader("x-client-time", localTime);
        request.setHeader("x-client-hash", generateClientHash(localTime));


        request.setConfig(REQUEST_CONFIG);
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

            try (CloseableHttpResponse response = httpclient.execute(request)) {
                // 获取响应实体
                HttpEntity responseEntity = response.getEntity();
                if (response.getHeader("Content-Encoding") != null)
                    responseEntity = switch (response.getHeader("Content-Encoding").getValue()) {
                        case "gzip" -> new GzipDecompressingEntity(responseEntity);
                        case "br" -> new BrotliDecompressingEntity(responseEntity);
                        default -> responseEntity;
                    };
                String responseBody = EntityUtils.toString(responseEntity);

                PixivData.Auth data = GSON.fromJson(responseBody, PixivData.Auth.class);
                user = data.response.user;
                accessToken = data.response.access_token;
                refreshToken = data.response.refresh_token;
                HEADER_GROUP.setHeader(new BasicHeader("Authorization", "Bearer " + accessToken));
                return data;
            } catch (ProtocolException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonElement requestsApiGet(String url) {
        return GSON.fromJson(new String(requestsGetEntity(AUTH_URL + url), StandardCharsets.UTF_8), JsonElement.class);
    }

    private byte[] requestsGetEntity(String url) {
        HttpGet request = new HttpGet(url);
        request.setHeaders(HEADER_GROUP.getHeaders());
        request.setConfig(REQUEST_CONFIG);
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpclient.execute(request)) {
                HttpEntity responseEntity = response.getEntity();
                if (response.getHeader("Content-Encoding") != null)
                    responseEntity = switch (response.getHeader("Content-Encoding").getValue()) {
                        case "gzip" -> new GzipDecompressingEntity(responseEntity);
                        case "br" -> new BrotliDecompressingEntity(responseEntity);
                        default -> responseEntity;
                    };
                /*if (response.getCode() != 200)
                    throw new RuntimeException("连接异常: " + response.getCode());*/
                return responseEntity.getContent().readAllBytes();
            } catch (ProtocolException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long fileSize(String url) {
        HttpHead request = new HttpHead(url);
        request.setHeaders(HEADER_GROUP.getHeaders());
        request.setConfig(REQUEST_CONFIG);
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpclient.execute(request)) {
                return Long.parseLong(response.getHeader("Content-Length").getValue());
            } catch (ProtocolException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取插图详情
     *
     * @param illust_id 插图id
     * @return 插图详情
     */
    public PixivData.IllustDetail illustDetail(long illust_id) {
        return GSON.fromJson(requestsApiGet("/v1/illust/detail?illust_id=" + illust_id), PixivData.IllustDetail.class);
    }

    /**
     * 获取用户插图列表
     *
     * @param user_id 用户id
     * @param offset  偏移
     * @return 插图列表
     */
    public PixivData.UserIllusts userIllusts(long user_id, int offset) {
        return GSON.fromJson(requestsApiGet("/v1/user/illusts?user_id=" + user_id + "&type=illust&offset=" + offset), PixivData.UserIllusts.class);
    }

    /**
     * 获取关注列表
     *
     * @param user_id 用户id
     * @param offset  偏移
     * @return 关注列表
     */
    public PixivData.UserPreviews userFollowing(long user_id, int offset) {
        return GSON.fromJson(requestsApiGet("/v1/user/following?user_id=" + user_id + "&restrict=public&offset=" + offset), PixivData.UserPreviews.class);
    }

    /**
     * 获取自己的关注列表
     *
     * @param offset 偏移
     * @return 关注列表
     */
    public PixivData.UserPreviews userFollowing(int offset) {
        return userFollowing(user.id, offset);
    }

    /**
     * 下载图片
     *
     * @param url 图片url
     * @return 图片数据
     */
    public byte[] download(String url) {
        return requestsGetEntity(url);
    }
}
