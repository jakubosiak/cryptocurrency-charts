package josiak.android.example.cryptocurrency.charts.ui;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import josiak.android.example.cryptocurrency.charts.data.CryptoWithFavs;

/**
 * Created by Kuba on 2018-06-08.
 */

public class CryptoSimpleAdapter extends ListAdapter<CryptoWithFavs, CryptoViewHolder> {

    private Glide glide;

    public CryptoSimpleAdapter(Glide glide) {
        super(CryptoAdapter.diffCallback);
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
        if(item != null){
            holder.bind(item);
        }
    }
}
