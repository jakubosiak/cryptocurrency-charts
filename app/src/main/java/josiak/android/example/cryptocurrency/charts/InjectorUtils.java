package josiak.android.example.cryptocurrency.charts;

import josiak.android.example.cryptocurrency.charts.api.CoinMarketCapApi;
import josiak.android.example.cryptocurrency.charts.api.CryptoCompareApi;
import josiak.android.example.cryptocurrency.charts.api.HttpClient;
import josiak.android.example.cryptocurrency.charts.ui.MainListViewModelFactory;
import okhttp3.OkHttpClient;

/**
 * Created by Jakub on 2018-05-25.
 */

public class InjectorUtils {
    public static OkHttpClient provideHttpClient(){
        return HttpClient.create();
    }

    public static CryptoRepository provideCryptoRepository(){
        return new CryptoRepository(CoinMarketCapApi.create(), CryptoCompareApi.create());
    }

    public static MainListViewModelFactory provideMainListViewModelFactory(){
        return new MainListViewModelFactory(provideCryptoRepository());
    }
}
