package josiak.android.example.cryptocurrency.charts.database

import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList
import josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState
import josiak.android.example.cryptocurrency.charts.data.CryptoWithFavs

/**
 * Created by Kuba on 2018-05-29.
 */
data class CryptoResultFromDatabase(
        val pagedListData: LiveData<PagedList<CryptoWithFavs>>,
        val fetchingData: LiveData<NetworkCallbackState>
)