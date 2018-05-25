package josiak.android.example.cryptocurrency.charts;

import josiak.android.example.cryptocurrency.charts.api.CoinMarketCapApi.CoinMarketCap;
import josiak.android.example.cryptocurrency.charts.api.CryptoCompareApi.CryptoCompare;

/**
 * Created by Jakub on 2018-05-25.
 */

public class CryptoRepository {
    private CoinMarketCap coinMarketCapApi;
    private CryptoCompare cryptoCompareApi;

    public CryptoRepository(
            CoinMarketCap coinMarketCapApi,
            CryptoCompare cryptoCompareApi) {
        this.coinMarketCapApi = coinMarketCapApi;
        this.cryptoCompareApi = cryptoCompareApi;
    }

    public void requestCoins(){

    }
}
