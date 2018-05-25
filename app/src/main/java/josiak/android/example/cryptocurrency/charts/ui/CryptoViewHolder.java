package josiak.android.example.cryptocurrency.charts.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import josiak.android.example.android.cryptocurrency.charts.R;

/**
 * Created by Jakub on 2018-05-23.
 */

public class CryptoViewHolder extends RecyclerView.ViewHolder {

    public CryptoViewHolder(View itemView) {
        super(itemView);
    }

    public static CryptoViewHolder create(ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cryptocurrency_item, parent, false);
        return new CryptoViewHolder(view);
    }
}