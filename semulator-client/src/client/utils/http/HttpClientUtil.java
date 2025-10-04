package client.utils.http;

import okhttp3.*;

import java.io.File;
import java.util.Map;
import java.util.function.Consumer;

public class HttpClientUtil {

   //todo cookies:  private final static SimpleCookieManager simpleCookieManager = new SimpleCookieManager();
    private final static OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
                    /*todo.cookieJar(simpleCookieManager)*/
                    .followRedirects(false)
                    .build();

//    public static void setCookieManagerLoggingFacility(Consumer<String> logConsumer) {
//        simpleCookieManager.setLogData(logConsumer);
//    }

//    public static void removeCookiesOf(String domain) {
//        simpleCookieManager.removeCookiesOf(domain);
//    }

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
}
