package josiak.android.example.cryptocurrency.charts.repository;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import josiak.android.example.cryptocurrency.charts.R;
import josiak.android.example.cryptocurrency.charts.Utilities;
import josiak.android.example.cryptocurrency.charts.api.CryptoCompare.CryptoCompareApi.CryptoCompare;
import josiak.android.example.cryptocurrency.charts.api.CryptoCompare.CryptoDetailedResponse;
import josiak.android.example.cryptocurrency.charts.data.CryptoDetailed;
import josiak.android.example.cryptocurrency.charts.data.CryptoType;
import josiak.android.example.cryptocurrency.charts.database.CryptoLocalCache;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kuba on 2018-06-07.
 */

public class SimpleNetworkCalls {
    private CryptoCompare cryptoCompareApi;
    private CryptoLocalCache cache;
    private static final String TO_SYMBOL = "USD";
    private boolean requestingInProgress = false;
    private static final String LOG_TAG = SimpleNetworkCalls.class.getSimpleName();

    public SimpleNetworkCalls(
            CryptoCompare cryptoCompareApi,
            CryptoLocalCache cache) {
        this.cryptoCompareApi = cryptoCompareApi;
        this.cache = cache;
    }

    public void searchSpecifiedCoin(String searchQuery) {
        Log.v("searchSpecifiedCoin", "true");
        networkCall(searchQuery);
    }

    private void networkCall(String searchQuery) {
        if (requestingInProgress) return;
        requestingInProgress = true;
        if(searchQuery != null)
        cryptoCompareApi.requestCoins(searchQuery, TO_SYMBOL).enqueue(new Callback<CryptoDetailedResponse>() {
            @Override
            public void onResponse(Call<CryptoDetailedResponse> call, Response<CryptoDetailedResponse> response) {
                if (response.isSuccessful()) {
                    HashMap<String, HashMap<String, CryptoDetailed>> hashMap = response.body().getList();
                    for (Map.Entry<String, HashMap<String, CryptoDetailed>> entry : hashMap.entrySet()) {
                        for (Map.Entry<String, CryptoDetailed> innerEntry : entry.getValue().entrySet()) {
                            cache.updateDataAfterSearch(Utilities.cryptoUpdateConverter(innerEntry.getValue(), searchQuery));
                            Log.v("added", "searchQuery");
                        }
                    }
                    requestingInProgress = false;
                }else {
                    Log.d(LOG_TAG, "Unknown error " + response.errorBody().toString());
                    requestingInProgress = false;
                }
            }

            @Override
            public void onFailure(Call<CryptoDetailedResponse> call, Throwable t) {
                requestingInProgress = false;
                Log.d(LOG_TAG, "failed to get data");
                Log.d(LOG_TAG, "Unknown error " + t.getMessage());
            }
        });
        requestingInProgress = false;
    }
}
