package josiak.android.example.cryptocurrency.charts.ui;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.view.ViewGroup;

import josiak.android.example.cryptocurrency.charts.data.Crypto;

/**
 * Created by Jakub on 2018-05-23.
 */

public class CryptoAdapter extends ListAdapter<Crypto, CryptoViewHolder> {

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

    }

    public static DiffUtil.ItemCallback<Crypto> diffCallback = new DiffUtil.ItemCallback<Crypto>() {
        @Override
        public boolean areItemsTheSame(Crypto oldItem, Crypto newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(Crypto oldItem, Crypto newItem) {
            return false;
        }
    };
}
