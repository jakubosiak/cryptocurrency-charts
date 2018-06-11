package josiak.android.example.cryptocurrency.charts.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;

import java.util.List;

import josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState;
import josiak.android.example.cryptocurrency.charts.data.CryptoWithNameAndSymbol;
import josiak.android.example.cryptocurrency.charts.repository.CryptoRepository;
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

    private MutableLiveData<String> trigger = new MutableLiveData<>();
    private LiveData<CryptoResultFromDatabase> cryptoResultFromDatabaseLiveData =
            Transformations.map(trigger, trigger ->
                    repository.requestCoins()
            );

    public LiveData<PagedList<Crypto>> cryptoPagedList =
            Transformations.switchMap(cryptoResultFromDatabaseLiveData, CryptoResultFromDatabase::getPagedListData);

    public LiveData<NetworkCallbackState> fetchingData =
            Transformations.switchMap(cryptoResultFromDatabaseLiveData, CryptoResultFromDatabase::getFetchingData);

    public void init(String init) {
        trigger.postValue(init);
    }

    public void refreshList() {
        repository.refresh();
    }

    public LiveData<List<CryptoWithNameAndSymbol>> searchForCryptoNamesAndSymbols() {
        return repository.searchForCryptoNamesAndSymbols();
    }

    public LiveData<List<Crypto>> searchSpecifiedCoin(String searchQuery) {
       return repository.searchSpecifiedCoin(searchQuery);
    }
}
