package com.etec.ads;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

public class AdsUnit {
    protected String mStrUnitID;

    public AdsUnit(String strUnitID) {
        this.mStrUnitID = strUnitID;
    }

    public String getUnitID() {
        return this.mStrUnitID;
    }

    public void load() {

    }

    public boolean isLoaded() {
        return false;
    }

    public void setLoaded(boolean isLoaded) {

    }

    public void show() {

    }

    public void onStatusUpdate(String strStatus) {
        JSONObject resultJson = new JSONObject();
        try {
            resultJson.put("status",strStatus);
            resultJson.put("unitid",this.mStrUnitID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AdsManager.instance().onStatusUpdate(resultJson.toString());
    }
}
