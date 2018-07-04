package josiak.android.example.cryptocurrency.charts.data

import android.arch.persistence.room.*
import com.google.gson.annotations.SerializedName

@Entity(tableName = "cryptoHistorical",
        foreignKeys = [ForeignKey(
                entity = Crypto::class,
                parentColumns = ["id"],
                childColumns = ["cryptoHistoricalId"])],
        indices = [Index("cryptoHistoricalId")])
data class CryptoHistoricalData(
        @ColumnInfo(name = "cryptoHistoricalId") val cryptoId: Long? = null,
        val cryptoSymbol: String? = null,
        @SerializedName("time") val time: Long,
        @SerializedName("close") val close: Float,
        @SerializedName("high") val high: Float,
        @SerializedName("low") val low: Float,
        @SerializedName("open") val open: Float
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}