package josiak.android.example.cryptocurrency.charts.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import josiak.android.example.cryptocurrency.charts.InjectorUtils;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Jakub on 2018-05-25.
 */

public class CoinMarketCapApi {
    private static final String BASE_URL = "https://api.coinmarketcap.com/";

    public CoinMarketCapApi() {
    }

    public interface CoinMarketCap {
        @GET("v2/ticker/?")
        Call<CryptoSimpleResponse> requestCoins(
                @Query("start")int resultsFromRank,
                @Query("limit")int maxResults
        );
    }

    public static CoinMarketCap create() {
        OkHttpClient client = InjectorUtils.provideHttpClient();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CoinMarketCap.class);
    }
}
