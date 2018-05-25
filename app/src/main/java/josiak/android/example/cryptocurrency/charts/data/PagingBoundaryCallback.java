package josiak.android.example.cryptocurrency.charts.data;

import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import josiak.android.example.cryptocurrency.charts.api.CoinMarketCapApi.CoinMarketCap;
import josiak.android.example.cryptocurrency.charts.api.CryptoCompareApi.CryptoCompare;
import josiak.android.example.cryptocurrency.charts.api.CryptoDetailedResponse;
import josiak.android.example.cryptocurrency.charts.api.CryptoSimpleResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jakub on 2018-05-25.
 */

public class PagingBoundaryCallback extends PagedList.BoundaryCallback {
    private CoinMarketCap coinMarketCapApi;
    private CryptoCompare cryptoCompareApi;
    private int resultsFromRank = 1;
    private static final int RESULTS_SIZE = 50;
    private static final String LOG_TAG = PagingBoundaryCallback.class.getSimpleName();
    private boolean requestingInProgress = false;
    private List<CryptoSimple> cryptoSimpleList;

    public PagingBoundaryCallback(
            CoinMarketCap coinMarketCapApi,
            CryptoCompare cryptoCompareApi) {
        this.coinMarketCapApi = coinMarketCapApi;
        this.cryptoCompareApi = cryptoCompareApi;
    }

    @Override
    public void onZeroItemsLoaded() {
        super.onZeroItemsLoaded();
    }

    @Override
    public void onItemAtEndLoaded(@NonNull Object itemAtEnd) {
        super.onItemAtEndLoaded(itemAtEnd);
    }

    private void requestAndSaveData() {
        if (requestingInProgress) return;

        requestingInProgress = true;
        coinMarketCapApi.requestCoins(resultsFromRank, RESULTS_SIZE).enqueue(
                new Callback<CryptoSimpleResponse>() {
                    @Override
                    public void onResponse(Call<CryptoSimpleResponse> call, Response<CryptoSimpleResponse> response) {
                        Log.d(LOG_TAG, "got response from CoinMarketCapApi: " + response.toString());
                        if (response.isSuccessful()) {
                            cryptoSimpleList = response.body().getItems();
                            resultsFromRank += RESULTS_SIZE;
                        } else {
                            Log.d(LOG_TAG, "Unknown error " + response.errorBody().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<CryptoSimpleResponse> call, Throwable t) {
                        Log.d(LOG_TAG, "failed to get data");
                        Log.d(LOG_TAG, "Unknown error " + t.getMessage());
                    }
                }
        );
        StringBuilder cryptoSymbol = new StringBuilder(RESULTS_SIZE);

        for (CryptoSimple item : cryptoSimpleList) {
            cryptoSymbol
                    .append(item.getSymbol())
                    .append(",");
        }

        String fromSymbol = cryptoSymbol.toString();

        cryptoCompareApi.requestCoins(fromSymbol, "USD").enqueue(new Callback<CryptoDetailedResponse>() {
            @Override
            public void onResponse(Call<CryptoDetailedResponse> call, Response<CryptoDetailedResponse> response) {
                
            }

            @Override
            public void onFailure(Call<CryptoDetailedResponse> call, Throwable t) {

            }
        });

    }
}
