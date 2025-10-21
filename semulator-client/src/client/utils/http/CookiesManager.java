package client.utils.http;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.*;

public class CookiesManager implements CookieJar {

    private final Map<String, List<Cookie>> cookieStore = new HashMap<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cookieStore.put(url.host(), cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        // Return cookies for this host, or empty list if none
        List<Cookie> cookies = cookieStore.get(url.host());
        return cookies != null ? cookies : Collections.emptyList();
    }

    public List<Cookie> getCookiesForHost(String host) {
        return cookieStore.getOrDefault(host, Collections.emptyList());
    }

    public void clear() {
        cookieStore.clear();
    }
}
