package com.cmzsoft.weather.Utils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class WeatherUtil {
    public static String getLocationFromAddressLines(String add) {
        List<String> list = Arrays.asList(add.split(","));
        int startIdx = Math.max(0, list.size() - 3);
        String s = "";
        for (int i = startIdx; i < list.size(); i++) {
            if (s.equals("")) {
                s += list.get(i);
            } else {
                s += ", " + list.get(i);
            }
        }
        return s;
    }

    public static String getTitleAddress(String add) {
        List<String> list = Arrays.asList(add.split(","));
        if (list.size() > 1) return list.get(1);
        return list.get(0);
    }

    public static String getDayOfWeek(String dateStr) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate date = LocalDate.parse(dateStr, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            switch (date.getDayOfWeek().getValue()) {
                case 1:
                    return "Th 2";
                case 2:
                    return "Th 3";
                case 3:
                    return "Th 4";
                case 4:
                    return "Th 5";
                case 5:
                    return "Th 6";
                case 6:
                    return "Th 7";
                case 7:
                    return "CN";
            }
        }
        return "";
    }
}