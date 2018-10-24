package com.etec.ads.FAN;

import com.etec.ads.AdsManager;
import com.etec.ads.AdsUnit;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.RewardData;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;

public class FANRewardedVideo extends AdsUnit implements RewardedVideoAdListener {
    private RewardedVideoAd mAd;

    public FANRewardedVideo(String strUnitID) {
        super(strUnitID);
    }

    public boolean isLoaded() {
        return this.mAd != null && this.mAd.isAdLoaded();
    }

    public void load() {
        if (this.mAd == null) {
            this.mAd = new RewardedVideoAd(AdsManager.instance().getActivity(),this.mStrUnitID);
            this.mAd.setAdListener(this);
            this.mAd.setRewardData(new RewardData("", ""));
            this.mAd.loadAd();
        }
    }

    public void show() {
        if (this.isLoaded()) {
            this.mAd.show();
        }
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        System.out.println("FANRewardedVideo onError:"+adError.getErrorMessage());

        this.mAd = null;
        this.onStatusUpdate("error");
    }

    @Override
    public void onAdLoaded(Ad ad) {
        System.out.println("FANRewardedVideo onAdLoaded");

        this.onStatusUpdate("loaded");
    }

    @Override
    public void onAdClicked(Ad ad) {
    }

    @Override
    public void onRewardedVideoCompleted() {
        System.out.println("FANRewardedVideo onRewardedVideoCompleted");

        this.onStatusUpdate("reward");
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        System.out.println("FANRewardedVideo onLoggingImpression");

        this.onStatusUpdate("show");
    }

    @Override
    public void onRewardedVideoClosed() {
        System.out.println("FANRewardedVideo onRewardedVideoClosed");

        this.mAd = null;
        this.onStatusUpdate("closed");
    }
}
