package josiak.android.example.cryptocurrency.charts.data

import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Created by Jakub on 2018-05-25.
 */
data class CryptoSimple(
        @PrimaryKey @SerializedName("id") val id: Long,
        @SerializedName("name") val name: String,
        @SerializedName("symbol") val symbol: String,
        @SerializedName("rank") val rank: Int
)