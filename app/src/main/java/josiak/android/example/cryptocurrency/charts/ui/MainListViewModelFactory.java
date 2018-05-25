package josiak.android.example.cryptocurrency.charts.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import josiak.android.example.cryptocurrency.charts.CryptoRepository;

/**
 * Created by Jakub on 2018-05-25.
 */

public class MainListViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private CryptoRepository repository;

    public MainListViewModelFactory(CryptoRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MainListViewModel(repository);
    }
}
