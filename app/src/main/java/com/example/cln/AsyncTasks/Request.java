package com.example.cln.AsyncTasks;

import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.Callable;

public class Request implements Callable<String> {
    private String response;
    private final StringBuilder requestParameters = new StringBuilder();
    private final String requestUrl = "localhost";

    public Request(Map<String, String> parameters) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            parameters.forEach((key, value) -> {
                if (!requestParameters.toString().equals("")) {
                    requestParameters.append("&");
                }

                try {
                    requestParameters.append(URLEncoder.encode(key, "UTF-8")).append("=")
                        .append(URLEncoder.encode(value, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

//    public void addParam(String name, String value) {
//        Map<String, String> map = Map.of("", "", "", "", ", ", "");
//        try {
//            if (!parameters.equals("")) {
//                parameters += "&";
//            }
//            parameters = URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public String call() {
        System.setProperty("http.keepAlive", "false");
        PrintWriter writer = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(requestUrl);

            HttpURLConnection connexion = (HttpURLConnection) url.openConnection();
            connexion.setDoOutput(true);

            connexion.setRequestMethod("POST");
            connexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connexion.setFixedLengthStreamingMode(requestParameters.toString().getBytes().length);

            writer = new PrintWriter(connexion.getOutputStream());
            writer.print(requestParameters);
            writer.flush();

            reader = new BufferedReader(new InputStreamReader(connexion.getInputStream()));
            response = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert writer != null;
                writer.close();
            } catch (NullPointerException ignored) {}

            try {
                assert reader != null;
                reader.close();
            } catch (NullPointerException | IOException ignored) {}
        }

        return response;

    }
}
