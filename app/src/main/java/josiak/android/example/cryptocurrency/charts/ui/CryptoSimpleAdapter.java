package josiak.android.example.cryptocurrency.charts.ui;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import josiak.android.example.cryptocurrency.charts.data.Crypto;
import josiak.android.example.cryptocurrency.charts.data.CryptoWithFavs;

/**
 * Created by Kuba on 2018-06-08.
 */

public class CryptoSimpleAdapter extends ListAdapter<CryptoWithFavs, CryptoViewHolder> {


    protected CryptoSimpleAdapter() {
        super(CryptoAdapter.diffCallback);
    }

    @NonNull
    @Override
    public CryptoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return CryptoViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull CryptoViewHolder holder, int position) {
        CryptoWithFavs item = getItem(position);
        if(item != null){
            holder.bind(item);
        }
    }
}
