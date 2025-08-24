package com.example.batterycheck.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.batterycheck.R;
import com.yandex.mobile.ads.banner.BannerAdSize;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.MobileAds;

public class DocActivity extends AppCompatActivity {
    private static final String YANDEX_MOBILE_ADS_TAG = "YandexMobileAds";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc);

        MobileAds.initialize(this, () -> Log.d(YANDEX_MOBILE_ADS_TAG, "SDK initialized"));

        BannerAdView mBannerAdView = findViewById(R.id.banner_ad_view);
        String AdUnitId = "R-M-2733347-2";
        mBannerAdView.setAdUnitId(AdUnitId);
        mBannerAdView.setAdSize(BannerAdSize.stickySize(this, 350));

        AdRequest adRequest = new AdRequest.Builder().build();

        mBannerAdView.loadAd(adRequest);
    }
}

