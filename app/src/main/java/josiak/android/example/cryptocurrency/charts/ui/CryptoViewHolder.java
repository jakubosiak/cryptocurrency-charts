package josiak.android.example.cryptocurrency.charts.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.lang.reflect.Field;

import josiak.android.example.cryptocurrency.charts.InjectorUtils;
import josiak.android.example.cryptocurrency.charts.R;
import josiak.android.example.cryptocurrency.charts.Utilities;
import josiak.android.example.cryptocurrency.charts.data.CryptoWithFavs;
import josiak.android.example.cryptocurrency.charts.databinding.CryptocurrencyItemBinding;
import josiak.android.example.cryptocurrency.charts.repository.CryptoRepository;

/**
 * Created by Jakub on 2018-05-23.
 */

public class CryptoViewHolder extends RecyclerView.ViewHolder{
    private CryptocurrencyItemBinding binding;
    private CryptoRepository repository;
    int id;

    public CryptoViewHolder(CryptocurrencyItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
        repository = InjectorUtils.provideCryptoRepository(binding.getRoot().getContext());
    }

    public void bind(CryptoWithFavs crypto) {
        setFavIcon(crypto);
        binding.setCrypto(crypto);
        binding.executePendingBindings();

        setCryptoPercentageChange(crypto);
        binding.tvCryptoMarketcap.setText(Utilities.setMarketCap(crypto));

        //setCryptoIcon
        try {
            Field field = R.drawable.class.getDeclaredField(crypto.getSymbol().toLowerCase());
            int id = field.getInt(field);
            Glide.with(binding.imgCryptoIcon).load(id).into(binding.imgCryptoIcon);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Glide.with(binding.imgCryptoIcon).load(R.drawable.icon_question_mark).into(binding.imgCryptoIcon);
        }

        //setFavIcon
        binding.imgFav.setOnClickListener(v -> {
            switch (crypto.getFavourite()) {
                case 0:
                    repository.updateCryptoFavourite(1, crypto.getId());
                    binding.imgFav.setImageResource(R.drawable.ic_full_star);
                    break;
                case 1:
                    repository.updateCryptoFavourite(0, crypto.getId());
                    binding.imgFav.setImageResource(R.drawable.ic_star);
                    break;
            }
        });
    }

    private void setFavIcon(CryptoWithFavs crypto) {
        if (crypto.getFavourite() == 0) {
            binding.imgFav.setImageResource(R.drawable.ic_star);
        } else if (crypto.getFavourite() == 1) {
            binding.imgFav.setImageResource(R.drawable.ic_full_star);
        }
    }

    private void setCryptoPercentageChange(CryptoWithFavs crypto){
        if (crypto.getChangePercentage() > 0.00) {
            binding.tvCryptoChange.setTextColor(binding.tvCryptoChange.getContext().
                    getResources().getColor(R.color.changePercentagePlus));
            binding.imgArrow.setImageResource(R.drawable.arrow_up);
        } else if (crypto.getChangePercentage() < 0.00) {
            binding.tvCryptoChange.setTextColor(binding.tvCryptoChange.getContext().
                    getResources().getColor(R.color.changePercentageMinus));
            binding.imgArrow.setImageResource(R.drawable.arrow_down);
        }
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