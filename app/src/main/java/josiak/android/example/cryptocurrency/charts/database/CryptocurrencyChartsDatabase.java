package josiak.android.example.cryptocurrency.charts.database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import josiak.android.example.cryptocurrency.charts.data.Crypto;
import josiak.android.example.cryptocurrency.charts.data.CryptoFavs;

/**
 * Created by Jakub on 2018-05-25.
 */

@Database(
        entities = {Crypto.class, CryptoFavs.class},
        version = 1,
        exportSchema = false
)
public abstract class CryptocurrencyChartsDatabase extends RoomDatabase {

    private static final String LOG_TAG = CryptocurrencyChartsDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "cryptocurrency";
    private static final Object LOCK = new Object();
    private static CryptocurrencyChartsDatabase singletonInstance;

    public static CryptocurrencyChartsDatabase getInstance(Context context) {
        Log.d(LOG_TAG, "get instance of database");
        if (singletonInstance == null) {
            synchronized (LOCK) {
                singletonInstance = Room.databaseBuilder(context.getApplicationContext(),
                        CryptocurrencyChartsDatabase.class, DATABASE_NAME).build();
            }
        }
        return singletonInstance;
    }

    public abstract CryptoDao cryptoDao();
    public abstract FavsDao favsDao();
}
