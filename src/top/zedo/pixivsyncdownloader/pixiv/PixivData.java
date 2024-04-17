package top.zedo.pixivsyncdownloader.pixiv;

import java.util.List;
import java.util.Map;

public class PixivData {
    public static class Auth {
        public String access_token;
        public int expires_in;
        public String token_type;
        public String scope;
        public String refresh_token;
        public User user;
        public Auth response;
    }

    public static class User {
        public Map<String, String> profile_image_urls;
        public long id;
        public String name;
        public String account;
        public String mail_address;
        public boolean is_premium;
        public int x_restrict;
        public boolean is_mail_authorized;
        public boolean is_followed;
    }

    public static class IllustDetail {
        public Illust illust;
    }

    public static class Illust {
        public long id;
        public String title;
        public String type;
        public Map<String, String> image_urls;
        public String caption;
        public int restrict;
        public User user;
        public List<Tag> tags;
        public List<String> tools;
        public String create_date;
        public int page_count;
        public int width;
        public int height;
        public int sanity_level;
        public int x_restrict;
        //public String series;
        public Map<String, String> meta_single_page;
        public List<MetaPage> meta_pages;
        public int total_view;
        public int total_bookmarks;
        public boolean is_bookmarked;
        public boolean visible;
        public boolean is_muted;
        public int total_comments;
        public int illust_ai_type;
        public int illust_book_style;
        public int comment_access_control;
    }

    public static class Tag {
        public String name;
        public String translated_name;
    }

    public static class MetaPage {
        public ImageUrls image_urls;
    }

    public static class ImageUrls {
        public String square_medium;
        public String medium;
        public String large;
        public String original;
    }

    public static class UserIllusts {
        public User user;
        public List<Illust> illusts;
        public String next_url;
    }

    public static class UserPreview {
        public User user;
        public List<Illust> illusts;
        public boolean is_muted;
    }

    public static class UserPreviews {
        public List<UserPreview> user_previews;
        public String next_url;
    }
}