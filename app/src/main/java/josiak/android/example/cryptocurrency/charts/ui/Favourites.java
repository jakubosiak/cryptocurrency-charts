package josiak.android.example.cryptocurrency.charts.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.navigation.Navigation;
import josiak.android.example.cryptocurrency.charts.InjectorUtils;
import josiak.android.example.cryptocurrency.charts.R;
import josiak.android.example.cryptocurrency.charts.Utilities;
import josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState;
import josiak.android.example.cryptocurrency.charts.databinding.FragmentFavouritesBinding;

import static josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState.FINISHED;
import static josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState.NO_INTERNET;
import static josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState.REFRESHING;

public class Favourites extends Fragment {
    private FragmentFavouritesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourites, container, false);
        CryptoViewModel cryptoViewModel = ViewModelProviders.of(this,
                InjectorUtils.provideMainListViewModelFactory(getContext())).get(CryptoViewModel.class);

        CryptoSimpleAdapter adapter = new CryptoSimpleAdapter();
        binding.list.setAdapter(adapter);
        initSwipeToRefresh(cryptoViewModel, binding.swipeToRefresh);
        cryptoViewModel.getFavouriteCryptos().observe(this, adapter::submitList);
        observeNetworkCallbackState(cryptoViewModel);
        cryptoViewModel.updateFavouriteCryptos();

        return binding.getRoot();
    }

    private void initSwipeToRefresh(CryptoViewModel cryptoViewModel, SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setOnRefreshListener(cryptoViewModel::refreshFavs);
    }

    private void showToastNoInternet(NetworkCallbackState state) {
        if (state == NO_INTERNET)
            Toast.makeText(getContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
    }

    private void observeNetworkCallbackState(CryptoViewModel cryptoViewModel) {
        cryptoViewModel.favsState().observe(this, state -> {
            showToastNoInternet(state);
            if (state == REFRESHING)
                binding.swipeToRefresh.setRefreshing(true);
            else if (state == FINISHED || state == NO_INTERNET)
                binding.swipeToRefresh.setRefreshing(false);
        });
    }
}
