package josiak.android.example.cryptocurrency.charts.injectors;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Kuba on 2018-06-17.
 */

@Module
public class ApplicationContextModule {

    private Context context;

    public ApplicationContextModule(Context context) {
        this.context = context;
    }

    @Provides
    @AdaptersScope
    public Context context(){
        return context;
    }
}
