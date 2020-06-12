package com.songling.app.conf;

public class UrlCleaner {
    public static String clean(String url) {
        if (url.matches(".*/echo/.*")) {
            System.out.println("change url");
            url = url.replaceAll("/echo/.*", "/echo/{str}");
        }
        return url;
    }
}
