package josiak.android.example.cryptocurrency.charts.database;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import josiak.android.example.cryptocurrency.charts.data.Crypto;

/**
 * Created by Jakub on 2018-05-25.
 */
@Dao
public interface CryptoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCoins(List<Crypto> cryptoList);

    @Query("SELECT * FROM cryptos ORDER BY rank ASC")
    DataSource.Factory<Integer, Crypto> queryCryptosByRank();

    @Query("DELETE FROM cryptos")
    void deleteCoins();
}
