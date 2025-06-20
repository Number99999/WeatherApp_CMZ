package com.cmzsoft.weather.Utils;

import com.cmzsoft.weather.Model.FakeGlobal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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
                return "Mưa băng nhẹ";
            case 67:
                return "Mưa băng nặng";
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
        int iconCode;
        switch (code) {
            case 0:
                iconCode = isDay ? 1 : 9;
                break;
            case 1:
                iconCode = isDay ? 3 : 24;
                break;
            case 2:
                iconCode = isDay ? 3 : 24;
                break;
            case 3:
            case 45:
                iconCode = 7;
                break;
            case 48:
                iconCode = 12;
                break;
            case 51:
                iconCode = isDay ? 14 : 25;
                break;
            case 53:
                iconCode = isDay ? 5 : 27;
                break;
            case 55:
                iconCode = 6;
                break;
            case 56:
            case 57:
                iconCode = 32;
                break;
            case 61:
                iconCode = 5;
                break;
            case 63:
                iconCode = isDay ? 14 : 25;
                break;
            case 65:
                iconCode = isDay ? 5 : 27;
                break;
            case 66:
            case 67:
                iconCode = 32;
                break;
            case 71:
                iconCode = isDay ? 13 : 20;
                break;
            case 73:
                iconCode = 12;
                break;
            case 75:
            case 77:
                iconCode = 17;
                break;
            case 80:
            case 81:
                iconCode = 5;
                break;
            case 82:
                iconCode = 6;
                break;
            case 85:
                iconCode = isDay ? 13 : 20;
                break;
            case 86:
                iconCode = 12;
                break;
            case 95:
                iconCode = 18;
                break;
            case 96:
            case 99:
                iconCode = 32;
                break;
            case 9998:
                iconCode = 30;
                break;
            case 9999:
                iconCode = 31;
                break;
            default:
                iconCode = 1;
                break;
        }
        return "icon_weather_" + iconCode;
    }

    /*
        time: format: hh:mm
        timezone: format: GMT+h
     */
    public static String convertTimeDeviceToTimezone(String time, String timeZone) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sdf.setTimeZone(TimeZone.getDefault());
            Date date = sdf.parse(time);
            sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
            assert date != null;
            System.out.println("convertTimeDeviceToTimezone " + sdf.format(date));
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String CurrentTime() {
        Date now = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String hourMinute = formatter.format(now);

        String targetTimeZone = FakeGlobal.getInstance().curLocation.getTimeZone();
        return WeatherUtil.convertTimeDeviceToTimezone(hourMinute, targetTimeZone);
    }
}