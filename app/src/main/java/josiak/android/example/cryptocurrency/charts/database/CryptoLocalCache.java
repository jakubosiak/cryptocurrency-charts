package josiak.android.example.cryptocurrency.charts.database;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import josiak.android.example.cryptocurrency.charts.AppExecutors;
import josiak.android.example.cryptocurrency.charts.data.Crypto;
import josiak.android.example.cryptocurrency.charts.data.CryptoFavs;
import josiak.android.example.cryptocurrency.charts.data.CryptoType;
import josiak.android.example.cryptocurrency.charts.data.CryptoUpdate;
import josiak.android.example.cryptocurrency.charts.data.CryptoWithFavs;
import josiak.android.example.cryptocurrency.charts.data.CryptoWithNameAndSymbol;

/**
 * Created by Jakub on 2018-05-25.
 */

public class CryptoLocalCache {
    private AppExecutors executors;
    private CryptoDao cryptoDao;
    private FavsDao favsDao;

    public CryptoLocalCache(CryptoDao cryptoDao, FavsDao favsDao, AppExecutors executors) {
        this.executors = executors;
        this.cryptoDao = cryptoDao;
        this.favsDao = favsDao;
    }

    public void insertCoins(List<Crypto> cryptoList) {
        executors.diskIO().execute(() ->
                cryptoDao.insertCoins(cryptoList)
        );
    }

    public DataSource.Factory<Integer, CryptoWithFavs> queryCryptosByRank(
            CryptoType cryptoType, CryptoType dataTypeSearch) {
        return cryptoDao.queryCryptosByRank(cryptoType, dataTypeSearch);
    }

    public LiveData<List<CryptoWithNameAndSymbol>> searchForCryptoNamesAndSymbols() {
        Future<LiveData<List<CryptoWithNameAndSymbol>>> namesAndSymbols =
                executors.withCallback().submit(cryptoDao::searchForCryptoNamesAndSymbols);
        try {
            return namesAndSymbols.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Crypto getLastUpdatedCrypto() {
        Future<Crypto> crypto = executors.withCallback().submit(cryptoDao::getLastUpdatedCrypto);
        try {
            return crypto.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getAmountOfCryptos() {
        Future<Integer> count = executors.withCallback().submit(cryptoDao::amountOfCryptos);
        try {
            return count.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void markOldData(
            long timeBeforeFetchingData,
            CryptoType newDataType,
            CryptoType oldDataType,
            CryptoType searchDataType) {
        executors.diskIO().execute(() ->
                cryptoDao.markOldData(timeBeforeFetchingData, newDataType, oldDataType, searchDataType)
        );
    }

    public LiveData<List<CryptoWithFavs>> querySearchedCoins() {
        return cryptoDao.querySearchedCoins(CryptoType.SEARCH);
    }

    public String getCryptoSymbol(String searchQuery) {
        Future<String> symbol = executors.withCallback().submit(() ->
                cryptoDao.getCryptoSymbol(searchQuery)
        );
        try {
            return symbol.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateDataAfterSearch(CryptoUpdate cryptoUpdate) {
        executors.diskIO().execute(() ->
                cryptoDao.updatedDataAfterSearch(
                        cryptoUpdate.getSearchDataType(),
                        cryptoUpdate.getPrice(),
                        cryptoUpdate.getUpdatedTime(),
                        cryptoUpdate.getInsertedTime(),
                        cryptoUpdate.getVolume(),
                        cryptoUpdate.getChangePercentage(),
                        cryptoUpdate.getMarketCap(),
                        cryptoUpdate.getSearchQuery())
        );
    }

    public LiveData<List<CryptoWithFavs>> getFavouriteCryptos() {
        return cryptoDao.getFavouriteCryptos();
    }

    public void insertCryptoFavourite(List<CryptoFavs> favs) {
        executors.diskIO().execute(() ->
                favsDao.insertCryptoFavourite(favs)
        );
    }

    public void updateCryptoFavourite(int favourite, long id) {
        executors.diskIO().execute(() ->
                favsDao.updateCryptoFavourite(favourite, id)
        );
    }

    public List<String> getFavourites() {
        Future<List<String>> symbols = executors.withCallback().submit(() ->
                favsDao.getFavourites()
        );
        try {
            return symbols.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
