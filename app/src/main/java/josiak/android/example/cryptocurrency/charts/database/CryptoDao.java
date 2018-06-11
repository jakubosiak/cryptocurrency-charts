package josiak.android.example.cryptocurrency.charts.database;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import java.util.List;

import josiak.android.example.cryptocurrency.charts.data.Crypto;
import josiak.android.example.cryptocurrency.charts.data.CryptoType;
import josiak.android.example.cryptocurrency.charts.data.CryptoWithNameAndSymbol;

/**
 * Created by Jakub on 2018-05-25.
 */
@Dao
@TypeConverters(DataConverter.class)
public interface CryptoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCoins(List<Crypto> cryptoList);

    @Query("SELECT * FROM cryptos WHERE dataType = :dataType OR dataType = :dataTypeSearch ORDER BY rank ASC")
    DataSource.Factory<Integer, Crypto> queryCryptosByRank(CryptoType dataType, CryptoType dataTypeSearch);

    @Query("SELECT name, symbol FROM cryptos")
    LiveData<List<CryptoWithNameAndSymbol>> searchForCryptoNamesAndSymbols();

    @Query("SELECT * FROM cryptos ORDER BY insertedTime ASC, id ASC LIMIT 1")
    Crypto getLastUpdatedCrypto();

    @Query("SELECT COUNT(*) FROM cryptos")
    int amountOfCryptos();

    @Query("UPDATE cryptos SET dataType = :oldDataType WHERE (dataType = :newDataType OR dataType = :searchDataType) AND insertedTime < :timeBeforeFetching")
    void markOldData(long timeBeforeFetching, CryptoType newDataType, CryptoType oldDataType, CryptoType searchDataType);

    @Query("SELECT * FROM cryptos WHERE dataType = :searchDataType")
    LiveData<List<Crypto>> querySearchedCoins(CryptoType searchDataType);

    @Query("SELECT symbol FROM cryptos WHERE symbol LIKE :searchQuery or name LIKE :searchQuery")
    String getCryptoSymbol(String searchQuery);

    @Query("UPDATE cryptos " +
            "SET " +
            "dataType = :searchDataType, " +
            "price = :price, " +
            "updatedTime = :updatedTime, " +
            "insertedTime = :insertedTime, " +
            "volume = :volume, " +
            "changePercentage = :changePercentage, " +
            "marketCap = :marketCap " +
            "WHERE symbol LIKE :searchQuery OR name LIKE :searchQuery")
    void updatedDataAfterSearch(
            CryptoType searchDataType,
            float price,
            long updatedTime,
            long insertedTime,
            String volume,
            float changePercentage,
            String marketCap,
            String searchQuery);
}
