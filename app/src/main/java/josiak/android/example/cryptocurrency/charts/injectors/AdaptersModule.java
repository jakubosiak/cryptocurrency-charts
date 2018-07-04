package josiak.android.example.cryptocurrency.charts.injectors;

import com.bumptech.glide.Glide;

import dagger.Module;
import dagger.Provides;
import josiak.android.example.cryptocurrency.charts.ui.CryptoAdapter;
import josiak.android.example.cryptocurrency.charts.ui.CryptoSimpleAdapter;

/**
 * Created by Kuba on 2018-06-17.
 */

@Module(includes = {GlideModule.class})
public class AdaptersModule {

    @Provides
    @AdaptersScope
    public CryptoAdapter cryptoAdapter(Glide glide){
        return new CryptoAdapter(glide);
    }

    @Provides
    @AdaptersScope
    public CryptoSimpleAdapter cryptoSimpleAdapter(Glide glide){
        return new CryptoSimpleAdapter(glide);
    }
}
