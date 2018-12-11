package com.etec.ads.FAN;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etec.ads.AdsManager;
import com.etec.ads.AdsUnit;
import com.etec.ads.NativeExitView;
import com.etec.ads.NativeSplashView;
import com.etec.ads.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;

import java.util.ArrayList;
import java.util.List;

public class FANNative extends AdsUnit implements NativeAdListener,View.OnClickListener {
    private NativeAd mAd;

    public FANNative(String strUnitID) {
        super(strUnitID);
    }

    @Override
    public void load(String strUnitName) {
        if (!this.checkToLoad()) {
            return;
        }

        this.mAd = new NativeAd(AdsManager.instance().getActivity(), this.mStrUnitID);
        this.mAd.setAdListener(this);
        this.mAd.loadAd();
        this.setLoading(true);
    }

    @Override
    public void show(String strUnitName) {
        if (this.isLoaded()) {
            ViewGroup adView = null;
            LayoutInflater inflater = LayoutInflater.from(AdsManager.instance().getActivity());
            if (strUnitName.equals("FANNativeSplash")) {
                adView = (RelativeLayout)inflater.inflate(R.layout.fan_native_splash_ad, null, false);
                adView.findViewById(R.id.btn_close_native_ad).setOnClickListener(this);
            } else if (strUnitName.equals("FANNativeExit")) {
                adView = (LinearLayout)inflater.inflate(R.layout.fan_native_exit_ad, null);
            }

            if (adView != null) {
                com.facebook.ads.AdIconView nativeAdIcon = adView.findViewById(R.id.xad_icon);
                TextView nativeAdTitle = adView.findViewById(R.id.xad_title);
                MediaView nativeAdMedia = adView.findViewById(R.id.xad_media);
                TextView nativeAdSocialContext = adView.findViewById(R.id.xad_social_context);
                TextView nativeAdBody = adView.findViewById(R.id.xad_body);
                Button nativeAdCallToAction = adView.findViewById(R.id.xad_cta);

                // Set the Text.
                nativeAdTitle.setText(mAd.getAdvertiserName());
                nativeAdSocialContext.setText(mAd.getAdSocialContext());
                nativeAdBody.setText(mAd.getAdBodyText());
                nativeAdCallToAction.setText(mAd.getAdCallToAction());
                nativeAdCallToAction.setVisibility(mAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);

                // Add the AdChoices icon
                RelativeLayout adChoicesContainer = (RelativeLayout) adView.findViewById(R.id.xad_choices_container);
                AdChoicesView adChoicesView = new AdChoicesView(AdsManager.instance().getActivity(), mAd, true);
                adChoicesContainer.addView(adChoicesView);

                // Register the Title and CTA button to listen for clicks.
                List<View> clickableViews = new ArrayList<>();
                //clickableViews.add(nativeAdTitle);
                clickableViews.add(nativeAdCallToAction);
                mAd.registerViewForInteraction(adView, nativeAdMedia, nativeAdIcon,clickableViews);

                if (strUnitName.equals("FANNativeSplash")) {
                    NativeSplashView.instance().addAdView(adView);
                } else if (strUnitName.equals("FANNativeExit")) {
                    NativeExitView.instance().show(adView,this);
                }
            }
        }
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        System.out.println("FANNative onError:"+adError.getErrorMessage());

        this.mAd.destroy();
        this.mAd = null;
        this.setLoaded(false);
        this.setLoading(false);
        this.onStatusUpdate("error");
    }

    @Override
    public void onAdLoaded(Ad ad) {
        System.out.println("FANNative onAdLoaded");

        this.setLoaded(true);
        this.setLoading(false);
        this.onStatusUpdate("loaded");
    }

    @Override
    public void onAdClicked(Ad ad) {
        System.out.println("FANNative onAdClicked");
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        System.out.println("FANNative onLoggingImpression");

        this.onStatusUpdate("show");
    }

    @Override
    public void onMediaDownloaded(Ad var1) {
        System.out.println("FANNative onMediaDownloaded");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_close_native_ad) {
            NativeSplashView.instance().close();

            this.mAd.destroy();
            this.mAd = null;
            this.setLoaded(false);
            this.setLoading(false);
            this.onStatusUpdate("closed");
        } else if (v.getId() == R.id.btn_native_exit_confirm) {
            NativeExitView.instance().close();

            this.setLoaded(false);
            this.setLoading(false);
            this.onStatusUpdate("confirm");
            //this.onStatusUpdate("closed");
        } else if (v.getId() == R.id.btn_native_exit_cancel) {
            NativeExitView.instance().close();

            this.mAd.destroy();
            this.mAd = null;
            this.setLoaded(false);
            this.setLoading(false);
            this.onStatusUpdate("closed");
        }
    }
}
