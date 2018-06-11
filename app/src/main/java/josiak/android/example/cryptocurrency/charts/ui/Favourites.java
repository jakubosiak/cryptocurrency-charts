package josiak.android.example.cryptocurrency.charts.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import josiak.android.example.cryptocurrency.charts.InjectorUtils;
import josiak.android.example.cryptocurrency.charts.R;
import josiak.android.example.cryptocurrency.charts.databinding.FragmentFavouritesBinding;

public class Favourites extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentFavouritesBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourites, container, false);
        CryptoViewModel cryptoViewModel = ViewModelProviders.of(this,
                InjectorUtils.provideMainListViewModelFactory(getContext())).get(CryptoViewModel.class);

        CryptoSimpleAdapter adapter = new CryptoSimpleAdapter();
        binding.list.setAdapter(adapter);
        cryptoViewModel.getFavouriteCryptos().observe(this, adapter::submitList);
        return binding.getRoot();
    }
}
