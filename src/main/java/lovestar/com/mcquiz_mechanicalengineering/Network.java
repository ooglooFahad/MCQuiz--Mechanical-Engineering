package lovestar.com.mcquiz_mechanicalengineering;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by Asif ullah on 4/24/2016.
 */
public class Network {
    URL url;
    String prams;
    HttpURLConnection connection;
    InputStream inputStream;

    Network(String webaddress, HashMap<String, String> params) {
        try {
            this.url = new URL("http://mcquiz.thewebsupportdesk.com/" + webaddress + "?" + EncodeUrlforGet(params));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String ToRecieveDataFromWeb() throws IOException {
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        PrintWriter out = new PrintWriter(connection.getOutputStream());
        out.println(prams);
        out.close();
        inputStream = connection.getInputStream();
        String temp = getStringFromInputStream(inputStream);
        return temp;
    }

    private String EncodeUrlforGet(HashMap<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : params.keySet()) {
            String value = null;
            try {
                value = URLEncoder.encode(params.get(key), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }
            stringBuilder.append(key + "=" + value);
        }
        return stringBuilder.toString();
    }

    private String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(is);
            br = new BufferedReader(inputStreamReader);
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}