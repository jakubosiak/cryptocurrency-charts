package josiak.android.example.cryptocurrency.charts.data

/**
 * Created by Kuba on 2018-06-08.
 */
data class CryptoUpdate (
        val searchDataType: CryptoType,
        val price: Float,
        val updatedTime: Long,
        val insertedTime: Long,
        val volume: String,
        val changePercentage: Float,
        val marketCap: String,
        val searchQuery: String
)