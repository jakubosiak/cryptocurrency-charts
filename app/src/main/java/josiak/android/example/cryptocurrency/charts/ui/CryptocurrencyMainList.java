package josiak.android.example.cryptocurrency.charts.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import josiak.android.example.cryptocurrency.charts.InjectorUtils;
import josiak.android.example.cryptocurrency.charts.R;
import josiak.android.example.cryptocurrency.charts.Utilities;
import josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState;
import josiak.android.example.cryptocurrency.charts.dagger2.AdaptersComponent;
import josiak.android.example.cryptocurrency.charts.dagger2.ApplicationContextModule;
import josiak.android.example.cryptocurrency.charts.dagger2.DaggerAdaptersComponent;
import josiak.android.example.cryptocurrency.charts.data.CryptoWithNameAndSymbol;
import josiak.android.example.cryptocurrency.charts.databinding.FragmentCryptocurrencyMainListBinding;

import static josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState.FINISHED;
import static josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState.LOADED_ALL;
import static josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState.LOAD_ALL;
import static josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState.LOAD_MORE;
import static josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState.NO_INTERNET;
import static josiak.android.example.cryptocurrency.charts.api.NetworkCallbackState.REFRESHING;

public class CryptocurrencyMainList extends Fragment {
    private boolean reachedLastItemInList = false;
    private NetworkCallbackState mFetchingData = FINISHED;
    private FragmentCryptocurrencyMainListBinding binding;
    private List<String> searchSuggestionsList = new ArrayList<>();
    private CryptoViewModel cryptoViewModel;
    private CryptoAdapter adapter;
    private CryptoSimpleAdapter simpleAdapter;
    private MaterialDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate
                (inflater, R.layout.fragment_cryptocurrency_main_list, container, false);
        View view = binding.getRoot();
        binding.setMainList(this);

        AdaptersComponent adaptersComponent = DaggerAdaptersComponent.builder()
                .applicationContextModule(new ApplicationContextModule(getContext()))
                .build();

        adapter = adaptersComponent.getCryptoAdapter();
        simpleAdapter = adaptersComponent.getCryptoSimpleAdapter();

        binding.list.setAdapter(adapter);

        cryptoViewModel = ViewModelProviders.of(this,
                InjectorUtils.provideMainListViewModelFactory(getContext()))
                .get(CryptoViewModel.class);
        cryptoViewModel.init(getString(R.string.init_CryptoResultFromDatabase));


        setupOnScrollListener(binding.list, binding.swipeRefreshLayout);
        setupSearchField(binding.searchField);
        initSwipeToRefresh(cryptoViewModel, binding.swipeRefreshLayout);
        getCryptoNamesAndSymbols(cryptoViewModel);
        setupPagedList(cryptoViewModel);
        observeNetworkCallbackState(cryptoViewModel);
        getCryptosBySearchType(cryptoViewModel);

        setHasOptionsMenu(true);
        return view;
    }

    private void showToastNoInternet(NetworkCallbackState state) {
        if (state == NO_INTERNET) {
            Toast.makeText(getContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    private void observeNetworkCallbackState(CryptoViewModel cryptoViewModel){
        cryptoViewModel.fetchingData.observe(this, fetchingData -> {
                    mFetchingData = fetchingData;
                    showToastNoInternet(fetchingData);
                    changeProgressBarVisibility(fetchingData, binding.swipeRefreshLayout, reachedLastItemInList);
                    showProgressDialogWhenGettingAllCoins(fetchingData);
                }
        );
    }

    private void getCryptosBySearchType(CryptoViewModel cryptoViewModel){
        cryptoViewModel.cryptosBySearchType().observe(this, cryptoWithFavs -> {
                    if (binding.searchField.getVisibility() == View.VISIBLE) {
                        binding.list.setAdapter(simpleAdapter);
                        simpleAdapter.submitList(cryptoWithFavs);
                    }
                }
        );
    }

    private void getCryptoNamesAndSymbols(CryptoViewModel cryptoViewModel) {
        cryptoViewModel.searchForCryptoNamesAndSymbols().observe(this, namesAndSymbols -> {
            if (namesAndSymbols != null) {
                if (searchSuggestionsList.size() > 0)
                    searchSuggestionsList.clear();
                for (CryptoWithNameAndSymbol entry : namesAndSymbols) {
                    searchSuggestionsList.add(entry.getName());
                    searchSuggestionsList.add(entry.getSymbol());
                }
                ArrayAdapter<String> searchAdapter = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        searchSuggestionsList);
                binding.searchField.setAdapter(searchAdapter);
            }
        });
    }

    private void setupPagedList(CryptoViewModel cryptoViewModel) {
        cryptoViewModel.cryptoPagedList.observe(this, adapter::submitList);
    }

    private void setupOnScrollListener(RecyclerView list, SwipeRefreshLayout swipeRefreshLayout) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) list.getLayoutManager();
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int totalItemCount = layoutManager.getItemCount();
                int visibleItemCount = layoutManager.getChildCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                reachedLastItemInList = visibleItemCount + firstVisibleItem >= totalItemCount;
                changeProgressBarVisibility(mFetchingData, swipeRefreshLayout, reachedLastItemInList);
            }
        });
    }

    private void changeProgressBarVisibility(
            NetworkCallbackState state,
            SwipeRefreshLayout swipeRefreshLayout,
            boolean reachedLastItemInList) {
        if (state == REFRESHING) {
            swipeRefreshLayout.setRefreshing(true);
        } else if (reachedLastItemInList && state == LOAD_MORE) {
            binding.progressBarFetchingData.setVisibility(View.VISIBLE);
        } else if (state == FINISHED || state == NO_INTERNET) {
            swipeRefreshLayout.setRefreshing(false);
            binding.progressBarFetchingData.setVisibility(View.GONE);
        }
    }

    private void initSwipeToRefresh(CryptoViewModel cryptoViewModel, SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (binding.searchField.getVisibility() == View.VISIBLE) {
                swipeRefreshLayout.setRefreshing(false);
                return;
            }
            cryptoViewModel.refreshList();
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_button:
                if (binding.searchField.getVisibility() == View.VISIBLE) {
                    searchSpecifiedCoin(binding.searchField.getText().toString().trim());
                    return true;
                } else {
                    if (((MainActivity) getActivity()) != null) {
                        int toolbarHeight = ((MainActivity) getActivity()).getSupportActionBar().getHeight();
                        binding.searchField.setHeight(toolbarHeight);
                    }
                    binding.imgExitSearchField.setVisibility(View.VISIBLE);
                    binding.searchField.setVisibility(View.VISIBLE);
                    return true;
                }
            case android.R.id.home:
                binding.list.scrollToPosition(0);
                return true;
            case R.id.get_all_coins:
                cryptoViewModel.updateAllCoins();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void closeSearchField(View view) {
        binding.searchField.setText("");
        binding.searchField.clearFocus();
        binding.list.setAdapter(adapter);
        Utilities.hideKeyboard(getActivity());
        binding.imgExitSearchField.setVisibility(View.GONE);
        binding.searchField.setVisibility(View.GONE);
    }

    private void setupSearchField(AutoCompleteTextView textView) {
        textView.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String searchQuery = textView.getText().toString().trim();
                searchSpecifiedCoin(searchQuery);
                return true;
            }
            return false;
        });
    }

    private void searchSpecifiedCoin(String searchQuery) {
        cryptoViewModel.searchSpecifiedCoin(searchQuery);
    }

    private void showProgressDialogWhenGettingAllCoins(NetworkCallbackState state) {
        if (state == LOAD_ALL) {
            setMaterialDialog().show();
        } else if (state == LOADED_ALL) {
            setMaterialDialog().dismiss();
        }
    }

    private MaterialDialog setMaterialDialog() {
        if (progressDialog == null)
            progressDialog = new MaterialDialog.Builder(getContext())
                    .title(R.string.get_all_coins)
                    .content(R.string.please_wait)
                    .progress(true, 0)
                    .build();
        return progressDialog;
    }


}
