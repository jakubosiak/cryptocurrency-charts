package josiak.android.example.cryptocurrency.charts.api.CryptoCompare;

import josiak.android.example.cryptocurrency.charts.InjectorUtils;
import josiak.android.example.cryptocurrency.charts.data.CryptoHistoricalData;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Jakub on 2018-05-25.
 */

public class CryptoCompareApi {
    private static final String BASE_URL = "https://min-api.cryptocompare.com/";
    private static  OkHttpClient client = InjectorUtils.provideHttpClient();

    public CryptoCompareApi() {
    }

    public interface CryptoCompare {
        @GET("data/pricemultifull")
        Call<CryptoDetailedResponse> requestCoins(
                @Query("fsyms") String fromSymbol,
                @Query("tsyms") String toSymbol
        );
    }

    public interface CryptoCompareHistorical {
        @GET("data/{type}")
        Call<CryptoHistoricalDataResponse> requestHistoricalData(
                @Path("type") String type,
                @Query("fsym") String fromSymbol,
                @Query("tsym") String toSymbol,
                @Query("limit") int limit,
                @Query("aggregate") int aggregate
        );
    }

    public static CryptoCompare create(){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CryptoCompare.class);
    }

    public static CryptoCompareHistorical createHistoricalData(){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CryptoCompareHistorical.class);
    }
}
