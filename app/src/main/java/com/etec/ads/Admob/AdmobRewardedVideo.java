package com.etec.ads.Admob;

import com.etec.ads.AdsManager;
import com.etec.ads.AdsUnit;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class AdmobRewardedVideo extends AdsUnit implements RewardedVideoAdListener {
    private RewardedVideoAd mAd;

    private String mStrUnitID;

    public AdmobRewardedVideo(String strUnitID) {
        super(strUnitID);
    }

    public boolean isLoaded() {
        return this.mAd != null && this.mAd.isLoaded();
    }

    public void load() {
        if (this.mAd == null) {
            this.mAd = MobileAds.getRewardedVideoAdInstance(AdsManager.instance().getActivity());
            this.mAd.setRewardedVideoAdListener(this);
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        this.mAd.loadAd(this.mStrUnitID,adRequest);
    }

    public void show() {
        if (this.isLoaded()) {
            this.mAd.show();
        }
    }

    public void onRewardedVideoAdLoaded() {
        System.out.println("AdmobRewardedVideo onRewardedVideoAdLoaded");

        this.onStatusUpdate("loaded");
    }

    public void onRewardedVideoAdOpened() {
        System.out.println("AdmobRewardedVideo onRewardedVideoAdOpened");

        this.onStatusUpdate("show");
    }

    public void onRewardedVideoStarted() {
        System.out.println("AdmobRewardedVideo onRewardedVideoStarted");
    }

    public void onRewardedVideoAdClosed() {
        System.out.println("AdmobRewardedVideo onRewardedVideoAdClosed");

        this.onStatusUpdate("closed");
    }

    public void onRewarded(RewardItem var1) {
        System.out.println("AdmobRewardedVideo onRewarded");

        this.onStatusUpdate("reward");
    }

    public void onRewardedVideoCompleted() {
        System.out.println("AdmobRewardedVideo onRewardedVideoCompleted");
    }

    public void onRewardedVideoAdLeftApplication() {
        System.out.println("AdmobRewardedVideo onRewardedVideoAdLeftApplication");
    }

    public void onRewardedVideoAdFailedToLoad(int var1) {
        System.out.println("AdmobRewardedVideo onRewardedVideoAdFailedToLoad");

        this.onStatusUpdate("error");
    }
}
