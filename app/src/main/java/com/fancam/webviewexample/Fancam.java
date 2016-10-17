package com.fancam.webviewexample;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by nealshail on 14/10/2016.
 */

public class Fancam {

    public String title;
    public String url;

    public static ArrayList<Fancam> getFancamsFromFile(String filename, Context context){
        final ArrayList<Fancam> fancamList = new ArrayList<>();

        try {
            // Load data
            String jsonString = loadJsonFromAsset(filename, context);
            JSONArray fancams = new JSONArray(jsonString);

            // Get Fancam objects from data
            for(int i = 0; i < fancams.length(); i++){
                Fancam fancam = new Fancam();

                fancam.title = fancams.getJSONObject(i).getString("title");
                fancam.url = fancams.getJSONObject(i).getString("url");
                fancamList.add(fancam);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return fancamList;
    }

    private static String loadJsonFromAsset(String filename, Context context) {
        String json;

        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        }
        catch (java.io.IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }
}
