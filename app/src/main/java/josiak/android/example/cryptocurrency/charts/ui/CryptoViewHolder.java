package josiak.android.example.cryptocurrency.charts.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;

import josiak.android.example.cryptocurrency.charts.R;
import josiak.android.example.cryptocurrency.charts.data.Crypto;

/**
 * Created by Jakub on 2018-05-23.
 */

public class CryptoViewHolder extends RecyclerView.ViewHolder {

    TextView name = itemView.findViewById(R.id.tv_crypto_name);
    TextView symbol = itemView.findViewById(R.id.tv_crypto_symbol);
    TextView marketCap = itemView.findViewById(R.id.tv_crypto_marketcap);
    TextView price = itemView.findViewById(R.id.tv_crypto_price);
    TextView change24pct = itemView.findViewById(R.id.tv_crypto_change);
    ImageView icon = itemView.findViewById(R.id.img_crypto_icon);

    public CryptoViewHolder(View itemView) {
        super(itemView);
    }

    public void bind(Crypto crypto){
        if(crypto != null) {
            name.setText(crypto.getName());
            symbol.setText(crypto.getSymbol());
            marketCap.setText(String.valueOf(crypto.getMarketCap()));
            price.setText(String.valueOf(crypto.getPrice()));
            change24pct.setText(String.valueOf(crypto.getChangePercentage()));
            int identifier = icon.getContext().getResources().getIdentifier("btc", "drawable", icon.getContext().getPackageName());
            try {
                Field field = R.drawable.class.getDeclaredField("btc");
                int id = field.getInt(field);
                //icon.setImageResource(id);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            icon.setImageResource(R.drawable.ic_star_bottom_nav);
        }
    }

    public static CryptoViewHolder create(ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cryptocurrency_item, parent, false);
        return new CryptoViewHolder(view);
    }
}