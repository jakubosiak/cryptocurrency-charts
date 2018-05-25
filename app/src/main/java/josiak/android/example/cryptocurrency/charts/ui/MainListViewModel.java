package josiak.android.example.cryptocurrency.charts.ui;

import android.arch.lifecycle.ViewModel;

import josiak.android.example.cryptocurrency.charts.CryptoRepository;

/**
 * Created by Jakub on 2018-05-25.
 */

public class MainListViewModel extends ViewModel {

    private CryptoRepository repository;

    public MainListViewModel(CryptoRepository repository) {
        this.repository = repository;
    }


}
