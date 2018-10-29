package com.etec.ads.MoPub;

import com.etec.ads.AdsManager;
import com.etec.ads.AdsUnit;
import com.mopub.mobileads.MoPubErrorCode;

public class MoPubInterstitial extends AdsUnit implements com.mopub.mobileads.MoPubInterstitial.InterstitialAdListener {
    private com.mopub.mobileads.MoPubInterstitial mAd;

    public MoPubInterstitial(String strUnitID) {
        super(strUnitID);
    }

    public boolean isLoaded() {
        return this.mAd != null && this.mAd.isReady();
    }

    @Override
    public void load(String strUnitName) {
        if (!this.checkToLoad()) {
            return;
        }

        if (this.mAd == null) {
            this.mAd = new com.mopub.mobileads.MoPubInterstitial(AdsManager.instance().getActivity(), this.mStrUnitID);
            this.mAd.setInterstitialAdListener(this);
        }
        this.mAd.load();
        this.setLoading(true);
    }

    @Override
    public void show(String strUnitName) {
        if (this.isLoaded()) {
            this.mAd.show();
        }
    }

    @Override
    public void onInterstitialLoaded(com.mopub.mobileads.MoPubInterstitial interstitial) {
        System.out.println("MoPubInterstitial onInterstitialLoaded");

        this.setLoaded(true);
        this.setLoading(false);
        this.onStatusUpdate("loaded");
    }

    @Override
    public void onInterstitialFailed(com.mopub.mobileads.MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
        System.out.println("MoPubInterstitial onInterstitialFailed errorCode="+errorCode);

        this.setLoaded(false);
        this.setLoading(false);
        this.onStatusUpdate("error");
    }

    @Override
    public void onInterstitialShown(com.mopub.mobileads.MoPubInterstitial interstitial) {
        System.out.println("MoPubInterstitial onInterstitialShown");

        this.onStatusUpdate("show");
    }

    @Override
    public void onInterstitialClicked(com.mopub.mobileads.MoPubInterstitial interstitial) {
        System.out.println("MoPubInterstitial onInterstitialClicked");
    }

    @Override
    public void onInterstitialDismissed(com.mopub.mobileads.MoPubInterstitial interstitial) {
        System.out.println("MoPubInterstitial onInterstitialDismissed");

        this.setLoading(false);
        this.setLoaded(false);
        this.onStatusUpdate("closed");
    }
}
