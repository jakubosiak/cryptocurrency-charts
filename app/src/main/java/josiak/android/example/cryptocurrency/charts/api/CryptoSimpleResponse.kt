package josiak.android.example.cryptocurrency.charts.api

import com.google.gson.annotations.SerializedName
import josiak.android.example.cryptocurrency.charts.data.Crypto
import josiak.android.example.cryptocurrency.charts.data.CryptoSimple

/**
 * Created by Jakub on 2018-05-25.
 */
data class CryptoSimpleResponse(
       @SerializedName("data") val items: HashMap<String, CryptoSimple>
)