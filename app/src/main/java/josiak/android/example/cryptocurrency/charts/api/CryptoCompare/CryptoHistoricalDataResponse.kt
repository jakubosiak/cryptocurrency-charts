package josiak.android.example.cryptocurrency.charts.api.CryptoCompare

import com.google.gson.annotations.SerializedName
import josiak.android.example.cryptocurrency.charts.data.CryptoHistoricalData

data class CryptoHistoricalDataResponse(
        @SerializedName("Data") val data: List<CryptoHistoricalData> = emptyList()
)