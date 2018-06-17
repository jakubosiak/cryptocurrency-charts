package josiak.android.example.cryptocurrency.charts.dagger2;

import dagger.Component;
import josiak.android.example.cryptocurrency.charts.ui.CryptoAdapter;
import josiak.android.example.cryptocurrency.charts.ui.CryptoSimpleAdapter;

/**
 * Created by Kuba on 2018-06-17.
 */

@AdaptersScope
@Component(modules = {AdaptersModule.class})
public interface AdaptersComponent {

    CryptoAdapter getCryptoAdapter();

    CryptoSimpleAdapter getCryptoSimpleAdapter();
}
