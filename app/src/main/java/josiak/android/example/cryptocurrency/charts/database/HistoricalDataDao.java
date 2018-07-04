package josiak.android.example.cryptocurrency.charts.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import josiak.android.example.cryptocurrency.charts.data.CryptoHistoricalData;
import retrofit2.http.DELETE;

@Dao
public interface HistoricalDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCryptoHistoricalData(List<CryptoHistoricalData> cryptoHistoricalData);

    @Query("SELECT * FROM cryptoHistorical WHERE cryptoSymbol LIKE :cryptoSymbol")
    LiveData<List<CryptoHistoricalData>> getCryptoHistoricalData(String cryptoSymbol);

    @Query("DELETE FROM cryptoHistorical WHERE cryptoSymbol LIKE :cryptoSymbol")
    void deleteOldHistoricalData(String cryptoSymbol);
}
