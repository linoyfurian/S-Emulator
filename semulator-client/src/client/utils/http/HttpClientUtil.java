package client.utils.http;

import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class HttpClientUtil {

    private static final CookiesManager COOKIES_MANAGER = new CookiesManager();
    private final static OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
                    .cookieJar(COOKIES_MANAGER)
                    .followRedirects(false)
                    .build();

    public static CookiesManager getCookiesManager() {
        return COOKIES_MANAGER;
    }

    public static void runAsync(String finalUrl, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        call.enqueue(callback);
    }

    public static void shutdown() {
        System.out.println("Shutting down HTTP CLIENT");
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }

    //file
    public static void postFileAsync(String url,
                                     File file,
                                     String fieldName,
                                     Map<String,String> extraFields,
                                     Callback callback) {

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if (extraFields != null) {
            for (var entry : extraFields.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }

        RequestBody fileBody = RequestBody.create(file, MediaType.parse("application/xml"));
        builder.addFormDataPart(fieldName, file.getName(), fileBody);

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Call call = HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }

    public static void postFileAsync(String url, RequestBody requestBody, Callback callback) {

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Call call = HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }

}
