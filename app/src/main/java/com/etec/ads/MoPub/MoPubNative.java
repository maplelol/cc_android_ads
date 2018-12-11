package com.etec.ads.MoPub;

import android.view.View;

import com.etec.ads.AdsManager;
import com.etec.ads.AdsUnit;
import com.etec.ads.NativeExitView;
import com.etec.ads.NativeSplashView;
import com.etec.ads.R;
import com.mopub.nativeads.AdapterHelper;
import com.mopub.nativeads.FacebookAdRenderer;
import com.mopub.nativeads.GooglePlayServicesAdRenderer;
import com.mopub.nativeads.MediaViewBinder;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.ViewBinder;

import java.util.HashMap;
import java.util.Map;

public class MoPubNative extends AdsUnit implements View.OnClickListener {
    private NativeAd mAdContent;

    private static Map<String,com.mopub.nativeads.MoPubNative> mAdLoaderMap;

    public MoPubNative(String strUnitID) {
        super(strUnitID);
    }

    @Override
    public void load(String strUnitName) {
        if (!this.checkToLoad()) {
            return;
        }

        com.mopub.nativeads.MoPubNative ad = this.registerAdLoader(strUnitName);
        ad.makeRequest();
        this.setLoading(true);
    }

    @Override
    public void show(String strUnitName) {
        if (!this.isLoaded()) {
            return;
        }

        AdapterHelper adapterHelper = new AdapterHelper(AdsManager.instance().getActivity(), 0, 3);
        // Retrieve the pre-built ad view that AdapterHelper prepared for us.
        View v = adapterHelper.getAdView(null, null, this.mAdContent, new ViewBinder.Builder(0).build());
        // Set the native event listeners (onImpression, and onClick).

        if (strUnitName.equals("MoPubNativeSplash")) {
            v.findViewById(R.id.btn_close_native_ad).setOnClickListener(this);
            NativeSplashView.instance().addAdView(v);
        } else if (strUnitName.equals("MoPubNativeExit")) {
            NativeExitView.instance().show(v, this);
        }

        AdsManager.setViewClickable(v.findViewById(R.id.xad_icon),false);
        //AdsManager.setViewClickable(v.findViewById(R.id.xad_media),false);

        this.onStatusUpdate("show");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_close_native_ad) {
            NativeSplashView.instance().close();

            this.mAdContent = null;
            this.setLoading(false);
            this.setLoaded(false);
            this.onStatusUpdate("closed");
        } else if (v.getId() == R.id.btn_native_exit_confirm) {
            NativeExitView.instance().close();

            this.mAdContent = null;
            this.setLoading(false);
            this.setLoaded(false);
            this.onStatusUpdate("confirm");
            //this.onStatusUpdate("closed");
        } else if (v.getId() == R.id.btn_native_exit_cancel) {
            NativeExitView.instance().close();

            this.mAdContent = null;
            this.setLoading(false);
            this.setLoaded(false);
            this.onStatusUpdate("closed");
        }
    }

    protected com.mopub.nativeads.MoPubNative registerAdLoader(String strUnitName) {
        if (mAdLoaderMap == null) {
            mAdLoaderMap = new HashMap<>();
        }

        if (mAdLoaderMap.get(strUnitName) == null) {
            final MoPubNative mThis = this;
            com.mopub.nativeads.MoPubNative ad = new com.mopub.nativeads.MoPubNative(AdsManager.instance().getActivity(),this.mStrUnitID,new com.mopub.nativeads.MoPubNative.MoPubNativeNetworkListener() {
                @Override
                public void onNativeLoad(NativeAd nativeAd) {
                    System.out.println("MoPubNative onNativeLoad");
                    mThis.setLoaded(true);
                    mThis.setLoading(false);
                    mThis.mAdContent = nativeAd;
                    mThis.onStatusUpdate("loaded");
                }

                @Override
                public void onNativeFail(NativeErrorCode errorCode) {
                    System.out.println("MoPubNative onNativeFail errorCode "+errorCode);
                    mThis.setLoaded(false);
                    mThis.setLoading(false);
                    mThis.onStatusUpdate("error");
                }
            });

            int fan_layout_id = -1;
            int admob_layout_id = -1;
            int mopub_layout_id = -1;
            if (strUnitName.equals("MoPubNativeSplash")) {
                fan_layout_id = R.layout.fan_native_splash_ad;
                admob_layout_id = R.layout.mopub_admob_native_splash_ad;
                mopub_layout_id = R.layout.mopub_native_splash_ad;
            } else if (strUnitName.equals("MoPubNativeExit")) {
                fan_layout_id = R.layout.fan_native_exit_ad;
                admob_layout_id = R.layout.mopub_admob_native_exit_ad;
                mopub_layout_id = R.layout.mopub_native_exit_ad;
            }

            FacebookAdRenderer fbAdRender = new FacebookAdRenderer(new FacebookAdRenderer.FacebookViewBinder.Builder(fan_layout_id)
                    .mediaViewId(R.id.xad_media)
                    .adIconViewId(R.id.xad_icon)
                    .titleId(R.id.xad_title)
                    .textId(R.id.xad_body)
                    .adChoicesRelativeLayoutId(R.id.xad_choices_container)
                    .callToActionId(R.id.xad_cta)
                    .build());
            ad.registerAdRenderer(fbAdRender);

            GooglePlayServicesAdRenderer googlePlayServicesAdRenderer = new GooglePlayServicesAdRenderer(new MediaViewBinder.Builder(admob_layout_id)
                    .mediaLayoutId(R.id.xad_media)
                    .iconImageId(R.id.xad_icon)
                    .titleId(R.id.xad_title)
                    .textId(R.id.xad_body)
                    .privacyInformationIconImageId(R.id.xad_privacy_ad_icon)
                    .callToActionId(R.id.xad_cta)
                    .build());
            ad.registerAdRenderer(googlePlayServicesAdRenderer);

            MoPubStaticNativeAdRenderer moPubStaticNativeAdRenderer = new MoPubStaticNativeAdRenderer(new ViewBinder.Builder(mopub_layout_id)
                    .mainImageId(R.id.xad_media)
                    .iconImageId(R.id.xad_icon)
                    .titleId(R.id.xad_title)
                    .textId(R.id.xad_body)
                    .privacyInformationIconImageId(R.id.xad_privacy_ad_icon)
                    .callToActionId(R.id.xad_cta)
                    .build());
            ad.registerAdRenderer(moPubStaticNativeAdRenderer);

            mAdLoaderMap.put(strUnitName,ad);

            return ad;
        } else {
            return mAdLoaderMap.get(strUnitName);
        }
    }
}
