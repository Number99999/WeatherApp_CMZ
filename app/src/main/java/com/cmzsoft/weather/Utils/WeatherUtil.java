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

    public static String degreeToShortDirection(double degree) {
        String[] directions = {"B",    // Bắc
                "BĐB",  // Bắc Đông Bắc
                "ĐB",   // Đông Bắc
                "ĐĐB",  // Đông Đông Bắc
                "Đ",    // Đông
                "ĐĐN",  // Đông Đông Nam
                "ĐN",   // Đông Nam
                "NĐN",  // Nam Đông Nam
                "N",    // Nam
                "NTN",  // Nam Tây Nam
                "TN",   // Tây Nam
                "TTN",  // Tây Tây Nam
                "T",    // Tây
                "TTB",  // Tây Tây Bắc
                "TB",   // Tây Bắc
                "BTB"   // Bắc Tây Bắc
        };
        int idx = (int) Math.round(degree / 22.5) % 16;
        if (idx < 0) idx += 16;
        return directions[idx];
    }

    public static String getWeatherStatus(int weatherCode) {
        switch (weatherCode) {
            case 0:
                return "Trời quang đãng";
            case 1:
                return "Chủ yếu trời quang";
            case 2:
                return "Có mây rải rác";
            case 3:
                return "Nhiều mây";
            case 45:
                return "Sương mù";
            case 48:
                return "Sương mù đóng băng";
            case 51:
                return "Mưa phùn nhẹ";
            case 53:
                return "Mưa phùn vừa";
            case 55:
                return "Mưa phùn nặng";
            case 56:
                return "Mưa phùn đóng băng nhẹ";
            case 57:
                return "Mưa phùn đóng băng nặng";
            case 61:
                return "Mưa nhẹ";
            case 63:
                return "Mưa vừa";
            case 65:
                return "Mưa nặng";
            case 66:
                return "Mưa đóng băng nhẹ";
            case 67:
                return "Mưa đóng băng nặng";
            case 71:
                return "Tuyết nhẹ";
            case 73:
                return "Tuyết vừa";
            case 75:
                return "Tuyết nặng";
            case 77:
                return "Tuyết viên nhỏ";
            case 80:
                return "Mưa rào nhẹ";
            case 81:
                return "Mưa rào vừa";
            case 82:
                return "Mưa rào nặng";
            case 85:
                return "Tuyết rào nhẹ";
            case 86:
                return "Tuyết rào nặng";
            case 95:
                return "Dông";
            case 96:
                return "Dông kèm mưa đá nhẹ";
            case 99:
                return "Dông kèm mưa đá nặng";
            default:
                return "Không xác định";
        }
    }


    public static String getWeatherIconName(int code, boolean isDay) {
        switch (code) {
            case 0:
                return isDay ? "sunny.png" : "night_clear.png";
            case 1:
                return isDay ? "sunny.png" : "night_partly_cloudy_moon.png";
            case 2:
            case 3:
                return isDay ? "day_partly_cloudy.png" : "night_partly_cloudy_moon_1.png";
            case 45:
            case 48:
                return isDay ? "day_partly_cloudy.png" : "night_few_clouds.png";
            case 51:
            case 53:
            case 55:
            case 56:
            case 57: // Mưa phùn, mưa phùn băng
                return isDay ? "day_partly_cloudy.png" : "rain.png";
            case 61:
            case 63:
            case 65:
            case 66:
            case 67: // Mưa các loại
                return isDay ? "sunny_rain.png" : "rain.png";
            case 71:
            case 73:
            case 75:
            case 77: // Tuyết các loại
                return isDay ? "day_partly_cloudy.png" : "night_full_moon.png";
            case 80:
            case 81:
            case 82: // Mưa rào các loại
                return "rain.png";
            case 85:
            case 86: // Tuyết rào
                return isDay ? "day_partly_cloudy.png" : "night_full_moon.png";
            case 95: // Dông
                return "storm.png";
            case 96:
            case 99: // Dông kèm mưa storm
                return "storm.png";
        }
        return isDay ? "day_partly_cloudy.png" : "night_clear_moon.png";
    }

}