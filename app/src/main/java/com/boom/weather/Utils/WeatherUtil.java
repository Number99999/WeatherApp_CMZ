package com.boom.weather.Utils;

import com.boom.weather.Model.FakeGlobal;

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
                    return "Mon";
                case 2:
                    return "Tue";
                case 3:
                    return "Wed";
                case 4:
                    return "Thu";
                case 5:
                    return "Fri";
                case 6:
                    return "Sat";
                case 7:
                    return "Sun";
            }
        }
        return "";
    }

    public static String degreeToShortDirection(double degree) {
        String[] directions = {
                "N",    // North
                "NNE",  // North-Northeast
                "NE",   // Northeast
                "ENE",  // East-Northeast
                "E",    // East
                "ESE",  // East-Southeast
                "SE",   // Southeast
                "SSE",  // South-Southeast
                "S",    // South
                "SSW",  // South-Southwest
                "SW",   // Southwest
                "WSW",  // West-Southwest
                "W",    // West
                "WNW",  // West-Northwest
                "NW",   // Northwest
                "NNW"   // North-Northwest

        };
        int idx = (int) Math.round(degree / 22.5) % 16;
        if (idx < 0) idx += 16;
        return directions[idx];
    }

    public static String getWeatherStatus(int weatherCode) {
        switch (weatherCode) {
            case 0:
                return "Clear sky";
            case 1:
                return "Mainly clear";
            case 2:
                return "Partly cloudy";
            case 3:
                return "Overcast";
            case 45:
                return "Fog";
            case 48:
                return "Depositing rime fog";
            case 51:
                return "Light drizzle";
            case 53:
                return "Moderate drizzle";
            case 55:
                return "Dense drizzle";
            case 56:
                return "Light freezing drizzle";
            case 57:
                return "Dense freezing drizzle";
            case 61:
                return "Slight rain";
            case 63:
                return "Moderate rain";
            case 65:
                return "Heavy rain";
            case 66:
                return "Light freezing rain";
            case 67:
                return "Heavy freezing rain";
            case 71:
                return "Slight snow fall";
            case 73:
                return "Moderate snow fall";
            case 75:
                return "Heavy snow fall";
            case 77:
                return "Snow grains";
            case 80:
                return "Slight rain showers";
            case 81:
                return "Moderate rain showers";
            case 82:
                return "Violent rain showers";
            case 85:
                return "Slight snow showers";
            case 86:
                return "Heavy snow showers";
            case 95:
                return "Thunderstorm";
            case 96:
                return "Thunderstorm with slight hail";
            case 99:
                return "Thunderstorm with heavy hail";
            default:
                return "Unknown";
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

    public static int convertToCurTypeTemp(double in, String data) {
        if (data.equals("C")) return (int) in;
        return (int) (in * 9 / 5) + 32;
    }


    // 1 miles = 1.60934km
    public static double convertWindirToCurType(double in, String data) {
        double out = in;
        switch (data) {
            case "mph": {
                out = in / 1.60934;
                break;
            }
            case "km/h": {
                out = in;
                break;
            }
            case "m/s": {
                out = in / 3.6;
                break;
            }
        }
        out = Math.round(out * 100.0) / 100.0;
        return out;
    }

    public static String convertHourToCurType(String str, boolean is24h) {

        if (is24h) return str;
        String out = "";
        int h = Integer.parseInt(str.substring(0, 2));
        int m = Integer.parseInt(str.substring(3));
        if (h == 0) {
            out = "12:" + String.format("%02d", m) + " AM";
        } else if (h == 12) {
            out = "12:" + String.format("%02d", m) + " PM";
        } else if (h > 12) {
            out = (h - 12) + ":" + String.format("%02d", m) + " PM";
        } else {
            out = h + ":" + String.format("%02d", m) + " AM";
        }

        return out;
    }

    public static String convertDateToCurType(String str, String format) {
        if (str == null || str.isEmpty()) {
            return "";
        }

        String[] parts = str.split("-");
        if (parts.length != 3) {
            return "";
        }

        String year = parts[0];
        String month = parts[1];
        String day = parts[2];

        if (!year.matches("\\d{4}") || !month.matches("\\d{2}") || !day.matches("\\d{2}")) {
            return "";
        }

        try {
            int y = Integer.parseInt(year);
            int m = Integer.parseInt(month);
            int d = Integer.parseInt(day);

            if (m < 1 || m > 12 || d < 1 || d > 31) {
                return "";
            }

        } catch (NumberFormatException e) {
            return "";
        }

        String result = "";
        switch (format) {
            case "DD/MM/YYYY":
                result = day + "/" + month + "/" + year;
                break;
            case "MM/DD/YYYY":
                result = month + "/" + day + "/" + year;
                break;
            case "YYYY/MM/DD":
                result = year + "/" + month + "/" + day;
                break;
            default:
                result = str;
                break;
        }

        return result;
    }
}