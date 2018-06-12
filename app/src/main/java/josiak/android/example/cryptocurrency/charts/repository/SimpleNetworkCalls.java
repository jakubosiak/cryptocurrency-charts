package josiak.android.example.cryptocurrency.charts.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import josiak.android.example.cryptocurrency.charts.R;
import josiak.android.example.cryptocurrency.charts.Utilities;
import josiak.android.example.cryptocurrency.charts.api.CryptoCompare.CryptoCompareApi.CryptoCompare;
import josiak.android.example.cryptocurrency.charts.api.CryptoCompare.CryptoDetailedResponse;
import josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState;
import josiak.android.example.cryptocurrency.charts.data.CryptoDetailed;
import josiak.android.example.cryptocurrency.charts.data.CryptoType;
import josiak.android.example.cryptocurrency.charts.database.CryptoLocalCache;
import josiak.android.example.cryptocurrency.charts.database.FavsDao;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState.FINISHED;
import static josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState.REFRESHING;

/**
 * Created by Kuba on 2018-06-07.
 */

public class SimpleNetworkCalls {
    private CryptoCompare cryptoCompareApi;
    private CryptoLocalCache cache;
    private static final String TO_SYMBOL = "USD";
    private int index = 0;
    private boolean requestingInProgress = false;
    private static final String LOG_TAG = SimpleNetworkCalls.class.getSimpleName();
    private List<String> queryValues = new ArrayList<>();
    private MutableLiveData<NetworkCallbackState> _state = new MutableLiveData<>();
    public LiveData<NetworkCallbackState> state;

    public SimpleNetworkCalls(
            CryptoCompare cryptoCompareApi,
            CryptoLocalCache cache) {
        this.cryptoCompareApi = cryptoCompareApi;
        this.cache = cache;
        state = _state;
    }

    public void refreshFavs(){
        _state.postValue(REFRESHING);
        updateFavouriteCryptos();
    }

    public void searchSpecifiedCoin(String searchQuery) {
        networkCall(searchQuery);
    }

    public void updateFavouriteCryptos() {
        StringBuilder symbolsList = new StringBuilder();
        List<String> favsSymbols = cache.getFavourites();

        for (int i = 0; i < favsSymbols.size(); i++) {
            symbolsList
                    .append(favsSymbols.get(i))
                    .append(",");
            ++index;
            if (index == 25 || i == favsSymbols.size() - 1) {
                symbolsList.deleteCharAt(symbolsList.length() - 1);
                queryValues.add(symbolsList.toString());
                index = 0;
                if (i == favsSymbols.size() - 1) {
                    getFavouriteCryptos(queryValues);
                }
                symbolsList.setLength(0);
            }
        }

        if(favsSymbols.size() == 0){
            _state.postValue(FINISHED);
        }
    }

    private void networkCall(String searchQuery) {
        if (requestingInProgress) return;
        requestingInProgress = true;
        if (searchQuery != null)
            cryptoCompareApi.requestCoins(searchQuery, TO_SYMBOL).enqueue(new Callback<CryptoDetailedResponse>() {
                @Override
                public void onResponse(Call<CryptoDetailedResponse> call, Response<CryptoDetailedResponse> response) {
                    if (response.isSuccessful()) {
                        HashMap<String, HashMap<String, CryptoDetailed>> hashMap = response.body().getList();
                        for (Map.Entry<String, HashMap<String, CryptoDetailed>> entry : hashMap.entrySet()) {
                            for (Map.Entry<String, CryptoDetailed> innerEntry : entry.getValue().entrySet()) {
                                cache.updateDataAfterSearch(Utilities.cryptoUpdateConverter(CryptoType.SEARCH, innerEntry.getValue(), searchQuery));
                                Log.v("added", "searchQuery");
                            }
                        }
                        requestingInProgress = false;
                    } else {
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

    private void getFavouriteCryptos(List<String> query) {
        if (requestingInProgress) return;
        if (query.size() > 0) {
            requestingInProgress = true;
            if (index < query.size()) {
                cryptoCompareApi.requestCoins(query.get(index), TO_SYMBOL).enqueue(new Callback<CryptoDetailedResponse>() {
                    @Override
                    public void onResponse(Call<CryptoDetailedResponse> call, Response<CryptoDetailedResponse> response) {
                        if (response.isSuccessful()) {
                            HashMap<String, HashMap<String, CryptoDetailed>> hashMap = response.body().getList();
                            for (Map.Entry<String, HashMap<String, CryptoDetailed>> entry : hashMap.entrySet()) {
                                for (Map.Entry<String, CryptoDetailed> innerEntry : entry.getValue().entrySet()) {
                                    cache.updateDataAfterSearch(Utilities.cryptoUpdateConverter(CryptoType.NEW, innerEntry.getValue(), innerEntry.getValue().getSymbol()));
                                    Log.v("added", "searchQuery");
                                }
                            }
                            index++;
                            getFavouriteCryptos(queryValues);
                            requestingInProgress = false;
                        } else {
                            Log.d(LOG_TAG, "Unknown error " + response.errorBody().toString());
                            _state.postValue(FINISHED);
                            requestingInProgress = false;
                        }
                    }

                    @Override
                    public void onFailure(Call<CryptoDetailedResponse> call, Throwable t) {
                        _state.postValue(FINISHED);
                        requestingInProgress = false;
                        Log.d(LOG_TAG, "failed to get data");
                        Log.d(LOG_TAG, "Unknown error " + t.getMessage());
                    }
                });
                requestingInProgress = false;
            } else {
                _state.postValue(FINISHED);
                queryValues.clear();
                index = 0;
            }
        }
    }
}
