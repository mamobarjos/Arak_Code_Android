package com.arakadds.arak.common.helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.arakadds.arak.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class GoogleAdsHelper {
    private static final String TAG = "GoogleAdsHelper";

    public static void initBannerGoogleAdView(Context context, AdView bannerAdView) {
        MobileAds.initialize(context, initializationStatus -> {

            //bannerAdView.setAdUnitId(context.getResources().getString(R.string.google_ad_mobe_banner_id));
            AdRequest adRequest = new AdRequest.Builder().build();
            bannerAdView.loadAd(adRequest);

            bannerAdView.setAdListener(new AdListener() {
                @Override
                public void onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.
                }

                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    // Code to be executed when an ad request fails.
                    bannerAdView.setVisibility(View.GONE);
                }

                @Override
                public void onAdImpression() {
                    // Code to be executed when an impression is recorded
                    // for an ad.
                }

                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    Log.d(TAG, "onAdLoaded: called");
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }
            });
        });
    }

    public static void initInterstitialGoogleAdView(Activity activity) {
        InterstitialAd[] mIssnterstitialAd = new InterstitialAd[1];

        MobileAds.initialize(activity, initializationStatus -> {
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(activity, activity.getResources().getString(R.string.google_ad_mobe_Interstitial_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mIssnterstitialAd[0] = interstitialAd;
                        setFullScreenContentCallback(activity, mIssnterstitialAd[0]);
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mIssnterstitialAd[0] = null;
                    }
                });
    }

    private static void setFullScreenContentCallback(Activity activity, InterstitialAd interstitialAd) {

        InterstitialAd[] mIssnterstitialAd = new InterstitialAd[1];
        mIssnterstitialAd[0] = interstitialAd;

        mIssnterstitialAd[0].setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.");
                mIssnterstitialAd[0] = null;
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.");
                mIssnterstitialAd[0] = null;
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.");
            }
        });

        if (mIssnterstitialAd[0] != null) {
            mIssnterstitialAd[0].show(activity);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

}
