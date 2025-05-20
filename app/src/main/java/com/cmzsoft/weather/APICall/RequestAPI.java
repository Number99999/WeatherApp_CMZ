package com.cmzsoft.weather.APICall;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestAPI {
    private String apiKey = "11735badaf909f1af42295fae212a917";
    private static RequestAPI instance;
    public static RequestAPI getInstance(){
        if (instance == null) {
            instance = new RequestAPI();
        }
        return instance;
    }

    public String CallAPI(){
        String city = "Hanoi";
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + city +
                "&appid=" + apiKey + "&units=metric";

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

                System.out.println("Phản hồi từ API:");
                System.out.println(response.toString());

                return "Phản hồi từ API:" + response.toString();
            } else {
                System.out.println("Lỗi khi gọi API. Mã lỗi: " + responseCode);
            }

            conn.disconnect();
            return "called api" ;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error when call api" + e.toString();
        }
    }



}
