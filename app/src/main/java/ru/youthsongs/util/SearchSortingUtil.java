package ru.youthsongs.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ru.youthsongs.entity.Song;

public class SearchSortingUtil {

    public static List<Song> sortListByLevenstaindistanse(List<Song> rawSongs, String query) {
        Map<Song, Integer> songs = new HashMap<>();
        for (Song rawSong : rawSongs) {
            songs.put(rawSong, getLevenstainDistanse(rawSong.getName(), query));
        }
        Map<Song, Integer> sortedMap = sortByValue(songs);
        return new ArrayList<>(sortedMap.keySet());
    }

    private static Map<Song, Integer> sortByValue(Map<Song, Integer> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<Song, Integer>> list =
                new LinkedList<Map.Entry<Song, Integer>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<Song, Integer>>() {
            public int compare(Map.Entry<Song, Integer> o1,
                               Map.Entry<Song, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<Song, Integer> sortedMap = new LinkedHashMap<Song, Integer>();
        for (Map.Entry<Song, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    // Implementation of Levenstain algorithm.
    private static int getLevenstainDistanse(String str1, String str2) {
        int[] Di_1 = new int[str2.length() + 1];
        int[] Di = new int[str2.length() + 1];

        for (int j = 0; j <= str2.length(); j++) {
            Di[j] = j; // (i == 0)
        }

        for (int i = 1; i <= str1.length(); i++) {
            System.arraycopy(Di, 0, Di_1, 0, Di_1.length);

            Di[0] = i; // (j == 0)
            for (int j = 1; j <= str2.length(); j++) {
                int cost = (str1.charAt(i - 1) != str2.charAt(j - 1)) ? 1 : 0;
                Di[j] = min(
                        Di_1[j] + 1,
                        Di[j - 1] + 1,
                        Di_1[j - 1] + cost
                );
            }
        }
        return Di[Di.length - 1];
    }

    private static int min(int n1, int n2, int n3) {
        return Math.min(Math.min(n1, n2), n3);
    }
}
