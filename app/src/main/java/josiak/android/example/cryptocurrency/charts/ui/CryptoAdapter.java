package josiak.android.example.cryptocurrency.charts.ui;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.view.ViewGroup;

import josiak.android.example.cryptocurrency.charts.data.Crypto;
import josiak.android.example.cryptocurrency.charts.database.CryptoResultFromDatabase;

/**
 * Created by Jakub on 2018-05-23.
 */

public class CryptoAdapter extends PagedListAdapter<Crypto, CryptoViewHolder> {

    protected CryptoAdapter() {
        super(diffCallback);
    }

    @NonNull
    @Override
    public CryptoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return CryptoViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull CryptoViewHolder holder, int position) {
        Crypto item = getItem(position);
        if(item != null) {
            holder.bind(item);
        }
    }

    public static DiffUtil.ItemCallback<Crypto> diffCallback = new DiffUtil.ItemCallback<Crypto>() {
        @Override
        public boolean areItemsTheSame(Crypto oldItem, Crypto newItem) {

            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(Crypto oldItem, Crypto newItem) {
            return oldItem == newItem;
        }
    };
}
