package josiak.android.example.cryptocurrency.charts.radio_button;

import android.view.View;
import android.widget.Checkable;

/**
 * Created by Kuba on 2018-06-26.
 */

public interface RadioCheckable extends Checkable {
    void addOnCheckChangeListener(OnCheckedChangeListener onCheckedChangeListener);
    void removeOnCheckChangeListener(OnCheckedChangeListener onCheckedChangeListener);

    interface OnCheckedChangeListener{
        void onCheckedChanged(View buttonView);
    }
}
