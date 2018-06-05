package josiak.android.example.cryptocurrency.charts.database;

import android.arch.paging.DataSource;

import java.util.List;

import josiak.android.example.cryptocurrency.charts.AppExecutors;
import josiak.android.example.cryptocurrency.charts.data.Crypto;
import josiak.android.example.cryptocurrency.charts.database.CryptoDao;

/**
 * Created by Jakub on 2018-05-25.
 */

public class CryptoLocalCache {
    private AppExecutors executors;
    private CryptoDao cryptoDao;

    public CryptoLocalCache(CryptoDao cryptoDao, AppExecutors executors) {
        this.executors = executors;
        this.cryptoDao = cryptoDao;
    }

    public void deleteCoins() {
        executors.diskIO().execute(() ->
                cryptoDao.deleteCoins()
        );
    }

    public void deleteCoinsBelowRank50() {
        executors.diskIO().execute(() ->
                cryptoDao.deleteCoinsBelowRank50()
        );
    }

    public void deleteOldCoinsData(long timeBeforeFetchingData) {
        executors.diskIO().execute(() ->
                cryptoDao.deleteOldCoinsData(timeBeforeFetchingData)
        );
    }

    public void insertCoins(List<Crypto> cryptoList) {
        executors.diskIO().execute(() ->
                cryptoDao.insertCoins(cryptoList)
        );
    }

    public DataSource.Factory<Integer, Crypto> queryCryptosByRank() {
        return cryptoDao.queryCryptosByRank();
    }
}
