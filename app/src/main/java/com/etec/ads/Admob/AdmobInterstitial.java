package com.etec.ads.Admob;

import com.etec.ads.AdsManager;
import com.etec.ads.AdsUnit;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class AdmobInterstitial extends AdsUnit {
    private InterstitialAd mAd;

    public AdmobInterstitial(final String strUnitID) {
        super(strUnitID);
    }

    @Override
    public void load(String strUnitName) {
        if (!this.checkToLoad()) {
            return;
        }

        final AdmobInterstitial mThis = this;
        this.mAd = new InterstitialAd(AdsManager.instance().getActivity());
        this.mAd.setAdUnitId(this.mStrUnitID);
        this.mAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                System.out.println("AdmobInterstitial onAdClosed");

                mThis.mAd = null;
                mThis.setLoaded(false);
                mThis.setLoading(false);
                mThis.onStatusUpdate("closed");
            }

            @Override
            public void onAdFailedToLoad(int var1) {
                System.out.println("AdmobInterstitial onAdFailedToLoad");

                mThis.mAd = null;
                mThis.setLoaded(false);
                mThis.setLoading(false);
                mThis.onStatusUpdate("error");
            }

            @Override
            public void onAdLeftApplication() {
                System.out.println("AdmobInterstitial onAdLeftApplication");
            }

            @Override
            public void onAdOpened() {
                System.out.println("AdmobInterstitial onAdOpened");

                mThis.onStatusUpdate("show");
            }

            @Override
            public void onAdLoaded() {
                System.out.println("AdmobInterstitial onAdLoaded");

                mThis.setLoaded(true);
                mThis.setLoading(false);
                mThis.onStatusUpdate("loaded");
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        this.mAd.loadAd(adRequest);
        this.setLoading(true);
    }

    @Override
    public void show(String strUnitName) {
        if (this.isLoaded()) {
            this.mAd.show();
        }
    }
}
