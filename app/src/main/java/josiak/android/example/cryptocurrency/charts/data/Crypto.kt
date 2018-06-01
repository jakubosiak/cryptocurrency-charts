package josiak.android.example.cryptocurrency.charts.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Created by Jakub on 2018-05-23.
 */
@Entity(tableName = "cryptos")
data class Crypto(
        @PrimaryKey @SerializedName("id") var id: Long,
        @SerializedName("name") val name: String,
        @SerializedName("symbol") val symbol: String,
        @SerializedName("rank") val rank: Int,
        @SerializedName("PRICE") val price: Float,
        @SerializedName("LASTUPDATE") val time: Long,
        @SerializedName("VOLUME24HOURTO") val volume: Float,
        @SerializedName("CHANGEPCT24HOUR") val changePercentage: Float,
        @SerializedName("MKTCAP") val marketCap: Float,
        val favourite: Int
)