package josiak.android.example.cryptocurrency.charts.api.CoinMarketCap;

import josiak.android.example.cryptocurrency.charts.InjectorUtils;
import okhttp3.OkHttpClient;
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
