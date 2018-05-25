package josiak.android.example.cryptocurrency.charts.api

import com.google.gson.annotations.SerializedName
import josiak.android.example.cryptocurrency.charts.data.CryptoDetailed

/**
 * Created by Jakub on 2018-05-25.
 */
data class CryptoDetailedResponse(
        @SerializedName("RAW") val list: List<CryptoDetailed> = emptyList()
)