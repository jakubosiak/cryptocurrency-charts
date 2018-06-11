package josiak.android.example.cryptocurrency.charts.data

import android.arch.persistence.room.TypeConverters
import josiak.android.example.cryptocurrency.charts.database.DataConverter

/**
 * Created by Kuba on 2018-06-12.
 */
data class CryptoWithFavs(
        var id: Long,
        val name: String,
        val symbol: String,
        val rank: Int,
        val price: Float,
        val updatedTime: Long,
        val insertedTime: Long,
        val volume: String,
        val changePercentage: Float,
        val marketCap: String,
        @TypeConverters(value = DataConverter::class)
        val dataType: CryptoType,
        val favourite: Int
)