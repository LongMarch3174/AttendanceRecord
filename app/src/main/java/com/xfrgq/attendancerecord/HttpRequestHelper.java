package com.xfrgq.attendancerecord;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequestHelper {

    private static final String BASE_URL = "http://192.168.0.101:8080/AttendanceServer_war_exploded";

    public static HttpURLConnection createPostRequest(String endpoint, JSONObject jsonData) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        if (jsonData != null) {
            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonData.toString().getBytes());
                os.flush();
            }
        }

        return connection;
    }

    public static String getResponse(HttpURLConnection connection) throws Exception {
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return new String(connection.getInputStream().readAllBytes());
        } else {
            throw new Exception("HTTP error: " + connection.getResponseCode());
        }
    }
}
