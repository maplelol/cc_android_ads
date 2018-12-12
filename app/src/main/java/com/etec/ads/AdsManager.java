package com.etec.ads;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.etec.ads.Admob.AdmobInterstitial;
import com.etec.ads.Admob.AdmobNative;
import com.etec.ads.Admob.AdmobRewardedVideo;
import com.etec.ads.FAN.FANInterstitial;
import com.etec.ads.FAN.FANNative;
import com.etec.ads.FAN.FANRewardedVideo;
import com.etec.ads.MoPub.MoPubInterstitial;
import com.etec.ads.MoPub.MoPubNative;
import com.etec.ads.MoPub.MoPubRewardedVideo;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdsManager {
    protected static final String LOG_TAG = "com.etec.ads.AdsManager";
    protected static AdsManager mInstance;

    protected Activity mActivity;
    protected Map<String,AdsUnit> mUnitMap;
    protected int mLogoResID = -1;

    protected AdsStatusUpdateListener mStatusListener;

    protected boolean mIsInitialized = false;

    protected AdsManager() {
        mUnitMap = new HashMap<>();
    }

    public static AdsManager instance() {
        if (mInstance == null) {
            mInstance = new AdsManager();
        }
        return mInstance;
    }

    public void initialize(Activity act, AdsStatusUpdateListener l, int iSplashLogoID) {
        this.setActivity(act);
        this.setStatusUpdateListener(l);
        this.setSplashLogoID(iSplashLogoID);

        //AdSettings.addTestDevice("02d126ff-8c0f-4baa-b2b9-1012d0f03645");
        //AudienceNetworkAds.initialize(act);
    }

    public void setActivity(Activity act) {
        this.mActivity = act;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public void setSplashLogoID(int iResID) {
        mLogoResID = iResID;
    }

    public int getSplashLogoID() {
        return mLogoResID;
    }

    public void init(String strJson) {
        try {
            mIsInitialized = true;
            JSONObject jsonData = new JSONObject(strJson);
            if (jsonData.has("admob_app_id")) {
                String admobAppID = jsonData.getString("admob_app_id");
                MobileAds.initialize(this.getActivity(), admobAppID);
            }

            if (jsonData.has("mopub_unit_id")) {
                final AdsManager mThis = this;
                mIsInitialized = false; //MoPub的初始化是个异步的过程，所以需要等待回调
                String mopubUnitID = jsonData.getString("mopub_unit_id");
                SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(mopubUnitID)
                        .withNetworksToInit(new ArrayList<String>())
                        .build();
                MoPub.initializeSdk(this.getActivity(),sdkConfiguration,new SdkInitializationListener() {
                    @Override
                    public void onInitializationFinished() {
                        mIsInitialized = true;
                        //checkInitialized();
                        Log.d(LOG_TAG, "MoPubAds onInitializationFinished");
                    }
                });
            }

            //this.checkInitialized();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void createAdsUnit(String strUnitID, String szClassName) {
        AdsUnit ads = this.getAds(strUnitID);
        if (ads == null) {
            if (szClassName.equals("FANInterstitial")) {
                ads = new FANInterstitial(strUnitID);
            } else if (szClassName.equals("FANRewardedVideo")) {
                ads = new FANRewardedVideo(strUnitID);
            } else if (szClassName.equals("FANNativeSplash") || szClassName.equals("FANNativeExit")) {
                ads = new FANNative(strUnitID);
            } else if (szClassName.equals("AdmobInterstitial")) {
                ads = new AdmobInterstitial(strUnitID);
            } else if (szClassName.equals("AdmobRewardedVideo")) {
                ads = new AdmobRewardedVideo(strUnitID);
            } else if (szClassName.equals("AdmobNativeSplash") || szClassName.equals("AdmobNativeExit")) {
                ads = new AdmobNative(strUnitID);
            } else if (szClassName.equals("MoPubInterstitial")) {
                ads = new MoPubInterstitial(strUnitID);
            } else if (szClassName.equals("MoPubRewardedVideo")) {
                ads = new MoPubRewardedVideo(strUnitID);
            } else if (szClassName.equals("MoPubNativeSplash") || szClassName.equals("MoPubNativeExit")) {
                ads = new MoPubNative(strUnitID);
            }

            if (ads != null) {
                this.putAds(ads);
            } else {
                Log.d(LOG_TAG,"error: can not find implementation for ads "+szClassName);
            }
        }
    }

    public boolean isInitialized() {
        return mIsInitialized;
    }

    public void loadAds(String strUnitID,String strUnitName) {
        AdsUnit ads = mUnitMap.get(strUnitID);
        if (ads != null) {
            ads.load(strUnitName);
        }
    }

    public boolean isAdsLoaded(String strUnitID) {
        AdsUnit ads = mUnitMap.get(strUnitID);
        if (ads != null) {
            return ads.isLoaded();
        }
        return false;
    }

    public void showAds(String strUnitID,String strUnitName) {
        AdsUnit ads = mUnitMap.get(strUnitID);
        if (ads != null) {
            Log.d(LOG_TAG, "showAds strUnitName="+strUnitName);
            ads.show(strUnitName);
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

    public void checkInitialized() {
        if (this.mIsInitialized) {
            JSONObject resultJson = new JSONObject();
            try {
                resultJson.put("status","init");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.onStatusUpdate(resultJson.toString());
        }
    }

    public static void export_init(final String strJson) {
        Log.d(LOG_TAG, "export_init "+strJson);
        AdsManager.instance().getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AdsManager.instance().init(strJson);
            }
        });
    }

    public static void export_createAdsUnit(String strUnitID, String szClassName) {
        Log.d(LOG_TAG, "export_createAdsUnit "+strUnitID+" szClassName "+szClassName);
        AdsManager.instance().createAdsUnit(strUnitID,szClassName);
    }

    public static void export_loadAdsUnit(final String strUnitID, final String strUnitName) {
        Log.d(LOG_TAG, "export_loadAdsUnit strUnitID="+strUnitID+" strUnitName="+strUnitName);
        AdsManager.instance().getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AdsManager.instance().loadAds(strUnitID,strUnitName);
            }
        });
    }

    public static void export_showAdsUnit(final String strUnitID, final String strUnitName) {
        Log.d(LOG_TAG, "export_showAdsUnit strUnitID="+strUnitID+" strUnitName="+strUnitName);
        AdsManager.instance().getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AdsManager.instance().showAds(strUnitID,strUnitName);
            }
        });
    }

    public static void export_closeAdsUnit(String strUnitID) {
        Log.d(LOG_TAG, "export_closeAdsUnit "+strUnitID);
    }

    public static boolean export_isInitialized() {
        return AdsManager.instance().isInitialized();
    }

    public static void setViewClickable(View v, boolean e) {
        v.setClickable(e);
        v.setOnTouchListener(null);
        v.setOnClickListener(null);
        if (v instanceof ViewGroup) {
            ViewGroup vg = ((ViewGroup) v);
            for (int i = 0; i < vg.getChildCount(); i++) {
                setViewClickable(vg.getChildAt(i),e);
            }
        }
    }

    public void onCreate(Activity act) {
        MoPub.onCreate(act);
    }
    public void onPause(Activity act) {
        MoPub.onPause(act);
    }
    public void onResume(Activity act) {
        MoPub.onResume(act);
    }
    public void onStart(Activity act) {
        MoPub.onStart(act);
    }
    public void onRestart(Activity act) {
        MoPub.onRestart(act);
    }
    public void onStop(Activity act) {
        MoPub.onStop(act);
    }
    public void onDestroy(Activity act) {
        MoPub.onDestroy(act);
    }
    public void onBackPressed(Activity act) {
        MoPub.onBackPressed(act);
    }
}
