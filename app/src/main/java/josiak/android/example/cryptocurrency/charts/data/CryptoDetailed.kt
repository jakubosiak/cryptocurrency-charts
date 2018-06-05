package josiak.android.example.cryptocurrency.charts.data

import com.google.gson.annotations.SerializedName

/**
 * Created by Jakub on 2018-05-25.
 */
data class CryptoDetailed(
        @SerializedName("FROMSYMBOL") val symbol: String,
        @SerializedName("PRICE") val price: Float,
        @SerializedName("LASTUPDATE") val time: Long,
        @SerializedName("VOLUME24HOURTO") val volume: String,
        @SerializedName("CHANGEPCT24HOUR") val changePercentage: Float,
        @SerializedName("MKTCAP") val marketCap: String
)