package josiak.android.example.cryptocurrency.charts.repository;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.Context;
import android.util.Log;

import josiak.android.example.cryptocurrency.charts.api.CoinMarketCap.CoinMarketCapApi.CoinMarketCap;
import josiak.android.example.cryptocurrency.charts.api.CryptoCompare.CryptoCompareApi.CryptoCompare;
import josiak.android.example.cryptocurrency.charts.data.Crypto;
import josiak.android.example.cryptocurrency.charts.database.CryptoLocalCache;
import josiak.android.example.cryptocurrency.charts.database.CryptoResultFromDatabase;

/**
 * Created by Jakub on 2018-05-25.
 */

public class CryptoRepository {
    private CoinMarketCap coinMarketCapApi;
    private CryptoCompare cryptoCompareApi;
    private CryptoLocalCache cache;
    private Context contextForResources;
    private PagingBoundaryCallback pagingBoundaryCallback;

    private static final int PAGE_SIZE = 50;

    public CryptoRepository(
            CoinMarketCap coinMarketCapApi,
            CryptoCompare cryptoCompareApi,
            CryptoLocalCache cache,
            Context contextForResources) {
        this.coinMarketCapApi = coinMarketCapApi;
        this.cryptoCompareApi = cryptoCompareApi;
        this.cache = cache;
        this.contextForResources = contextForResources;
    }

    public CryptoResultFromDatabase requestCoins() {
        pagingBoundaryCallback =
                new PagingBoundaryCallback(coinMarketCapApi, cryptoCompareApi, cache, contextForResources);
        LiveData<String> fetchingData = pagingBoundaryCallback.fetchingData;

        DataSource.Factory<Integer, Crypto> dataSourceFactory = cache.queryCryptosByRank();

        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
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
}