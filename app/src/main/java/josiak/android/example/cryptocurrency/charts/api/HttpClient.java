package josiak.android.example.cryptocurrency.charts.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;

/**
 * Created by Jakub on 2018-05-25.
 */

public class HttpClient {

    private static OkHttpClient singletonInstance;
    private static final Object LOCK = new Object();

    public static OkHttpClient getInstance() {
        if (singletonInstance == null)
            synchronized (LOCK) {
                singletonInstance = new OkHttpClient.Builder()
                        .addInterceptor(new HttpLoggingInterceptor().setLevel(Level.BASIC))
                        .build();
            }
        return singletonInstance;
    }
}
