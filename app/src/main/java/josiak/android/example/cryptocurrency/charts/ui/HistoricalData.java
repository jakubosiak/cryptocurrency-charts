package josiak.android.example.cryptocurrency.charts.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.IHighlighter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import josiak.android.example.cryptocurrency.charts.InjectorUtils;
import josiak.android.example.cryptocurrency.charts.R;
import josiak.android.example.cryptocurrency.charts.Utilities;
import josiak.android.example.cryptocurrency.charts.data.CryptoHistoricalData;
import josiak.android.example.cryptocurrency.charts.databinding.FragmentHistoricalDataBinding;
import josiak.android.example.cryptocurrency.charts.radio_button.RadioButton;
import josiak.android.example.cryptocurrency.charts.radio_button.RadioGroup;

/**
 * Created by Kuba on 2018-06-18.
 */

public class HistoricalData extends Fragment implements RadioGroup.OnCheckedChangeListener {
    private static final String HISTORY_DAY = "histoday";
    private static final String HISTORY_HOUR = "histohour";
    private static final String HISTORY_MINUTE = "histominute";
    private static final int HISTORY_MINUTE_TIME_PERIOD = 15;
    private static final int LIMIT_MAX = 5000;
    private static final int LIMIT_24H = 24 * 4;
    private static final String HISTORY_DAILY_DATE_PATTERN = "MMM/yy";
    private static final String HISTORY_HOURLY_DATE_PATTERN = "hh:mm";

    private FragmentHistoricalDataBinding binding;
    private CryptoViewModel cryptoViewModel;
    private List<Entry> entryList = new ArrayList<>();
    private List<String> listXAxisValues = new ArrayList<>();
    private String symbol;
    private SimpleDateFormat simpleDateFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_historical_data, container, false);
        cryptoViewModel = ViewModelProviders.of(this,
                InjectorUtils.provideViewModelFactory(getContext())).get(CryptoViewModel.class);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(getContext().getString(R.string.argument_symbol))) {
            symbol = getArguments().get(getContext().getString(R.string.argument_symbol)).toString();
            binding.tvId.setText(symbol);
            binding.radioGroup.setOnCheckedChangeListener(this);
            cryptoViewModel.searchHistoricalData(symbol, HISTORY_DAY, LIMIT_MAX, 0);
            setupCryptoHistoricalData(symbol, HISTORY_DAILY_DATE_PATTERN);
        }
    }

    private synchronized void setupCryptoHistoricalData(String symbol, String daterFormatPattern) {
        simpleDateFormat = new SimpleDateFormat(daterFormatPattern);
        cryptoViewModel.getCryptoHistoricalData(symbol).observe(this, list -> {
            if (entryList.size() > 0)
                entryList.clear();
            if (listXAxisValues.size() > 0)
                listXAxisValues.clear();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    String time = String.valueOf(list.get(i).getTime()) + "000";
                    float closeValue = list.get(i).getClose();
                    long timeLong = Long.parseLong(time);
                    Date date = new Date(timeLong);
                    Entry entry = new Entry(i, closeValue);
                    listXAxisValues.add(simpleDateFormat.format(date));
                    entryList.add(entry);
                }

                LineDataSet set = new LineDataSet(entryList, "Label");
                set.setDrawFilled(true);
                set.setDrawCircles(false);
                set.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                set.setFillDrawable(ContextCompat.getDrawable(getContext(), R.drawable.blue_gradient));
                set.setHighLightColor(Color.MAGENTA);

                LineData data = new LineData(set);
                Log.v("dataSize", String.valueOf(set.getEntryCount()));
                data.setValueTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
                binding.chart.setData(data);
                binding.chart.getDescription().setEnabled(false);
                binding.chart.setTouchEnabled(true);
                binding.chart.setDragEnabled(true);
                binding.chart.setScaleEnabled(true);
                binding.chart.setPinchZoom(false);
                Legend legend = binding.chart.getLegend();
                legend.setEnabled(false);

                IAxisValueFormatter valueFormatter = ((value, axis) -> {
                    if ((int) value >= listXAxisValues.size()) {
                        return null;
                    } else {
                        Log.v("listXAxisValues", listXAxisValues.get((int) value));
                        return listXAxisValues.get((int) value);
                    }
                });
                XAxis xAxis = binding.chart.getXAxis();
                xAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setValueFormatter(valueFormatter);

                YAxis yAxisLeft = binding.chart.getAxisLeft();
                YAxis yAxisRight = binding.chart.getAxisRight();
                yAxisLeft.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor));
                IAxisValueFormatter yAxisValueFormatter = (value, axis) -> "$" + Utilities.convertPrice(value);
                yAxisLeft.setValueFormatter(yAxisValueFormatter);
                yAxisRight.setEnabled(false);

                binding.chart.invalidate();
            }
        });
    }

    private void getCryptoHistoricalData(String symbol, String daterFormatPattern){
        simpleDateFormat = new SimpleDateFormat(daterFormatPattern);
        cryptoViewModel.getCryptoHistoricalData(symbol);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_historical_data, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_zoom_out:
                binding.chart.fitScreen();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(View radioGroup, View radioButton, boolean isChecked, int checkedId) {
        switch (((RadioButton) radioButton).getValue()) {
            case "24H":
                binding.tvClose.setText(((RadioButton) radioButton).getValue());
                cryptoViewModel.searchHistoricalData(symbol, HISTORY_MINUTE, LIMIT_24H, HISTORY_MINUTE_TIME_PERIOD);
                getCryptoHistoricalData(symbol, HISTORY_HOURLY_DATE_PATTERN);
                break;
            case "7D":
                break;
            case "1M":
                break;
            case "3M":
                break;
            case "MAX":
                break;
        }
    }
}
