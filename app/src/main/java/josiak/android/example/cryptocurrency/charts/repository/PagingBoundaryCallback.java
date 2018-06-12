package josiak.android.example.cryptocurrency.charts.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PagedList;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import josiak.android.example.cryptocurrency.charts.R;
import josiak.android.example.cryptocurrency.charts.Utilities;
import josiak.android.example.cryptocurrency.charts.api.CoinMarketCap.CoinMarketCapApi;
import josiak.android.example.cryptocurrency.charts.api.CoinMarketCap.CoinMarketCapApi.CoinMarketCap;
import josiak.android.example.cryptocurrency.charts.api.CryptoCompare.CryptoCompareApi;
import josiak.android.example.cryptocurrency.charts.api.CryptoCompare.CryptoCompareApi.CryptoCompare;
import josiak.android.example.cryptocurrency.charts.api.CryptoCompare.CryptoDetailedResponse;
import josiak.android.example.cryptocurrency.charts.api.CoinMarketCap.CryptoSimpleResponse;
import josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState;
import josiak.android.example.cryptocurrency.charts.data.Crypto;
import josiak.android.example.cryptocurrency.charts.data.CryptoDetailed;
import josiak.android.example.cryptocurrency.charts.data.CryptoFavs;
import josiak.android.example.cryptocurrency.charts.data.CryptoType;
import josiak.android.example.cryptocurrency.charts.data.CryptoSimple;
import josiak.android.example.cryptocurrency.charts.data.CryptoWithFavs;
import josiak.android.example.cryptocurrency.charts.data.CryptoWithNameAndSymbol;
import josiak.android.example.cryptocurrency.charts.database.CryptoLocalCache;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState.FINISHED;
import static josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState.LOADED_ALL;
import static josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState.LOAD_ALL;
import static josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState.LOAD_MORE;
import static josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState.REFRESHING;

/**
 * Created by Jakub on 2018-05-25.
 */

public class PagingBoundaryCallback extends PagedList.BoundaryCallback<CryptoWithFavs> {

    private static final String LOG_TAG = PagingBoundaryCallback.class.getSimpleName();
    private static final String TAG_COIN_MARKET_CAP_API = LOG_TAG + CoinMarketCapApi.class.getSimpleName();
    private static final String TAG_CRYPTO_COMPARE_API = LOG_TAG + CryptoCompareApi.class.getSimpleName();

    private static final int RESULTS_SIZE = 50;
    private static final String TO_SYMBOL = "USD";
    private long timeBeforeFetchingData;
    private static final long TWO_WEEKS_IN_MILLIS = 1000 * 60 * 60 * 24 * 14;

    private CoinMarketCap coinMarketCapApi;
    private CryptoCompare cryptoCompareApi;
    private CryptoLocalCache cache;
    private Context contextForResources;

    private int resultsFromRank = 1;
    private boolean requestingInProgress = false;
    private boolean initialRequest = true;
    private boolean updateAllCoinsPerTwoWeeks = false;

    private HashMap<String, CryptoDetailed> cryptoDetailedHashMap = new HashMap<>();
    private List<CryptoSimple> cryptoSimpleList = new ArrayList<>();
    private List<CryptoDetailed> cryptoDetailedList = new ArrayList<>();
    private List<Crypto> cryptoList = new ArrayList<>();
    private List<CryptoFavs> cryptoFavsList = new ArrayList<>();

    private MutableLiveData<NetworkCallbackState> _fetchingData = new MutableLiveData<>();
    public LiveData<NetworkCallbackState> fetchingData;

    public PagingBoundaryCallback(
            CoinMarketCap coinMarketCapApi,
            CryptoCompare cryptoCompareApi,
            CryptoLocalCache cache,
            Context contextForResources) {
        this.coinMarketCapApi = coinMarketCapApi;
        this.cryptoCompareApi = cryptoCompareApi;
        this.cache = cache;
        this.contextForResources = contextForResources;
        fetchingData = _fetchingData;
    }

    public void refresh() {
        if (!updateAllCoinsPerTwoWeeks && Utilities.isOnline(contextForResources)) {
            initialRequest = true;
            _fetchingData.postValue(REFRESHING);
            resultsFromRank = 1;
        } else {
            _fetchingData.postValue(FINISHED);
        }
        requestAndSaveData();
    }

    @Override
    public void onZeroItemsLoaded() {
        super.onZeroItemsLoaded();
        Log.v("onZeroItemsLoaded", "true");
        requestAndSaveData();
    }

    @Override
    public void onItemAtFrontLoaded(@NonNull CryptoWithFavs itemAtFront) {
        super.onItemAtFrontLoaded(itemAtFront);
        Log.v("onItemAtFrontLoaded", "true");
        if (initialRequest)
            requestAndSaveData();
        Log.v("initialRequestOnFront", String.valueOf(initialRequest));
    }

    @Override
    public void onItemAtEndLoaded(CryptoWithFavs itemAtEnd) {
        super.onItemAtEndLoaded(itemAtEnd);
        Log.v("onItemAtEndLoaded", "true");
        _fetchingData.postValue(LOAD_MORE);
        requestAndSaveData();
    }

    private void requestAndSaveData() {
        Log.v("requestAndSaveData", "start");
        if (requestingInProgress || !Utilities.isOnline(contextForResources)) return;
        timeBeforeFetchingData = System.currentTimeMillis();
        if (initialRequest) {
            setUpdateAllCoinsPerTwoWeeks();
        }
        requestCryptoSimple();
    }

    private void requestCryptoSimple() {
        if (requestingInProgress) return;
        requestingInProgress = true;
        coinMarketCapApi.requestCoins(resultsFromRank, RESULTS_SIZE).enqueue(
                new Callback<CryptoSimpleResponse>() {

                    @Override
                    public void onFailure(Call<CryptoSimpleResponse> call, Throwable t) {
                        Log.d(TAG_COIN_MARKET_CAP_API, "failed to get data");
                        Log.d(TAG_COIN_MARKET_CAP_API, "Unknown error " + t.getMessage());
                        _fetchingData.postValue(FINISHED);

                        requestingInProgress = false;
                    }

                    @Override
                    public void onResponse(Call<CryptoSimpleResponse> call, Response<CryptoSimpleResponse> response) {
                        Log.d(TAG_COIN_MARKET_CAP_API, "got response: " + response.toString());
                        if (response.code() == 404) {
                            Log.v("404", "helloŻurek:)");

                            Log.v("amountOfCryptos", String.valueOf(cache.getAmountOfCryptos()));
                        }
                        if (response.isSuccessful()) {
                            Log.v("data null?", response.body().getItems().toString());
                            if (cryptoSimpleList.size() > 0)
                                cryptoSimpleList.clear();
                            HashMap<String, CryptoSimple> hashMap =
                                    Utilities.replaceSymbolsIncompatibility(response.body().getItems(), contextForResources);

                            //Log.v("Simple Original:", hashMap.toString());
                            HashMap<String, CryptoSimple> sortedHashMap = Utilities.sortCryptoSimpleHashMap(hashMap);
                            //Log.v("Simple Sorted:", sortedHashMap.toString());
                            for (Map.Entry<String, CryptoSimple> entry : sortedHashMap.entrySet()) {
                                CryptoSimple cryptoSimple = entry.getValue();
                                cryptoSimpleList.add(cryptoSimple);
                            }
                            resultsFromRank += RESULTS_SIZE;
                            requestCryptoDetailed();
                        } else {
                            Log.d(TAG_COIN_MARKET_CAP_API, "Unknown error " + response.errorBody().toString());
                            _fetchingData.postValue(FINISHED);
                            if(updateAllCoinsPerTwoWeeks){
                                _fetchingData.postValue(LOADED_ALL);
                                updateAllCoinsPerTwoWeeks = false;
                            }
                            requestingInProgress = false;
                        }
                    }
                }
        );
    }

    private void requestCryptoDetailed() {
        StringBuilder cryptoSymbol = new StringBuilder();

        for (CryptoSimple item : cryptoSimpleList) {
            cryptoSymbol
                    .append(item.getSymbol())
                    .append(",");
        }

        int cryptoSymbolLength = cryptoSymbol.length();
        String fromSymbol = cryptoSymbol.delete(cryptoSymbolLength - 1, cryptoSymbolLength).toString();

        cryptoCompareApi.requestCoins(fromSymbol, TO_SYMBOL).enqueue(new Callback<CryptoDetailedResponse>() {
            @Override
            public void onResponse(Call<CryptoDetailedResponse> call, Response<CryptoDetailedResponse> response) {
                Log.d(TAG_CRYPTO_COMPARE_API, "got response: " + response.toString());
                if (response.isSuccessful()) {
                    if (cryptoDetailedList.size() > 0)
                        cryptoDetailedList.clear();
                    if (cryptoDetailedHashMap.size() > 0)
                        cryptoDetailedHashMap.clear();
                    HashMap<String, HashMap<String, CryptoDetailed>> hashMap = response.body().getList();
                    for (Map.Entry<String, HashMap<String, CryptoDetailed>> entry : hashMap.entrySet()) {
                        for (Map.Entry<String, CryptoDetailed> innerEntry : entry.getValue().entrySet()) {
                            cryptoDetailedHashMap.put(entry.getKey(), innerEntry.getValue());
                        }
                    }
                    //Log.v("Detailed Original", cryptoDetailedHashMap.toString());
                    HashMap<String, CryptoDetailed> sortedHashMap = Utilities.sortCryptoDetailedHashMap(cryptoDetailedHashMap);
                    //Log.v("Detailed Sorted", sortedHashMap.toString());
                    for (Map.Entry<String, CryptoDetailed> entry : sortedHashMap.entrySet()) {
                        cryptoDetailedList.add(entry.getValue());
                    }
                    createCryptoFromResponses(cryptoSimpleList, cryptoDetailedList);
                } else {
                    Log.d(TAG_COIN_MARKET_CAP_API, "Unknown error " + response.errorBody().toString());
                    _fetchingData.postValue(FINISHED);
                    requestingInProgress = false;
                }
            }

            @Override
            public void onFailure(Call<CryptoDetailedResponse> call, Throwable t) {
                Log.d(TAG_CRYPTO_COMPARE_API, "failed to get data");
                Log.d(TAG_CRYPTO_COMPARE_API, "Unknown error " + t.getMessage());
                _fetchingData.postValue(FINISHED);
                requestingInProgress = false;
            }
        });
    }

    private void createCryptoFromResponses(List<CryptoSimple> cryptoSimpleList, List<CryptoDetailed> cryptoDetailedList) {
        if (cryptoList.size() != 0)
            cryptoList.clear();
        if (cryptoFavsList.size() != 0)
            cryptoFavsList.clear();
        for (int i = 0; i < cryptoSimpleList.size() && i < cryptoDetailedList.size(); i++) {
            if (!cryptoSimpleList.get(i).getSymbol().equals(cryptoDetailedList.get(i).getSymbol())) {
                /*Log.v("Data from lists", "at position " + String.valueOf(i) +
                        " doesn't match. CryptoSimpleSymbol vs CryptoDetailedSymbol: " +
                        cryptoSimpleList.get(i).getName() + "(" +
                        cryptoSimpleList.get(i).getSymbol() + ")" + " : " +
                        cryptoDetailedList.get(i).getSymbol());*/
                cryptoSimpleList.remove(i);
                i--;
            } else {
                Crypto cryptoItem = Utilities.cryptoConverter(
                        cryptoSimpleList.get(i),
                        cryptoDetailedList.get(i),
                        System.currentTimeMillis());
                cryptoList.add(cryptoItem);

                CryptoFavs cryptoFavs = new CryptoFavs(cryptoSimpleList.get(i).getId(), 0);
                cryptoFavsList.add(cryptoFavs);
                //Log.v("cryptoItemCompleted", "Position " + String.valueOf(i) + " " + cryptoItem.toString());
            }
        }
        if (cryptoList.size() > 0) {
            cache.insertCoins(cryptoList);
        }
        if(cryptoFavsList.size() > 0) {
            cache.insertCryptoFavourite(cryptoFavsList);
        }
        Log.v(LOG_TAG, "Inserted " + String.valueOf(cryptoList.size()) + " to database");
        /*if (updateAllCoinsPerTwoWeeks && interactiveCryptoList.size() > 0) {
                cache.insertAllCryptosInteractive(interactiveCryptoList);
            Log.v(LOG_TAG, "Inserted InteractiveCrypto " + String.valueOf(interactiveCryptoList.size()) + " to database");
        }*/
        if (initialRequest && Utilities.isOnline(contextForResources)) {
            if (cache.getAmountOfCryptos() > 0)
                cache.markOldData(timeBeforeFetchingData, CryptoType.NEW, CryptoType.OLD, CryptoType.SEARCH);
            initialRequest = false;
            Log.v(LOG_TAG, "Deleted old rows");
        }
        _fetchingData.postValue(FINISHED);
        requestingInProgress = false;
        if (updateAllCoinsPerTwoWeeks && Utilities.isOnline(contextForResources)) {
            requestAndSaveData();
        }
    }

    private void setUpdateAllCoinsPerTwoWeeks() {
        Log.v("amountOfCryptos", String.valueOf(cache.getAmountOfCryptos()));
        if (cache.getAmountOfCryptos() == 0 || (cache.getLastUpdatedCrypto().getInsertedTime() + TWO_WEEKS_IN_MILLIS) <= timeBeforeFetchingData) {
            _fetchingData.postValue(LOAD_ALL);
            Log.v("setAllCoinsPerTwoWeeks", "true");
            updateAllCoinsPerTwoWeeks = true;
            requestCryptoSimple();
        }
    }
}
