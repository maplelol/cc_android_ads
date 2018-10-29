package com.etec.ads.Admob;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.etec.ads.AdsManager;
import com.etec.ads.AdsUnit;
import com.etec.ads.NativeExitView;
import com.etec.ads.NativeSplashView;
import com.etec.ads.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.util.List;

public class AdmobNative extends AdsUnit implements View.OnClickListener {
    private UnifiedNativeAd mAd;

    public AdmobNative(final String strUnitID) {
        super(strUnitID);
    }

    @Override
    public void load(String strUnitName) {
        if (!this.checkToLoad()) {
            return;
        }

        final AdmobNative mThis = this;

        AdLoader.Builder builder = new AdLoader.Builder(AdsManager.instance().getActivity(), this.mStrUnitID);

        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            // OnUnifiedNativeAdLoadedListener implementation.
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                System.out.println("AdmobNative onUnifiedNativeAdLoaded");
                mThis.mAd = unifiedNativeAd;
                mThis.setLoading(false);
                mThis.setLoaded(true);
                mThis.onStatusUpdate("loaded");
            }
        });

        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(false).build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                System.out.println("AdmobNative onAdFailedToLoad errorCode="+errorCode);
                mThis.setLoading(false);
                mThis.setLoaded(false);
                mThis.onStatusUpdate("error");
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());
        this.setLoading(true);
    }

    @Override
    public void show(String strUnitName) {
        if (!this.isLoaded()) {
            return;
        }

        VideoController vc = this.mAd.getVideoController();

        // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
        // VideoController will call methods on this object when events occur in the video
        // lifecycle.
        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            public void onVideoEnd() {
                // Publishers should allow native ads to complete video playback before refreshing
                // or replacing them with another ad in the same UI location.
                super.onVideoEnd();
            }
        });

        ViewGroup adContrainer = null;
        UnifiedNativeAdView adView = null;
                LayoutInflater inflater = LayoutInflater.from(AdsManager.instance().getActivity());
        if (strUnitName.equals("AdmobNativeSplash")) {
            adContrainer = (ViewGroup)inflater.inflate(R.layout.admob_native_splash_ad, null, false);
            adContrainer.findViewById(R.id.btn_close_native_ad).setOnClickListener(this);
            adView = adContrainer.findViewById(R.id.ad_view);
        } else if (strUnitName.equals("AdmobNativeExit")) {
            adContrainer = (ViewGroup)inflater.inflate(R.layout.admob_native_exit_ad, null);
            adView = (UnifiedNativeAdView)adContrainer;
        }
        //ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //AdsUtil.getActivity().addContentView(nativeAdContainer,lp);

        MediaView mediaView = adView.findViewById(R.id.xad_media);
        ImageView mainImageView = adView.findViewById(R.id.xad_image);

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeAppInstallAd has a video asset.
        if (vc.hasVideoContent()) {
            adView.setMediaView(mediaView);
            mainImageView.setVisibility(View.GONE);
        } else {
            adView.setImageView(mainImageView);
            mediaView.setVisibility(View.GONE);

            // At least one image is guaranteed.
            List<NativeAd.Image> images = this.mAd.getImages();
            mainImageView.setImageDrawable(images.get(0).getDrawable());
        }

        adView.setHeadlineView(adView.findViewById(R.id.xad_title));
        adView.setBodyView(adView.findViewById(R.id.xad_body));
        adView.setCallToActionView(adView.findViewById(R.id.xad_cta));
        adView.setIconView(adView.findViewById(R.id.xad_icon));
        adView.setPriceView(adView.findViewById(R.id.xad_text_price));
        adView.setStarRatingView(adView.findViewById(R.id.xad_stars));
        adView.setStoreView(adView.findViewById(R.id.xad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.xad_social_context));

        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(this.mAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(this.mAd.getBody());
        ((Button) adView.getCallToActionView()).setText(this.mAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (this.mAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    this.mAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (this.mAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(this.mAd.getPrice());
        }

        if (this.mAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(this.mAd.getStore());
        }

        if (this.mAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(this.mAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (this.mAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(this.mAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
        adView.setNativeAd(this.mAd);

        if (strUnitName.equals("AdmobNativeSplash")) {
            NativeSplashView.instance().addAdView(adContrainer);
        } else if (strUnitName.equals("AdmobNativeExit")) {
            NativeExitView.instance().show(adContrainer,this);
        }

        this.onStatusUpdate("show");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_close_native_ad) {
            NativeSplashView.instance().close();

            this.mAd = null;
            this.setLoaded(false);
            this.setLoading(false);
            this.onStatusUpdate("closed");
        } else if (v.getId() == R.id.btn_native_exit_confirm) {
            NativeExitView.instance().close();

            this.mAd = null;
            this.setLoaded(false);
            this.setLoading(false);
            this.onStatusUpdate("confirm");
            //this.onStatusUpdate("closed");
        } else if (v.getId() == R.id.btn_native_exit_cancel) {
            NativeExitView.instance().close();

            this.mAd = null;
            this.setLoaded(false);
            this.setLoading(false);
            this.onStatusUpdate("closed");
        }
    }
}
