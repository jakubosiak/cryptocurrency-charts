package josiak.android.example.cryptocurrency.charts.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.util.Log;

import josiak.android.example.cryptocurrency.charts.CryptoRepository;
import josiak.android.example.cryptocurrency.charts.data.Crypto;
import josiak.android.example.cryptocurrency.charts.database.CryptoResultFromDatabase;

/**
 * Created by Jakub on 2018-05-25.
 */

public class MainListViewModel extends ViewModel {

    private CryptoRepository repository;

    public MainListViewModel(CryptoRepository repository) {
        this.repository = repository;
    }

    private LiveData<PagedList<Crypto>> cryptoPagedList;
    private LiveData<Boolean> fetchingData;
    //private CryptoResultFromDatabase cryptoResultFromDatabaseLiveData;

    public LiveData<PagedList<Crypto>> getCryptoPagedList() {
        if (repository != null && cryptoPagedList == null) {
            cryptoPagedList = repository.requestCoins().getPagedListData();
        }
        return cryptoPagedList;
    }

    public LiveData<Boolean> isFetchingData() {
        if (repository != null && fetchingData == null) {
            return fetchingData = repository.refresh();
        }
        return fetchingData;
    }

/*    public void getResultsFromDataBase() {
        if (repository != null)
            cryptoResultFromDatabaseLiveData = repository.requestCoins();
    }*/

    public void refreshList() {
        if (repository != null)
            repository.refresh();
    }

}
