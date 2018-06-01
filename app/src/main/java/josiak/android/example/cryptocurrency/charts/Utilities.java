package josiak.android.example.cryptocurrency.charts;

import android.arch.persistence.room.util.StringUtil;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import josiak.android.example.cryptocurrency.charts.data.CryptoDetailed;
import josiak.android.example.cryptocurrency.charts.data.CryptoSimple;

/**
 * Created by Kuba on 2018-05-31.
 */

public class Utilities {
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

    public static HashMap<String, CryptoSimple> replaceSymbolsIncompatibility(HashMap<String, CryptoSimple> hashMap, Context context){
        String[] symbolConflictList = context.getResources().getStringArray(R.array.symbolConflictList);
        int symbolConflictListLength = symbolConflictList.length;
        String[] symbolKeys = new String[symbolConflictListLength];
        List<CryptoSimple> cryptoNewCustomSymbolList = new ArrayList<>();
        for (Map.Entry<String, CryptoSimple> entryExceptions : hashMap.entrySet()) {
            for(int i = 0; i<symbolConflictListLength; i++){
                if(entryExceptions.getValue().getSymbol().equals(symbolConflictList[i])){
                    symbolKeys[i] = entryExceptions.getKey();
                    cryptoNewCustomSymbolList.add(new CryptoSimple(entryExceptions.getValue().getId(),
                            entryExceptions.getValue().getName(),
                            context.getResources().getStringArray(R.array.symbolConflictNewValues)[i],
                            entryExceptions.getValue().getRank()));
                }
            }
        }
        for(int j = 0; j<cryptoNewCustomSymbolList.size(); j++) {
            hashMap.remove(symbolKeys[j]);
            hashMap.put(cryptoNewCustomSymbolList.get(j).getSymbol(), cryptoNewCustomSymbolList.get(j));
        }
        return hashMap;
    }
}
