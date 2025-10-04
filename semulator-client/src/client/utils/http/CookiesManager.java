package client.utils.http;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.*;

/**
 * Simple in-memory Cookie Manager for OkHttp.
 * Keeps cookies per host so that session (like JSESSIONID) is maintained.
 */
public class CookiesManager implements CookieJar {

    // A map between host -> list of cookies
    private final Map<String, List<Cookie>> cookieStore = new HashMap<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        // Save cookies by host
        cookieStore.put(url.host(), cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        // Return cookies for this host, or empty list if none
        List<Cookie> cookies = cookieStore.get(url.host());
        return cookies != null ? cookies : Collections.emptyList();
    }

    /** Optional: for debugging or manual access **/
    public List<Cookie> getCookiesForHost(String host) {
        return cookieStore.getOrDefault(host, Collections.emptyList());
    }

    public void clear() {
        cookieStore.clear();
    }
}
