package josiak.android.example.cryptocurrency.charts.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import josiak.android.example.cryptocurrency.charts.data.CryptoFavs;

/**
 * Created by Kuba on 2018-06-12.
 */

@Dao
public interface FavsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCryptoFavourite(List<CryptoFavs> favs);

    @Query("UPDATE favsCrypto SET favourite = :favourite WHERE id = :id")
    void updateCryptoFavourite(int favourite, long id);
}
