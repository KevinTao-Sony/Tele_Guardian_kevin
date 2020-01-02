package com.surrey.tele_guardian;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;

public class NameComparator implements Comparator<JSONObject>
{
    public int compare(JSONObject left, JSONObject right) {
        try {
            return left.get("NAME").toString().compareTo(right.get("NAME").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}