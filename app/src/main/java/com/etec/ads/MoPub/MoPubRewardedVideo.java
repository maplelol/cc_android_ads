package com.etec.ads.MoPub;

import com.etec.ads.AdsManager;
import com.etec.ads.AdsUnit;
import com.mopub.common.MoPubReward;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideos;

import java.util.Set;

public class MoPubRewardedVideo extends AdsUnit {
    protected static MoPubRewardedVideoListener mMoPubRewardedVideoListener = null;

    public MoPubRewardedVideo(String strUnitID) {
        super(strUnitID);
    }

    @Override
    public void load(String strUnitName) {
        if (!this.checkToLoad()) {
            return;
        }

        initListener();
        MoPubRewardedVideos.loadRewardedVideo(this.mStrUnitID);
        this.setLoading(true);
    }

    @Override
    public void show(String strUnitName) {
        if (this.isLoaded()) {
            MoPubRewardedVideos.showRewardedVideo(this.mStrUnitID);
        }
    }

    protected void initListener() {
        if (mMoPubRewardedVideoListener == null) {
            mMoPubRewardedVideoListener = new MoPubRewardedVideoListener() {
                @Override
                public void onRewardedVideoLoadSuccess(String adUnitId) {
                    // Called when the video for the given adUnitId has loaded. At this point you should be able to call MoPubRewardedVideos.showRewardedVideo(String) to show the video.
                    System.out.println("MoPubRewardedVideo onRewardedVideoLoadSuccess " + adUnitId);

                    AdsUnit ad = AdsManager.instance().getAds(adUnitId);
                    if (ad != null && MoPubRewardedVideos.hasRewardedVideo(adUnitId)) {
                        ad.setLoaded(true);
                        ad.setLoading(false);
                        ad.onStatusUpdate("loaded");
                    }
                }

                @Override
                public void onRewardedVideoLoadFailure(String adUnitId, MoPubErrorCode errorCode) {
                    // Called when a video fails to load for the given adUnitId. The provided error code will provide more insight into the reason for the failure to load.
                    System.out.println("MoPubRewardedVideo onRewardedVideoLoadFailure " + adUnitId + " errorCode " + errorCode);

                    AdsUnit ad = AdsManager.instance().getAds(adUnitId);
                    if (ad != null) {
                        ad.setLoaded(false);
                        ad.setLoading(false);
                        ad.onStatusUpdate("error");
                    }
                }

                @Override
                public void onRewardedVideoStarted(String adUnitId) {
                    // Called when a rewarded video starts playing.
                    System.out.println("MoPubRewardedVideo onRewardedVideoStarted " + adUnitId);

                    AdsUnit ad = AdsManager.instance().getAds(adUnitId);
                    if (ad != null) {
                        ad.onStatusUpdate("show");
                    }
                }

                @Override
                public void onRewardedVideoPlaybackError(String adUnitId, MoPubErrorCode errorCode) {
                    //  Called when there is an error during video playback.
                    System.out.println("MoPubRewardedVideo onRewardedVideoPlaybackError " + adUnitId);

                    AdsUnit ad = AdsManager.instance().getAds(adUnitId);
                    if (ad != null) {
                        ad.setLoaded(false);
                        ad.setLoading(false);
                        ad.onStatusUpdate("error");
                    }
                }

                @Override
                public void onRewardedVideoClicked(String adUnitId) {

                }

                @Override
                public void onRewardedVideoClosed(String adUnitId) {
                    // Called when a rewarded video is closed. At this point your application should resume.
                    System.out.println("MoPubRewardedVideo onRewardedVideoClosed " + adUnitId);

                    AdsUnit ad = AdsManager.instance().getAds(adUnitId);
                    if (ad != null) {
                        ad.setLoaded(false);
                        ad.setLoading(false);
                        ad.onStatusUpdate("closed");
                    }
                }

                @Override
                public void onRewardedVideoCompleted(Set<String> adUnitIds, MoPubReward reward) {
                    // Called when a rewarded video is completed and the user should be rewarded.
                    // You can query the reward object with boolean isSuccessful(), String getLabel(), and int getAmount().

                    for (String adUnitId : adUnitIds) {
                        System.out.println("MoPubRewardedVideo onRewardedVideoCompleted " + adUnitId);

                        AdsUnit ad = AdsManager.instance().getAds(adUnitId);
                        if (ad != null) {
                            ad.onStatusUpdate("rewarded");
                        }
                    }
                }
            };
            MoPubRewardedVideos.setRewardedVideoListener(mMoPubRewardedVideoListener);
        }
    }
}
