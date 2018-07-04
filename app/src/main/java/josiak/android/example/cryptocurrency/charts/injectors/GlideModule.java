package josiak.android.example.cryptocurrency.charts.injectors;

import android.content.Context;

import com.bumptech.glide.Glide;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Kuba on 2018-06-17.
 */

@Module(includes = ApplicationContextModule.class)
public class GlideModule {

    @Provides
    @AdaptersScope
    public Glide glide(Context context){
        return Glide.get(context);
    }
}
