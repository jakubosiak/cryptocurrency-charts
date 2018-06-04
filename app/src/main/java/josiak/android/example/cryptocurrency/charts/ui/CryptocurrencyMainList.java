package josiak.android.example.cryptocurrency.charts.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
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

public class CryptocurrencyMainList extends Fragment {
    private boolean reachedLastItemInList = false;
    private ProgressBar progressBarFetchingData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cryptocurrency_main_list, container, false);

        MainListViewModel viewModel = ViewModelProviders.of(this,
                InjectorUtils.provideMainListViewModelFactory(getContext()))
                .get(MainListViewModel.class);
        viewModel.init(getString(R.string.init_CryptoResultFromDatabase));

        RecyclerView list = view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(layoutManager);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressBarFetchingData = view.findViewById(R.id.progress_bar_fetching_data);
        setupOnScrollListener(list);

        CryptoAdapter adapter = new CryptoAdapter();
        list.setAdapter(adapter);

        viewModel.cryptoPagedList.observe(this, adapter::submitList);
        viewModel.fetchingData.observe(this, fetchingData ->
                changeProgressBarVisibility(fetchingData, swipeRefreshLayout)
        );
        initSwipeToRefresh(viewModel, swipeRefreshLayout);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setupOnScrollListener(RecyclerView list) {
        list.setOnScrollChangeListener((view, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            LinearLayoutManager layoutManager = (LinearLayoutManager) list.getLayoutManager();
            int totalItemCount = layoutManager.getItemCount();
            int visibleItemCount = layoutManager.getChildCount();
            int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
            reachedLastItemInList = visibleItemCount + firstVisibleItem >= totalItemCount;
        });
    }

    private void changeProgressBarVisibility(String fetchingData, SwipeRefreshLayout swipeRefreshLayout) {
        if (fetchingData.equals(getString(R.string.fetching_data_refreshing))) {
            swipeRefreshLayout.setRefreshing(true);
        } else if (reachedLastItemInList && fetchingData.equals(getString(R.string.fetching_data_reached_end_list))) {
            progressBarFetchingData.setVisibility(View.VISIBLE);
        } else if (fetchingData.equals(getString(R.string.fetching_data_false))) {
            swipeRefreshLayout.setRefreshing(false);
            progressBarFetchingData.setVisibility(View.GONE);
        }
    }

    private void initSwipeToRefresh(MainListViewModel viewModel, SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setOnRefreshListener(viewModel::refreshList);
    }
}
