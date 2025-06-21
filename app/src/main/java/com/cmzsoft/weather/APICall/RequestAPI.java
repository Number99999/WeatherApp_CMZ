package com.cmzsoft.weather.APICall;

import com.cmzsoft.weather.Model.DataWeatherPerHourModel;
import com.cmzsoft.weather.Model.FakeGlobal;
import com.cmzsoft.weather.Utils.WeatherUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RequestAPI {
    private String domainApi = "https://api.weatherapi.com/v1";
    private String apiKey = "cde7ae0b623f4dd69d645128252305";
    private static RequestAPI instance;

    public static RequestAPI getInstance() {
        if (instance == null) {
            instance = new RequestAPI();
        }
        return instance;
    }

    public JSONObject GetCurrentWeather(double lat, double lon) {
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon + "&current_weather=true&hourly=temperature_2m,precipitation,relative_humidity_2m,wind_direction_10m,uv_index,apparent_temperature&timezone=auto";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                JSONObject obj = new JSONObject(response.toString());
                FakeGlobal.getInstance().curLocation.setTimeZone(obj.getString("timezone_abbreviation"));
                return obj;
            } else {
                System.out.println("Lỗi khi gọi API. Mã lỗi: " + responseCode);
            }
            conn.disconnect();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject GetAllDataInCurrentDay(double lat, double lon) {
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon + "&hourly=temperature_2m,precipitation_probability,weathercode,wind_speed_10m,wind_direction_10m&timezone=auto";

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                return new JSONObject(response.toString());
            } else {
                System.out.println("Lỗi khi gọi API. Mã lỗi: " + responseCode);
            }
            conn.disconnect();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean GetWeatherForNext120Minutes(double lat, double lon) {
        String urlString = "https://api.meteo.fr/api/forecast?key=" + apiKey + "&q=" + lat + "," + lon + "&hours=3";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray forecastArray = jsonResponse.getJSONArray("forecast");
                JSONObject forecastData = forecastArray.getJSONObject(0);
                JSONArray hourArray = forecastData.getJSONArray("hourly");
                long currentTime = System.currentTimeMillis() / 1000;
                for (int i = 0; i < hourArray.length(); i++) {
                    JSONObject hourData = hourArray.getJSONObject(i);
                    long hourTime = hourData.getLong("timestamp");

                    if (hourTime <= currentTime + 3 * 60 * 60) {  // 3 giờ sau
                        String condition = hourData.getJSONObject("condition").getString("text");
                        if (condition.toLowerCase().contains("rain")) {
                            return true;  // Nếu có mưa, trả về true
                        }
                    }
                }
            } else {
                System.out.println("Error calling API. Error code: " + responseCode);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<DataWeatherPerHourModel> getWeatherPerHourInNextTwentyFour(double lat, double lon) {
        List<DataWeatherPerHourModel> weatherList = new ArrayList<>();
        String urlString = "https://api.open-meteo.com/v1/forecast" + "?latitude=" + lat + "&longitude=" + lon + "&hourly=temperature_2m,precipitation_probability,weathercode,wind_speed_10m,wind_direction_10m" + "&daily=sunrise,sunset" + "&timezone=auto";

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            Date now = new Date();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String hourMinute = formatter.format(now);

            String targetTimeZone = FakeGlobal.getInstance().curLocation.getTimeZone();

            String timeConverted = WeatherUtil.convertTimeDeviceToTimezone(hourMinute, targetTimeZone);
            int curHour = Integer.parseInt(timeConverted.substring(11, 13));
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject daily = jsonResponse.getJSONObject("daily");
                JSONArray sunriseArr = daily.getJSONArray("sunrise");
                JSONArray sunsetArr = daily.getJSONArray("sunset");
                String sunrise = Integer.parseInt(sunriseArr.getString(0).substring(11, 13)) <= curHour ? sunriseArr.getString(0).substring(11) : sunriseArr.getString(1).substring(11);
                String sunset = Integer.parseInt(sunsetArr.getString(0).substring(11, 13)) >= curHour ? sunsetArr.getString(0).substring(11) : sunsetArr.getString(1).substring(11);
                JSONObject hourly = jsonResponse.getJSONObject("hourly");
                JSONArray timeArr = hourly.getJSONArray("time");
                JSONArray tempArr = hourly.getJSONArray("temperature_2m");
                JSONArray rainChangeArr = hourly.getJSONArray("precipitation_probability");
                JSONArray weatherCodeArr = hourly.getJSONArray("weathercode");
                JSONArray windSpeedArr = hourly.getJSONArray("wind_speed_10m");
                JSONArray windDir = hourly.getJSONArray("wind_direction_10m");
                for (int i = curHour; i < curHour + 24; i++) {
                    String fullTime = timeArr.getString(i); // "2025-06-18T18:00"
                    String hourMinute2 = fullTime.split("T")[1]; // "18:00"
//                    LocalTime currentHour = LocalTime.parse(hourMinute2);

                    boolean isDay = (Integer.parseInt(hourMinute2.substring(0, 2)) >= 6) && (Integer.parseInt(hourMinute2.substring(0, 2)) <= 19);
                    int tempC = tempArr.getInt(i);
                    int iconCode = weatherCodeArr.getInt(i);
                    String winDirStr = WeatherUtil.degreeToShortDirection(windDir.getInt(i));
                    float winSpeed = (float) windSpeedArr.getDouble(i);
                    int changeRain = rainChangeArr.getInt(i);

                    DataWeatherPerHourModel weather = new DataWeatherPerHourModel(0L, hourMinute2, tempC, isDay, iconCode, winDirStr, winSpeed, changeRain);
                    weatherList.add(weather);
                    if (sunrise.substring(0, 2).equals(hourMinute2.substring(0, 2))) {
                        weatherList.add(new DataWeatherPerHourModel(0L, sunrise, tempC, isDay, 9998, winDirStr, winSpeed, changeRain));
                    } else if (sunset.substring(0, 2).equals(hourMinute2.substring(0, 2))) {
                        weatherList.add(new DataWeatherPerHourModel(0L, sunset, tempC, isDay, 9999, winDirStr, winSpeed, changeRain));
                    }
                }
            } else {
                System.out.println("❌ Error calling API. Response code: " + responseCode);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return weatherList;
    }

    public JSONObject GetPOPNextWeek(double lat, double lon) {
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon + "&hourly=precipitation_probability&timezone=auto";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                JSONObject result = new JSONObject(response.toString());
                return result;
            } else {
                System.out.println("Lỗi khi gọi API. Mã lỗi: " + responseCode);
            }
            conn.disconnect();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject GetTempInAWeek(double lat, double lon) {
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon + "&hourly=precipitation_probability,temperature_2m,weathercode&timezone=auto";
        System.out.println("STRINGGGGGGGGGGGGGG " + urlString);
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JSONObject result = new JSONObject(response.toString());
                return result;
            } else {
                System.out.println("Lỗi khi gọi API. Mã lỗi: " + responseCode);
            }
            conn.disconnect();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
