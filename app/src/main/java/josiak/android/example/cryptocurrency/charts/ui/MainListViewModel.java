package josiak.android.example.cryptocurrency.charts.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;

import josiak.android.example.cryptocurrency.charts.CryptoRepository;
import josiak.android.example.cryptocurrency.charts.data.Crypto;

/**
 * Created by Jakub on 2018-05-25.
 */

public class MainListViewModel extends ViewModel {

    private CryptoRepository repository;

    public MainListViewModel(CryptoRepository repository) {
        this.repository = repository;
    }

    private LiveData<PagedList<Crypto>> cryptoPagedList;

    public LiveData<PagedList<Crypto>> getCryptoPagedList() {
        if(repository != null){
            cryptoPagedList = repository.requestCoins().getPagedListData();
        }
        return cryptoPagedList;
    }
}
