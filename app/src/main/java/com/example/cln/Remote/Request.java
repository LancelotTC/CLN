package com.example.cln.Remote;

import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Object that makes a request
 */
public class Request implements Callable<String> {
    private final String METHOD;
    private String response = "";
    private final StringBuilder requestParameters = new StringBuilder();

    /**
     * Constructor that mainly formats the parameters.
     * @param parameters URL parameters
     * @param METHOD Request method
     */
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

    /**
     * Method that launches the requests and returns the response
     * @return The response of the request
     */
    @Override
    public String call() {
        System.setProperty("http.keepAlive", "false");
        BufferedReader reader = null;

        try {
            //    private final String requestUrl = "http://10.0.2.2/cln/clnserver.php";
            String requestUrl = "http://20240228t184156-dot-cafelumierenoire.ue.r.appspot.com/home/lancelot_tariot_camille/CLNREST/index.php";
            Log.d("Requested URL", requestUrl + "?"+ requestParameters);

            URL url = new URL(requestUrl + "?"+ requestParameters);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(METHOD);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // get last line of response
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                response = currentLine;
            }
        } catch (Exception e) {
            Log.d("Error in Request", e.toString());
        } finally {
            try {
                assert reader != null;
                reader.close();
            } catch (AssertionError | NullPointerException | IOException ignored) {}
        }

        response = response.replace("</br>", "");
        Log.d("response", response);
        return response;
    }
}
