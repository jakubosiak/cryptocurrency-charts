package josiak.android.example.cryptocurrency.charts.repository;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.Context;
import android.support.annotation.MainThread;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import josiak.android.example.cryptocurrency.charts.api.CoinMarketCap.CoinMarketCapApi.CoinMarketCap;
import josiak.android.example.cryptocurrency.charts.api.CryptoCompare.CryptoCompareApi.CryptoCompare;
import josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState;
import josiak.android.example.cryptocurrency.charts.data.Crypto;
import josiak.android.example.cryptocurrency.charts.data.CryptoType;
import josiak.android.example.cryptocurrency.charts.data.CryptoWithNameAndSymbol;
import josiak.android.example.cryptocurrency.charts.database.CryptoLocalCache;
import josiak.android.example.cryptocurrency.charts.database.CryptoResultFromDatabase;

/**
 * Created by Jakub on 2018-05-25.
 */

public class CryptoRepository {
    private CoinMarketCap coinMarketCapApi;
    private CryptoCompare cryptoCompareApi;
    private CryptoLocalCache cache;
    private SimpleNetworkCalls simpleNetworkCalls;
    private Context contextForResources;
    private PagingBoundaryCallback pagingBoundaryCallback;

    private static final int PAGE_SIZE = 50;

    public CryptoRepository(
            CoinMarketCap coinMarketCapApi,
            CryptoCompare cryptoCompareApi,
            CryptoLocalCache cache,
            SimpleNetworkCalls simpleNetworkCalls,
            Context contextForResources) {
        this.coinMarketCapApi = coinMarketCapApi;
        this.cryptoCompareApi = cryptoCompareApi;
        this.cache = cache;
        this.simpleNetworkCalls = simpleNetworkCalls;
        this.contextForResources = contextForResources;
    }

    public CryptoResultFromDatabase requestCoins() {
        pagingBoundaryCallback =
                new PagingBoundaryCallback(coinMarketCapApi, cryptoCompareApi, cache, contextForResources);
        LiveData<NetworkCallbackState> fetchingData = pagingBoundaryCallback.fetchingData;

        DataSource.Factory<Integer, Crypto> dataSourceFactory = cache.queryCryptosByRank(CryptoType.NEW, CryptoType.SEARCH);

        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPageSize(PAGE_SIZE)
                .build();

        LiveData<PagedList<Crypto>> pagedListData =
                new LivePagedListBuilder<Integer, Crypto>(dataSourceFactory, pagedListConfig)
                        .setBoundaryCallback(pagingBoundaryCallback)
                        .build();

        return new CryptoResultFromDatabase(pagedListData, fetchingData);
    }

    public void refresh() {
        pagingBoundaryCallback.refresh();
    }

    public LiveData<List<CryptoWithNameAndSymbol>> searchForCryptoNamesAndSymbols() {
        return cache.searchForCryptoNamesAndSymbols();
    }

    public LiveData<List<Crypto>> searchSpecifiedCoin(String searchQuery) {
        simpleNetworkCalls.searchSpecifiedCoin(cache.getCryptoSymbol(searchQuery));
        return cache.querySearchedCoins();
    }
}
