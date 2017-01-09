package com.gp.browser.manager.ad;

import android.content.Context;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.gp.browser.R;
import com.gp.browser.utils.Constant;
import com.gp.utils.LogUtils;

/**
 * Created by wuxiaojun on 17-1-9.
 */

public class BannerAdUtils {

    private Context context;
    private AdView adView;

    public BannerAdUtils(Context context) {
        this.context = context;
        adView = new AdView(context, Constant.FACEBOOK_PLACEMENT_ID_BANNER, AdSize.BANNER_320_50);
        AdSettings.addTestDevice("08bb6a71da61b942148a6f86cf639ca1");
    }

    public void loadAdView(){
        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                LogUtils.e("onError  "+adError.getErrorCode()+"  "+adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                LogUtils.e("onAdLoaded");
            }

            @Override
            public void onAdClicked(Ad ad) {

            }
        });
        adView.loadAd();
    }

    public AdView getBannerAd() {
        return adView;
    }

    public void refreshAdView(){
        if(adView != null){
            adView.destroy();
        }
        adView = new AdView(context, Constant.FACEBOOK_PLACEMENT_ID_BANNER, AdSize.BANNER_320_50);
    }

}
