package josiak.android.example.cryptocurrency.charts.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
        }
    }

    public static CryptoViewHolder create(ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cryptocurrency_item, parent, false);
        return new CryptoViewHolder(view);
    }
}