package com.etec.ads;

import android.app.Activity;
import android.util.Log;

import com.etec.ads.Admob.AdmobInterstitial;
import com.etec.ads.Admob.AdmobRewardedVideo;
import com.etec.ads.FAN.FANInterstitial;
import com.etec.ads.FAN.FANRewardedVideo;
import com.etec.ads.MoPub.MoPubInterstitial;
import com.etec.ads.MoPub.MoPubRewardedVideo;
import com.google.android.gms.ads.MobileAds;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AdsManager {
    protected static final String LOG_TAG = "com.etec.ads.AdsManager";
    protected static AdsManager mInstance;

    protected Activity mActivity;
    protected Map<String,AdsUnit> mUnitMap;

    protected AdsStatusUpdateListener mStatusListener;

    protected AdsManager() {
        mUnitMap = new HashMap<String,AdsUnit>();
    }

    public static AdsManager instance() {
        if (mInstance == null) {
            mInstance = new AdsManager();
        }
        return mInstance;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public void init(String strJson) {
        try {
            JSONObject jsonData = new JSONObject(strJson);
            String admobAppID = jsonData.getString("admob_app_id");
            if (!admobAppID.isEmpty()) {
                MobileAds.initialize(this.getActivity(), admobAppID);
            }
            String mopubUnitID = jsonData.getString("mopub_unit_id");
            if (!mopubUnitID.isEmpty()) {
                SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(mopubUnitID).build();
                MoPub.initializeSdk(this.getActivity(),sdkConfiguration,new SdkInitializationListener() {
                    @Override
                    public void onInitializationFinished() {
                        Log.d(LOG_TAG, "MoPubAds onInitializationFinished");
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void createAdsUnit(String strUnitID, String szClassName) {
        AdsUnit ads = this.getAds(strUnitID);
        if (ads == null) {
            if (szClassName == "FANInterstitial") {
                ads = new FANInterstitial(strUnitID);
            } else if (szClassName == "FANRewardedVideo") {
                ads = new FANRewardedVideo(strUnitID);
            } else if (szClassName == "AdmobInterstitial") {
                ads = new AdmobInterstitial(strUnitID);
            } else if (szClassName == "AdmobRewardedVideo") {
                ads = new AdmobRewardedVideo(strUnitID);
            } else if (szClassName == "MoPubInterstitial") {
                ads = new MoPubInterstitial(strUnitID);
            } else if (szClassName == "MoPubRewardedVideo") {
                ads = new MoPubRewardedVideo(strUnitID);
            }

            if (ads != null) {
                this.putAds(ads);
            } else {
                Log.d(LOG_TAG,"error: can not find implementation for ads "+szClassName);
            }
        }
    }

    public void loadAds(String strUnitID) {
        AdsUnit ads = mUnitMap.get(strUnitID);
        if (ads != null) {
            ads.load();
        }
    }

    public boolean isAdsLoaded(String strUnitID) {
        AdsUnit ads = mUnitMap.get(strUnitID);
        if (ads != null) {
            return ads.isLoaded();
        }
        return false;
    }

    public void showAds(String strUnitID) {
        AdsUnit ads = mUnitMap.get(strUnitID);
        if (ads != null) {
            ads.show();
        }
    }

    public AdsUnit getAds(String strUnitID) {
        return mUnitMap.get(strUnitID);
    }

    public void putAds(AdsUnit ads) {
        mUnitMap.put(ads.getUnitID(),ads);
    }

    public void setStatusUpdateListener(AdsStatusUpdateListener l) {
        mStatusListener = l;
    }

    public void onStatusUpdate(String strData) {
        Log.d(LOG_TAG, "onStatusUpdate "+strData);
        if (mStatusListener != null) {
            mStatusListener.call(strData);
        }
    }

    public static void export_init(String strJson) {
        Log.d(LOG_TAG, "export_init "+strJson);
        AdsManager.instance().init(strJson);
    }

    public static void export_createAdsUnit(String strUnitID, String szClassName) {
        Log.d(LOG_TAG, "export_createAdsUnit "+strUnitID+" szClassName "+szClassName);
        AdsManager.instance().createAdsUnit(strUnitID,szClassName);
    }

    public static void export_loadAdsUnit(String strUnitID) {
        Log.d(LOG_TAG, "export_loadAdsUnit "+strUnitID);
        AdsManager.instance().loadAds(strUnitID);
    }

    public static void export_showAdsUnit(String strUnitID) {
        Log.d(LOG_TAG, "export_showAdsUnit "+strUnitID);
        AdsManager.instance().showAds(strUnitID);
    }

    public static void export_closeAdsUnit(String strUnitID) {
        Log.d(LOG_TAG, "export_closeAdsUnit "+strUnitID);
    }
}
