package com.cmzsoft.weather.APICall;

import com.cmzsoft.weather.Model.DataWeatherPerHourModel;
import com.cmzsoft.weather.Utils.WeatherUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
        String urlString = "https://api.weatherapi.com/v1/forecast.json?key=" + apiKey + "&q=" + lat + "," + lon + "&hours=3&aqi=yes&alerts=yes"; // Requesting forecast for 3 hours
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

                // Parse the response to get the forecast
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray forecastArray = jsonResponse.getJSONObject("forecast").getJSONArray("forecastday");
                JSONObject forecastData = forecastArray.getJSONObject(0);

                // Check the weather conditions for the next 2 hours
                JSONArray hourArray = forecastData.getJSONArray("hour");
                for (int i = 0; i < hourArray.length(); i++) {
                    JSONObject hourData = hourArray.getJSONObject(i);
                    if (hourData.getInt("time_epoch") <= (System.currentTimeMillis() / 1000) + 120 * 60) {
                        String condition = hourData.getJSONObject("condition").getString("text");
                        if (condition.toLowerCase().contains("rain")) {
                            return true;
                        }
                    }
                }
                return false;
            } else {
                System.out.println("Error calling API. Error code: " + responseCode);
            }
            conn.disconnect();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<DataWeatherPerHourModel> getWeatherPerHourInNextTwentyFour(double lat, double lon) {
        List<DataWeatherPerHourModel> weatherList = new ArrayList<>();
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

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject hourly = jsonResponse.getJSONObject("hourly");

                Calendar calendar = Calendar.getInstance();
                int curHour = calendar.get(Calendar.HOUR_OF_DAY);
                JSONArray timeArr = hourly.getJSONArray("time");
                JSONArray tempArr = hourly.getJSONArray("temperature_2m");
                JSONArray rainChangeArr = hourly.getJSONArray("precipitation_probability");
                JSONArray weatherCodeArr = hourly.getJSONArray("weathercode");
                JSONArray windSpeedArr = hourly.getJSONArray("wind_speed_10m");
                JSONArray windDir = hourly.getJSONArray("wind_direction_10m");
                for (int i = curHour; i < curHour + 24; i++) {


                    long timeEpoch = 0;
                    String time = timeArr.get(i).toString().substring(timeArr.get(i).toString().length() - 5);
                    int hour = Integer.parseInt(time.substring(0, 2)); //
                    boolean isDay = hour >= 6 && hour <= 18;
                    int tempC = tempArr.getInt(i);
                    int iconCode = weatherCodeArr.getInt(i);
                    String winDir = WeatherUtil.degreeToShortDirection(windDir.getInt(i));
                    float winSpeed = (float) windSpeedArr.getDouble(i);
                    int changeRain = rainChangeArr.getInt(i);
                    DataWeatherPerHourModel weather = new DataWeatherPerHourModel(timeEpoch, time, tempC, isDay, iconCode, winDir, winSpeed, changeRain);
                    weatherList.add(weather);
                }
            } else {
                System.out.println("Error calling API. Error code: " + responseCode);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return weatherList;
    }


    public JSONObject GetTempInAWeek(double lat, double lon) {
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon + "&hourly=precipitation_probability,temperature_2m,weathercode&timezone=auto\n";
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
