package com.etec.ads.FAN;

import com.etec.ads.AdsManager;
import com.etec.ads.AdsUnit;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

public class FANInterstitial extends AdsUnit implements InterstitialAdListener {
    private InterstitialAd mAd;

    public FANInterstitial(final String strUnitID) {
        super(strUnitID);
    }

    public boolean isLoaded() {
        return this.mAd != null && this.mAd.isAdLoaded();
    }

    public void load() {
        if (this.mAd == null) {
            this.mAd = new InterstitialAd(AdsManager.instance().getActivity(),this.mStrUnitID);
            this.mAd.setAdListener(this);
            this.mAd.loadAd();
        }
    }

    public void show() {
        if (this.isLoaded()) {
            this.mAd.show();
        }
    }

    @Override
    public void onInterstitialDisplayed(Ad ad) {
        System.out.println("FANInterstitial onInterstitialDisplayed");

        this.onStatusUpdate("show");
    }

    @Override
    public void onInterstitialDismissed(Ad ad) {
        System.out.println("FANInterstitial onInterstitialDismissed");

        this.mAd = null;
        this.onStatusUpdate("closed");
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        System.out.println("FANInterstitial onError:"+adError.getErrorMessage());

        this.mAd = null;
        this.onStatusUpdate("error");
    }

    @Override
    public void onAdLoaded(Ad ad) {
        System.out.println("FANInterstitial onAdLoaded");

        this.onStatusUpdate("loaded");
    }

    @Override
    public void onAdClicked(Ad ad) {
        System.out.println("FANInterstitial onAdClicked");
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        System.out.println("FANInterstitial onLoggingImpression");
    }
}
