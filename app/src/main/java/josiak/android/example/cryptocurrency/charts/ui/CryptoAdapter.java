package josiak.android.example.cryptocurrency.charts.ui;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import josiak.android.example.cryptocurrency.charts.data.CryptoWithFavs;

/**
 * Created by Jakub on 2018-05-23.
 */

public class CryptoAdapter extends PagedListAdapter<CryptoWithFavs, CryptoViewHolder>{

    private Glide glide;

    public CryptoAdapter(Glide glide) {
        super(diffCallback);
        this.glide = glide;
    }

    @NonNull
    @Override
    public CryptoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return CryptoViewHolder.create(parent, glide);
    }

    @Override
    public void onBindViewHolder(@NonNull CryptoViewHolder holder, int position) {
        CryptoWithFavs item = getItem(position);
        if(item != null) {
            holder.bind(item);
        }
    }

    public static DiffUtil.ItemCallback<CryptoWithFavs> diffCallback = new DiffUtil.ItemCallback<CryptoWithFavs>() {
        @Override
        public boolean areItemsTheSame(CryptoWithFavs oldItem, CryptoWithFavs newItem) {

            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(CryptoWithFavs oldItem, CryptoWithFavs newItem) {
            return oldItem == newItem;
        }
    };
}
