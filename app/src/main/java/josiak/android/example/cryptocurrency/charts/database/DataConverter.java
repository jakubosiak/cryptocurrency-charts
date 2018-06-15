package josiak.android.example.cryptocurrency.charts.database;

import josiak.android.example.cryptocurrency.charts.data.CryptoType;

import android.arch.persistence.room.TypeConverter;

/**
 * Created by Kuba on 2018-06-08.
 */

public class DataConverter {
    @TypeConverter
    public CryptoType toEnum(int integer) {
        switch (integer) {
            case 0:
                return CryptoType.NEW;
            case 1:
                return CryptoType.OLD;
            case 2:
                return CryptoType.SEARCH;
            default:
                return CryptoType.NEW;
        }
    }

    @TypeConverter
    public int toNumber(CryptoType cryptoType) {
        switch (cryptoType) {
            case NEW:
                return 0;
            case OLD:
                return 1;
            case SEARCH:
                return 2;
            default:
                return 0;
        }
    }
}
