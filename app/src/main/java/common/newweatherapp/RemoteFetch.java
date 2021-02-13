package common.newweatherapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import org.json.JSONObject;
 
import android.content.Context;

public class RemoteFetch {


    public static JSONObject getJSON(Context context, String city) {

        String API_KEY = "64c6e8797c969ce17da446c485481de7";
        String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&APPID=" + API_KEY + "&units=metric" + "&lang=sp, es";


        try {
            URL url = new URL(urlString);
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            if (data.getInt("cod") != 200) {
                return null;
            }

            return data;
        } catch (Exception e) {
            return null;
        }
    }
}