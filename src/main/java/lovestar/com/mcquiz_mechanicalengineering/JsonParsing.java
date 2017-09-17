package lovestar.com.mcquiz_mechanicalengineering;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Asif ullah on 4/24/2016.
 */
public class JsonParsing {
    String jsonString;
    ArrayList<String> arrayList;
    HashMap<String, String> myhashmap;

    public JsonParsing(String string) {
        this.jsonString = string;
        arrayList = new ArrayList<>();
        myhashmap = new HashMap<>();
    }

    public ArrayList<HashMap<String, String>> ParsejsonArray(String JsonArrayString) {
        ArrayList<HashMap<String, String>> arrayListToberetuend = new ArrayList<>();
        JSONArray jsonArray;
        HashMap<String, String> temHashmap;
        try {

            jsonArray = new JSONArray(JsonArrayString);
            for (int i = 0; i < jsonArray.length(); i++) {
                temHashmap = new HashMap<>();
                JSONObject tempjobj = jsonArray.getJSONObject(i);
                for (int j = 0; j < tempjobj.names().length(); j++) {
                    String key = tempjobj.names().get(j) + "";
                    String value = tempjobj.getString(key);
                    temHashmap.put(key, value);
                }
                arrayListToberetuend.add(temHashmap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayListToberetuend;
    }
}