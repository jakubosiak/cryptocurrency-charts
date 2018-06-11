package josiak.android.example.cryptocurrency.charts.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import josiak.android.example.cryptocurrency.charts.database.DataConverter

/**
 * Created by Jakub on 2018-05-23.
 */
@Entity(tableName = "cryptos")
@TypeConverters(DataConverter::class)
data class Crypto(
        @PrimaryKey var id: Long,
        val name: String,
        val symbol: String,
        val rank: Int,
        val price: Float,
        val updatedTime: Long,
        val insertedTime: Long,
        val volume: String,
        val changePercentage: Float,
        val marketCap: String,
        val dataType: CryptoType,
        val favourite: Int
)