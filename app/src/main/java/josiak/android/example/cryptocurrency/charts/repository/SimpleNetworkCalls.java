package josiak.android.example.cryptocurrency.charts.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import josiak.android.example.cryptocurrency.charts.Utilities;
import josiak.android.example.cryptocurrency.charts.api.CryptoCompare.CryptoCompareApi.CryptoCompareHistorical;
import josiak.android.example.cryptocurrency.charts.api.CryptoCompare.CryptoCompareApi.CryptoCompare;
import josiak.android.example.cryptocurrency.charts.api.CryptoCompare.CryptoDetailedResponse;
import josiak.android.example.cryptocurrency.charts.api.CryptoCompare.CryptoHistoricalDataResponse;
import josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState;
import josiak.android.example.cryptocurrency.charts.data.CryptoDetailed;
import josiak.android.example.cryptocurrency.charts.data.CryptoHistoricalData;
import josiak.android.example.cryptocurrency.charts.data.CryptoType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState.FINISHED;
import static josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState.NO_INTERNET;
import static josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState.REFRESHING;

/**
 * Created by Kuba on 2018-06-07.
 */

public class SimpleNetworkCalls {
    private CryptoCompare cryptoCompareApi;
    private CryptoCompareHistorical cryptoCompareHistorical;
    private CryptoLocalCache cache;

    private static final String TO_SYMBOL = "USD";

    private int index = 0;
    private Context context;
    private boolean requestingInProgress = false;
    private boolean requestingHistoricalInProgress = false;
    private static final String LOG_TAG = SimpleNetworkCalls.class.getSimpleName();
    private List<String> queryValues = new ArrayList<>();
    private List<CryptoHistoricalData> cryptoHistoricalDataList = new ArrayList<>();
    private MutableLiveData<NetworkCallbackState> _state = new MutableLiveData<>();
    public LiveData<NetworkCallbackState> state;
    private StringBuilder symbolsList = new StringBuilder();

    public SimpleNetworkCalls(
            CryptoCompare cryptoCompareApi,
            CryptoCompareHistorical cryptoCompareHistorical,
            CryptoLocalCache cache,
            Context context) {
        this.cryptoCompareApi = cryptoCompareApi;
        this.cryptoCompareHistorical = cryptoCompareHistorical;
        this.cache = cache;
        this.context = context;
        state = _state;
    }

    public void refreshFavs() {
        if (!Utilities.isOnline(context)) {
            _state.postValue(NO_INTERNET);
        } else {
            _state.postValue(REFRESHING);
            updateFavouriteCryptos();
        }
    }

    public void searchSpecifiedCoin(String searchQuery) {
        networkCall(searchQuery, CryptoType.SEARCH);
    }

    public void searchHistoricalData(String symbol, String queryInterval, int queryLimit, int timePeriod) {
        networkCall(symbol, CryptoType.NEW);
        getCryptoHistoricalData(symbol, cache.getIdBySymbol(symbol), queryInterval, queryLimit, timePeriod);
    }

    public void updateFavouriteCryptos() {
        List<String> favsSymbols = cache.getFavourites();

        if (queryValues.size() > 0)
            queryValues.clear();
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

        if (favsSymbols.size() == 0) {
            _state.postValue(FINISHED);
        }
    }

    private void networkCall(String searchQuery, CryptoType type) {
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
                                cache.updateDataAfterSearch(Utilities.cryptoUpdateConverter(
                                        type,
                                        innerEntry.getValue(),
                                        searchQuery));
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
                                    cache.updateDataAfterSearch(Utilities.cryptoUpdateConverter(
                                            CryptoType.NEW,
                                            innerEntry.getValue(),
                                            innerEntry.getValue().getSymbol()));
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
                index = 0;
            }
        }
    }

    private void getCryptoHistoricalData(String symbol, long id, String queryInterval, int queryLimit, int timePeriod) {
        if (requestingHistoricalInProgress) return;
        requestingHistoricalInProgress = true;
        if (symbol != null)
            cryptoCompareHistorical.requestHistoricalData(queryInterval, symbol, TO_SYMBOL, queryLimit, timePeriod).enqueue(new Callback<CryptoHistoricalDataResponse>() {
                @Override
                public void onResponse(Call<CryptoHistoricalDataResponse> call, Response<CryptoHistoricalDataResponse> response) {
                    if (response.isSuccessful()) {
                        cache.deleteOldHistoricalData(symbol);
                        if (cryptoHistoricalDataList.size() > 0)
                            cryptoHistoricalDataList.clear();
                        Log.v("success", response.body().toString());
                        for (CryptoHistoricalData data : response.body().getData()) {
                            if (data.getClose() > 0.00f) {
                                CryptoHistoricalData cryptoHistoricalData = new CryptoHistoricalData(
                                        id,
                                        symbol,
                                        data.getTime(),
                                        Utilities.convertPrice(data.getClose()),
                                        Utilities.convertPrice(data.getHigh()),
                                        Utilities.convertPrice(data.getLow()),
                                        Utilities.convertPrice(data.getOpen()));
                                cryptoHistoricalDataList.add(cryptoHistoricalData);
                            }
                        }
                        cache.insertCryptoHistoricalData(cryptoHistoricalDataList);
                    }
                        requestingHistoricalInProgress = false;
                }

                @Override
                public void onFailure(Call<CryptoHistoricalDataResponse> call, Throwable t) {
                    requestingHistoricalInProgress = false;
                    Log.d(LOG_TAG, "failed to get data");
                    Log.d(LOG_TAG, "Unknown error " + t.getMessage());
                }
            });
        requestingHistoricalInProgress = false;
    }
}
