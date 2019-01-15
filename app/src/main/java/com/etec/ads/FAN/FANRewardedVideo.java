package com.etec.ads.FAN;

import com.etec.ads.AdsManager;
import com.etec.ads.AdsUnit;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
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

    @Override
    public void load(String strUnitName) {
        if (!this.checkToLoad()) {
            return;
        }

        this.mAd = new RewardedVideoAd(AdsManager.instance().getActivity(),this.mStrUnitID);
        this.mAd.setAdListener(this);
        this.mAd.setRewardData(new RewardData("", ""));
        this.mAd.loadAd();
        this.setLoading(true);
    }

    @Override
    public void show(String strUnitName) {
        if (this.isLoaded()) {
            this.mAd.show();
        }
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        System.out.println("FANRewardedVideo onError:"+adError.getErrorMessage());

        this.mAd.destroy();
        this.mAd = null;
        this.setLoaded(false);
        this.setLoading(false);
        this.onStatusUpdate("error");
    }

    @Override
    public void onAdLoaded(Ad ad) {
        System.out.println("FANRewardedVideo onAdLoaded");

        this.setLoaded(true);
        this.setLoading(false);
        this.onStatusUpdate("loaded");
    }

    @Override
    public void onAdClicked(Ad ad) {
    }

    @Override
    public void onRewardedVideoCompleted() {
        System.out.println("FANRewardedVideo onRewardedVideoCompleted");

        this.onStatusUpdate("rewarded");
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        System.out.println("FANRewardedVideo onLoggingImpression");

        this.onStatusUpdate("show");
    }

    @Override
    public void onRewardedVideoClosed() {
        System.out.println("FANRewardedVideo onRewardedVideoClosed");

        this.mAd.destroy();
        this.mAd = null;
        this.setLoaded(false);
        this.setLoading(false);
        this.onStatusUpdate("closed");
    }
}
