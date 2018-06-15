package josiak.android.example.cryptocurrency.charts;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import josiak.android.example.cryptocurrency.charts.data.Crypto;
import josiak.android.example.cryptocurrency.charts.data.CryptoDetailed;
import josiak.android.example.cryptocurrency.charts.data.CryptoSimple;
import josiak.android.example.cryptocurrency.charts.data.CryptoType;
import josiak.android.example.cryptocurrency.charts.data.CryptoUpdate;
import josiak.android.example.cryptocurrency.charts.data.CryptoWithFavs;

/**
 * Created by Kuba on 2018-05-31.
 */

public class Utilities {
    private static DecimalFormat dfTo6Places = new DecimalFormat("#.######");
    private static DecimalFormat dfTo2Places = new DecimalFormat("#.##");
    private static StringBuilder marketCapReverse = new StringBuilder();
    private static StringBuilder marketCap = new StringBuilder();
    private static final String fiatCurrency = "$";

    public static HashMap<String, CryptoSimple> sortCryptoSimpleHashMap(HashMap<String, CryptoSimple> unsortedMap) {
        List<Map.Entry<String, CryptoSimple>> list = new LinkedList<>(unsortedMap.entrySet());
        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, CryptoSimple>>() {
            public int compare(Map.Entry<String, CryptoSimple> o1,
                               Map.Entry<String, CryptoSimple> o2) {
                return o1.getValue().getSymbol().compareTo(o2.getValue().getSymbol());
            }
        });
        // Maintaining insertion order with the help of LinkedList
        HashMap<String, CryptoSimple> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, CryptoSimple> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static HashMap<String, CryptoDetailed> sortCryptoDetailedHashMap(HashMap<String, CryptoDetailed> unsortedMap) {
        List<Map.Entry<String, CryptoDetailed>> list = new LinkedList<>(unsortedMap.entrySet());
        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, CryptoDetailed>>() {
            public int compare(Map.Entry<String, CryptoDetailed> o1,
                               Map.Entry<String, CryptoDetailed> o2) {
                return o1.getValue().getSymbol().compareTo(o2.getValue().getSymbol());
            }
        });
        // Maintaining insertion order with the help of LinkedList
        HashMap<String, CryptoDetailed> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, CryptoDetailed> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static HashMap<String, CryptoSimple> replaceSymbolsIncompatibility(
            HashMap<String, CryptoSimple> hashMap, Context context) {
        String[] symbolConflictList = context.getResources().getStringArray(R.array.symbolConflictList);
        int symbolConflictListLength = symbolConflictList.length;
        String[] symbolKeys = new String[symbolConflictListLength];
        List<CryptoSimple> cryptoNewCustomSymbolList = new ArrayList<>();
        for (Map.Entry<String, CryptoSimple> entryExceptions : hashMap.entrySet()) {
            for (int i = 0; i < symbolConflictListLength; i++) {
                if (entryExceptions.getValue().getSymbol().equals(symbolConflictList[i])) {
                    symbolKeys[i] = entryExceptions.getKey();
                    cryptoNewCustomSymbolList.add(new CryptoSimple(entryExceptions.getValue().getId(),
                            entryExceptions.getValue().getName(),
                            context.getResources().getStringArray(R.array.symbolConflictNewValues)[i],
                            entryExceptions.getValue().getRank()));
                }
            }
        }
        for (int j = 0; j < cryptoNewCustomSymbolList.size(); j++) {
            hashMap.remove(symbolKeys[j]);
            hashMap.put(cryptoNewCustomSymbolList.get(j).getSymbol(), cryptoNewCustomSymbolList.get(j));
        }
        return hashMap;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static Crypto cryptoConverter(CryptoSimple cryptoSimple, CryptoDetailed cryptoDetailed, long insertedTime) {
        long id = cryptoSimple.getId();
        String name = cryptoSimple.getName();
        String symbol = cryptoSimple.getSymbol();
        int rank = cryptoSimple.getRank();
        final float price;
        long updatedTime = cryptoDetailed.getTime();
        String volume = cryptoDetailed.getVolume().split("\\.")[0];
        float changePercentage = Float.parseFloat(dfTo2Places.format(cryptoDetailed.getChangePercentage()));
        String marketCap = cryptoDetailed.getMarketCap().split("\\.")[0];

        if (cryptoDetailed.getPrice() < 1.00) {
            price = Float.parseFloat(dfTo6Places.format(cryptoDetailed.getPrice()));
        } else {
            price = Float.parseFloat(dfTo2Places.format(cryptoDetailed.getPrice()));
        }

        return new Crypto(id, name, symbol, rank, price, updatedTime,
                insertedTime, volume, changePercentage, marketCap, CryptoType.NEW);
    }

    public static CryptoUpdate cryptoUpdateConverter(CryptoType dataType, CryptoDetailed crypto, String searchQuery) {
        final float price;
        long updatedTime = crypto.getTime();
        long insertedTime = System.currentTimeMillis();
        String volume = crypto.getVolume().split("\\.")[0];
        float changePercentage = Float.parseFloat(dfTo2Places.format(crypto.getChangePercentage()));
        String marketCap = crypto.getMarketCap().split("\\.")[0];

        if (crypto.getPrice() < 1.00) {
            price = Float.parseFloat(dfTo6Places.format(crypto.getPrice()));
        } else {
            price = Float.parseFloat(dfTo2Places.format(crypto.getPrice()));
        }

        return new CryptoUpdate(dataType, price, updatedTime,
                insertedTime, volume, changePercentage, marketCap, searchQuery);
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static String setMarketCap(CryptoWithFavs crypto) {
        marketCapReverse.setLength(0);
        marketCap.setLength(0);
        for (int i = crypto.getMarketCap().length() - 1; i >= 0; i--) {
            marketCapReverse.append(crypto.getMarketCap().charAt(i));
            if (marketCapReverse.length() == 3 ||
                    (marketCapReverse.length() >= 7 && marketCapReverse.length() % 4 == 3)) {
                marketCapReverse.append(" ");
            }
        }
        for (int i = marketCapReverse.length() - 1; i >= 0; i--) {
            marketCap.append(marketCapReverse.toString().charAt(i));
        }
        if (String.valueOf(marketCap.charAt(0)).equals(" "))
            marketCap.deleteCharAt(0);
        return fiatCurrency + marketCap.toString();
    }
}
