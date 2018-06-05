package josiak.android.example.cryptocurrency.charts.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import josiak.android.example.cryptocurrency.charts.InjectorUtils;
import josiak.android.example.cryptocurrency.charts.R;
import josiak.android.example.cryptocurrency.charts.data.Crypto;
import josiak.android.example.cryptocurrency.charts.database.CryptoResultFromDatabase;
import josiak.android.example.cryptocurrency.charts.databinding.CryptocurrencyItemBindingImpl;
import josiak.android.example.cryptocurrency.charts.databinding.FragmentCryptocurrencyMainListBinding;

public class CryptocurrencyMainList extends Fragment {
    private boolean reachedLastItemInList = false;
    private String mFetchingData = "false";
    private FragmentCryptocurrencyMainListBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate
                (inflater, R.layout.fragment_cryptocurrency_main_list, container, false);
        View view = binding.getRoot();

        MainListViewModel viewModel = ViewModelProviders.of(this,
                InjectorUtils.provideMainListViewModelFactory(getContext()))
                .get(MainListViewModel.class);
        CryptoAdapter adapter = new CryptoAdapter();
        viewModel.init(getString(R.string.init_CryptoResultFromDatabase));

        binding.list.setAdapter(adapter);
        setupOnScrollListener(binding.list, binding.swipeRefreshLayout);
        initSwipeToRefresh(viewModel, binding.swipeRefreshLayout);

        viewModel.cryptoPagedList.observe(this, adapter::submitList);
        viewModel.fetchingData.observe(this, fetchingData -> {
                    mFetchingData = fetchingData;
                    changeProgressBarVisibility(fetchingData, binding.swipeRefreshLayout, reachedLastItemInList);
                }
        );
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setupOnScrollListener(RecyclerView list, SwipeRefreshLayout swipeRefreshLayout) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) list.getLayoutManager();
        list.setOnScrollChangeListener((view, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int totalItemCount = layoutManager.getItemCount();
            int visibleItemCount = layoutManager.getChildCount();
            int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
            reachedLastItemInList = visibleItemCount + firstVisibleItem >= totalItemCount;
            changeProgressBarVisibility(mFetchingData, swipeRefreshLayout, reachedLastItemInList);
        });
    }

    private void changeProgressBarVisibility(String fetchingData, SwipeRefreshLayout swipeRefreshLayout, boolean reachedLastItemInList) {
        if (fetchingData.equals(getString(R.string.fetching_data_refreshing))) {
            swipeRefreshLayout.setRefreshing(true);
        } else if (reachedLastItemInList && fetchingData.equals(getString(R.string.fetching_data_reached_end_list))) {
            binding.progressBarFetchingData.setVisibility(View.VISIBLE);
        } else if (fetchingData.equals(getString(R.string.fetching_data_false))) {
            swipeRefreshLayout.setRefreshing(false);
            binding.progressBarFetchingData.setVisibility(View.GONE);
        }
    }

    private void initSwipeToRefresh(MainListViewModel viewModel, SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setOnRefreshListener(viewModel::refreshList);
    }
}
