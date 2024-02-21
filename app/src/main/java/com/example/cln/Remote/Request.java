package com.example.cln.Remote;

import android.os.Build;

import com.example.cln.Utils.Shortcuts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.Callable;

public class Request implements Callable<String> {
    private final String METHOD;
    private String response = "";
    private final StringBuilder requestParameters = new StringBuilder();
    private final String requestUrl = "http://192.168.217.71/cln/clnserver.php";

    public Request(Map<String, String> parameters, String METHOD) {
        super();
        this.METHOD = METHOD;

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
//        PrintWriter writer = null;
        BufferedReader reader = null;

        try {
            Shortcuts.log("Requested URL", requestUrl + "?"+ requestParameters);
            URL url = new URL(requestUrl + "?"+ requestParameters);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoOutput(true);

            connection.setRequestMethod(METHOD);
//            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            connection.setFixedLengthStreamingMode(requestParameters.toString().getBytes().length);
//            connection.setUseCaches(false);

//            writer = new PrintWriter(connection.getOutputStream());
//            writer.print(requestParameters);
//            writer.flush();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // get last line of response
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                response = currentLine;
            }
        } catch (Exception e) {
            Shortcuts.log("Error in Request", e.toString());
            e.printStackTrace();
        } finally {
//            try {
//                assert writer != null;
//                writer.close();
//            } catch (NullPointerException ignored) {}

            try {
                assert reader != null;
                reader.close();
            } catch (AssertionError | NullPointerException | IOException ignored) {}
        }

        response = response.replace("</br>", "");
        return response;
    }
}
