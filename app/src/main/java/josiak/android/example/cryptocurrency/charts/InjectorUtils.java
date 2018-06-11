package josiak.android.example.cryptocurrency.charts;

import android.content.Context;

import josiak.android.example.cryptocurrency.charts.api.CoinMarketCap.CoinMarketCapApi;
import josiak.android.example.cryptocurrency.charts.api.CryptoCompare.CryptoCompareApi;
import josiak.android.example.cryptocurrency.charts.api.HttpClient;
import josiak.android.example.cryptocurrency.charts.database.CryptoLocalCache;
import josiak.android.example.cryptocurrency.charts.database.CryptocurrencyChartsDatabase;
import josiak.android.example.cryptocurrency.charts.repository.CryptoRepository;
import josiak.android.example.cryptocurrency.charts.repository.SimpleNetworkCalls;
import josiak.android.example.cryptocurrency.charts.ui.CryptoViewModelFactory;
import okhttp3.OkHttpClient;

/**
 * Created by Jakub on 2018-05-25.
 */

public class InjectorUtils {
    public static OkHttpClient provideHttpClient(){
        return HttpClient.getInstance();
    }

    private static AppExecutors provideAppExecutors(){
        return AppExecutors.getInstance();
    }

    private static CryptoLocalCache provideCryptoLocalCache(Context context){
        CryptocurrencyChartsDatabase database = CryptocurrencyChartsDatabase.getInstance(context);
        return new CryptoLocalCache(database.cryptoDao(), database.favsDao(), provideAppExecutors());
    }

    private static SimpleNetworkCalls provideSimpleNetworkCalls(Context context){
        return new SimpleNetworkCalls(CryptoCompareApi.create(), provideCryptoLocalCache(context));
    }

    public static CryptoRepository provideCryptoRepository(Context context){
        return new CryptoRepository(
                CoinMarketCapApi.create(),
                CryptoCompareApi.create(),
                provideCryptoLocalCache(context),
                provideSimpleNetworkCalls(context),
                context);
    }

    public static CryptoViewModelFactory provideMainListViewModelFactory(Context context){
        return new CryptoViewModelFactory(provideCryptoRepository(context));
    }
}
