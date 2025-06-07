package com.cmzsoft.weather.APICall;

import com.cmzsoft.weather.Model.DataWeatherPerHourModel;

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

    public JSONObject CallAPI(String location) {
        String urlString = "https://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + location;
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
        String urlString = "https://api.weatherapi.com/v1/forecast.json?key=" + apiKey + "&q=" + lat + "," + lon + "&days=2&aqi=yes&alerts=yes";
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

    public boolean GetWeatherForNext120Minutes(String location) {
        String urlString = "https://api.weatherapi.com/v1/forecast.json?key=" + apiKey + "&q=" + location + "&hours=3&aqi=yes&alerts=yes"; // Requesting forecast for 3 hours
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

    public List<DataWeatherPerHourModel> getWeatherPerHourInNextTwentyFour(String location) {
        String urlString = "https://api.weatherapi.com/v1/forecast.json?key=" + apiKey + "&q=" + location + "&days=2&aqi=yes&alerts=yes";
        List<DataWeatherPerHourModel> weatherList = new ArrayList<>();

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
                JSONArray forecastArray = jsonResponse.getJSONObject("forecast").getJSONArray("forecastday");
                JSONObject forecastData = forecastArray.getJSONObject(0);
                JSONObject forecastData2 = forecastArray.getJSONObject(1);
                JSONArray hourArray = forecastData.getJSONArray("hour");
                JSONArray hourArray2 = forecastData2.getJSONArray("hour");

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);


                for (int i = 0; i < hourArray2.length(); i++) {
                    hourArray.put(hourArray2.getJSONObject(i));
                }
                for (int i = 0; i < hourArray.length(); i++) {
                    if (i >= hour && i < (hour + 24)) {
                        JSONObject hourData = hourArray.getJSONObject(i);
                        long timeEpoch = hourData.getLong("time_epoch");
                        String time = hourData.getString("time");
                        int tempC = hourData.getInt("temp_c");
                        boolean isDay = hourData.getInt("is_day") == 1;
                        int iconCode = hourData.getJSONObject("condition").getInt("code");
                        String winDir = hourData.getString("wind_dir");
                        float winSpeed = (float) hourData.getDouble("wind_kph");
                        int changeRain = hourData.getInt("will_it_rain") == 1 ? 100 : 0;
                        DataWeatherPerHourModel weather = new DataWeatherPerHourModel(timeEpoch, time, tempC, isDay, iconCode, winDir, winSpeed, changeRain);
                        weatherList.add(weather);
                    }
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


    public JSONObject GetTempInAWeek(String s) {
        String urlString = "https://api.weatherapi.com/v1/forecast.json?key=" + apiKey + "&q=" + s + "&days=7&aqi=false&alerts=false";
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
