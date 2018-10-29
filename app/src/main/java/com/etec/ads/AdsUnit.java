package com.etec.ads;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

public class AdsUnit {
    protected String mStrUnitID;
    protected boolean mIsLoaded = false;
    protected boolean mIsLoading = false;

    public AdsUnit(String strUnitID) {
        this.mStrUnitID = strUnitID;
    }

    public String getUnitID() {
        return this.mStrUnitID;
    }

    public void load(String strUnitName) {

    }

    public boolean isLoaded() {
        return mIsLoaded;
    }

    public void setLoaded(boolean is) {
        mIsLoaded = is;
    }
    public void setLoading(boolean is) {
        mIsLoading = is;
    }

    public void show(String strUnitName) {

    }

    public boolean checkToLoad() {
        return !mIsLoading && !mIsLoaded;
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
