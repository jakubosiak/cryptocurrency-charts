package josiak.android.example.cryptocurrency.charts.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.lang.reflect.Field;

import josiak.android.example.cryptocurrency.charts.R;
import josiak.android.example.cryptocurrency.charts.data.Crypto;
import josiak.android.example.cryptocurrency.charts.databinding.CryptocurrencyItemBinding;

/**
 * Created by Jakub on 2018-05-23.
 */

public class CryptoViewHolder extends RecyclerView.ViewHolder {
    private CryptocurrencyItemBinding binding;

    public CryptoViewHolder(CryptocurrencyItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Crypto crypto){
            binding.setCrypto(crypto);
            binding.executePendingBindings();
            int identifier = binding.imgCryptoIcon.getContext().getResources().getIdentifier("btc", "drawable", binding.imgCryptoIcon.getContext().getPackageName());
            try {
                Field field = R.drawable.class.getDeclaredField("btc");
                int id = field.getInt(field);
                //icon.setImageResource(id);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            binding.imgCryptoIcon.setImageResource(R.drawable.ic_star_bottom_nav);
    }

    public static CryptoViewHolder create(ViewGroup parent) {
        CryptocurrencyItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.cryptocurrency_item,
                parent,
                false);
        return new CryptoViewHolder(binding);
    }
}