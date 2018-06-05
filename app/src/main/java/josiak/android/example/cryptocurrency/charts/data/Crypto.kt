package josiak.android.example.cryptocurrency.charts.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Created by Jakub on 2018-05-23.
 */
@Entity(tableName = "cryptos")
data class Crypto(
        @PrimaryKey var id: Long,
        val name: String,
        val symbol: String,
        val rank: Int,
        val price: Float,
        val time: Long,
        val volume: String,
        val changePercentage: Float,
        val marketCap: String
)