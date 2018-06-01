package josiak.android.example.cryptocurrency.charts.api;

import android.arch.paging.PagedList;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import josiak.android.example.cryptocurrency.charts.Utilities;
import josiak.android.example.cryptocurrency.charts.api.CoinMarketCapApi.CoinMarketCap;
import josiak.android.example.cryptocurrency.charts.api.CryptoCompareApi.CryptoCompare;
import josiak.android.example.cryptocurrency.charts.data.Crypto;
import josiak.android.example.cryptocurrency.charts.data.CryptoDetailed;
import josiak.android.example.cryptocurrency.charts.data.CryptoSimple;
import josiak.android.example.cryptocurrency.charts.database.CryptoLocalCache;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jakub on 2018-05-25.
 */

public class PagingBoundaryCallback extends PagedList.BoundaryCallback<Crypto> {

    private static final String LOG_TAG = PagingBoundaryCallback.class.getSimpleName();
    private static final String TAG_COIN_MARKET_CAP_API = LOG_TAG + CoinMarketCapApi.class.getSimpleName();
    private static final String TAG_CRYPTO_COMPARE_API = LOG_TAG + CryptoCompareApi.class.getSimpleName();

    private static final int RESULTS_SIZE = 50;
    private static final String TO_SYMBOL = "USD";
    private static final int FAVOURITE_FALSE = 0;

    private CoinMarketCap coinMarketCapApi;
    private CryptoCompare cryptoCompareApi;
    private CryptoLocalCache cache;
    private Context contextForResources;

    private int resultsFromRank = 1;
    private boolean requestingInProgress = false;

    private HashMap<String, CryptoDetailed> cryptoDetailedHashMap = new HashMap<>();
    private List<CryptoSimple> cryptoSimpleList = new ArrayList<>();
    private List<CryptoDetailed> cryptoDetailedList = new ArrayList<>();
    private List<Crypto> cryptoList = new ArrayList<>();

    public PagingBoundaryCallback(
            CoinMarketCap coinMarketCapApi,
            CryptoCompare cryptoCompareApi,
            CryptoLocalCache cache,
            Context contextForResources) {
        this.coinMarketCapApi = coinMarketCapApi;
        this.cryptoCompareApi = cryptoCompareApi;
        this.cache = cache;
        this.contextForResources = contextForResources;
    }

    @Override
    public void onZeroItemsLoaded() {
        super.onZeroItemsLoaded();
        requestAndSaveData();
    }

    @Override
    public void onItemAtEndLoaded(Crypto itemAtEnd) {
        super.onItemAtEndLoaded(itemAtEnd);
        requestAndSaveData();
    }

    private void requestAndSaveData() {
        if (requestingInProgress) return;

        requestingInProgress = true;
        requestCryptoSimple();
    }

    private void requestCryptoSimple() {
        coinMarketCapApi.requestCoins(resultsFromRank, RESULTS_SIZE).enqueue(
                new Callback<CryptoSimpleResponse>() {

                    @Override
                    public void onFailure(Call<CryptoSimpleResponse> call, Throwable t) {
                        Log.d(TAG_COIN_MARKET_CAP_API, "failed to get data");
                        Log.d(TAG_COIN_MARKET_CAP_API, "Unknown error " + t.getMessage());
                    }

                    @Override
                    public void onResponse(Call<CryptoSimpleResponse> call, Response<CryptoSimpleResponse> response) {
                        Log.d(TAG_COIN_MARKET_CAP_API, "got response: " + response.toString());
                        if (response.isSuccessful()) {
                            if(cryptoSimpleList.size() > 0)
                            cryptoSimpleList.clear();
                            HashMap<String, CryptoSimple> hashMap =
                                    Utilities.replaceSymbolsIncompatibility(response.body().getItems(), contextForResources);

                            Log.v("Simple Original:", hashMap.toString());
                            HashMap<String, CryptoSimple> sortedHashMap = Utilities.sortCryptoSimpleHashMap(hashMap);
                            Log.v("Simple Sorted:", sortedHashMap.toString());
                            for (Map.Entry<String, CryptoSimple> entry : sortedHashMap.entrySet()) {
                                CryptoSimple cryptoSimple = entry.getValue();
                                cryptoSimpleList.add(cryptoSimple);
                            }
                            resultsFromRank += RESULTS_SIZE;
                            requestCryptoDetailed();
                        } else {
                            Log.d(TAG_COIN_MARKET_CAP_API, "Unknown error " + response.errorBody().toString());
                        }
                    }
                }
        );
    }

    private void requestCryptoDetailed() {
        StringBuilder cryptoSymbol = new StringBuilder(RESULTS_SIZE);

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
                    if(cryptoDetailedList.size() > 0)
                        cryptoDetailedList.clear();
                    if(cryptoDetailedHashMap.size() > 0)
                        cryptoDetailedHashMap.clear();
                    HashMap<String, HashMap<String, CryptoDetailed>> hashMap = response.body().getList();
                    for (Map.Entry<String, HashMap<String, CryptoDetailed>> entry : hashMap.entrySet()) {
                        for (Map.Entry<String, CryptoDetailed> innerEntry : entry.getValue().entrySet()) {
                            cryptoDetailedHashMap.put(entry.getKey(), innerEntry.getValue());
                        }
                    }
                    Log.v("Detailed Original", cryptoDetailedHashMap.toString());
                    HashMap<String, CryptoDetailed> sortedHashMap = Utilities.sortCryptoDetailedHashMap(cryptoDetailedHashMap);
                    Log.v("Detailed Sorted", sortedHashMap.toString());
                    for (Map.Entry<String, CryptoDetailed> entry : sortedHashMap.entrySet()) {
                        cryptoDetailedList.add(entry.getValue());
                    }
                    createCryptoFromResponses(cryptoSimpleList, cryptoDetailedList);
                } else {
                    Log.d(TAG_COIN_MARKET_CAP_API, "Unknown error " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<CryptoDetailedResponse> call, Throwable t) {
                Log.d(TAG_CRYPTO_COMPARE_API, "failed to get data");
                Log.d(TAG_CRYPTO_COMPARE_API, "Unknown error " + t.getMessage());
            }
        });
    }

    private void createCryptoFromResponses(List<CryptoSimple> cryptoSimpleList, List<CryptoDetailed> cryptoDetailedList) {
        if (cryptoList.size() != 0)
            cryptoList.clear();
        Log.v("cryptoSimpleList", String.valueOf(cryptoSimpleList.size()));
        Log.v("cryptoDetailedList", String.valueOf(cryptoDetailedList.size()));
        for (int i = 0; i < cryptoSimpleList.size() && i < cryptoDetailedList.size(); i++) {
            if (!cryptoSimpleList.get(i).getSymbol().equals(cryptoDetailedList.get(i).getSymbol())) {
                Log.v("Data from lists", "at position " + String.valueOf(i) +
                        " doesn't match. CryptoSimpleSymbol vs CryptoDetailedSymbol: " +
                        cryptoSimpleList.get(i).getName() + "(" +
                        cryptoSimpleList.get(i).getSymbol() + ")" + " : " +
                        cryptoDetailedList.get(i).getSymbol());
                cryptoSimpleList.remove(i);
                i--;
            } else {
                Crypto cryptoItem = new Crypto(
                        cryptoSimpleList.get(i).getId(),
                        cryptoSimpleList.get(i).getName(),
                        cryptoSimpleList.get(i).getSymbol(),
                        cryptoSimpleList.get(i).getRank(),
                        cryptoDetailedList.get(i).getPrice(),
                        cryptoDetailedList.get(i).getTime(),
                        cryptoDetailedList.get(i).getVolume(),
                        cryptoDetailedList.get(i).getChangePercentage(),
                        cryptoDetailedList.get(i).getMarketCap(),
                        FAVOURITE_FALSE
                );
                cryptoList.add(cryptoItem);
                Log.v("cryptoItemCompleted", "Position " + String.valueOf(i) + " " + cryptoItem.toString());
            }
        }
        if (cryptoList.size() > 0)
            cache.insertCoins(cryptoList);
        Log.v(LOG_TAG, "Inserted " + String.valueOf(cryptoList.size()) + " to database");
        requestingInProgress = false;
    }
}
