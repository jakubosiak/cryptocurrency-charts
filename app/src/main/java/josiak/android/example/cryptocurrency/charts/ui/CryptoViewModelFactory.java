package josiak.android.example.cryptocurrency.charts.ui;

import android.arch.lifecycle.ViewModelProvider;

import josiak.android.example.cryptocurrency.charts.repository.CryptoRepository;

/**
 * Created by Jakub on 2018-05-25.
 */

public class CryptoViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private CryptoRepository repository;

    public CryptoViewModelFactory(CryptoRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends android.arch.lifecycle.ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new CryptoViewModel(repository);
    }
}
