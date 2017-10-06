package mopub.com.androidcustommanualnativestatic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mopub.common.UrlAction;
import com.mopub.common.UrlHandler;
import com.mopub.common.util.Drawables;
import com.mopub.nativeads.BaseNativeAd;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.NativeImageHelper;
import com.mopub.nativeads.StaticNativeAd;
import com.mopub.nativeads.ViewBinder;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    /**
     * Facebook: d766dac54f70456a8106644c2812924f
     * MoPub Sample: 11a17b188668469fb0412708c3d16813
     * AdMob: 57fd53ccacbf4e49a91b4f2cde681923
     * Flurry: f067181d6f554495bdb4824e4001e750
     */

    private static final String AD_UNIT_ID = "11a17b188668469fb0412708c3d16813";
    private final String TAG = this.getClass().getName();

    private MoPubNative moPubNative;
    private MoPubNative.MoPubNativeNetworkListener moPubNativeNetworkListener;
    private RelativeLayout parentView;
    private NativeAd.MoPubNativeEventListener moPubNativeEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parentView = (RelativeLayout) findViewById(R.id.parentView);

        moPubNativeEventListener = new NativeAd.MoPubNativeEventListener() {

            @Override
            public void onImpression(View view) {
                Log.d(TAG, "onImpression");
            }

            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick");
            }
        };


        moPubNativeNetworkListener = new MoPubNative.MoPubNativeNetworkListener() {

            @Override
            public void onNativeLoad(final NativeAd nativeAd) {

                nativeAd.setMoPubNativeEventListener(moPubNativeEventListener);

                BaseNativeAd baseNativeAd = nativeAd.getBaseNativeAd();

                if (baseNativeAd instanceof StaticNativeAd) {
                    StaticNativeAd staticNativeAd = (StaticNativeAd) baseNativeAd;

                    // Ad assets extraction
                    String adTitle = staticNativeAd.getTitle();
                    Log.d("MoPub", "Ad title: " + adTitle);

                    String cTA = staticNativeAd.getCallToAction();
                    Log.d("MoPub", "CTA: " + cTA);

                    String clickDestinationUrl = staticNativeAd.getClickDestinationUrl();
                    Log.d("MoPub", "clickDestinationUrl: " + clickDestinationUrl);

                    String iconImageUrl = staticNativeAd.getIconImageUrl();
                    Log.d("MoPub", "iconImageUrl: " + iconImageUrl);

                    String mainImageUrl = staticNativeAd.getMainImageUrl();
                    Log.d("MoPub", "mainImageUrl: " + mainImageUrl);

                    String adText = staticNativeAd.getText();
                    Log.d("MoPub", "adText: " + adText);

                    final String privacyInformationIconClickThroughUrl = staticNativeAd.getPrivacyInformationIconClickThroughUrl();
                    Log.d("MoPub", "privacyInformationIconClickThroughUrl: " + privacyInformationIconClickThroughUrl);

                    String privacyInformationIconImageUrl = staticNativeAd.getPrivacyInformationIconImageUrl();
                    Log.d("MoPub", "privacyInformationIconImageUrl: " + privacyInformationIconImageUrl);

                    TextView adTextTV = (TextView) findViewById(R.id.adTextTV);
                    TextView adTitleTV = (TextView) findViewById(R.id.adTitleTV);
                    Button cTABtn = (Button) findViewById(R.id.cTABtn);
                    ImageView iconImageIV = (ImageView) findViewById(R.id.iconImageIV);
                    ImageView mainImageIV = (ImageView) findViewById(R.id.mainImageIV);
                    ImageView privacyInfoIconIV = (ImageView) findViewById(R.id.privacyIV);

                    // Ad assets populating

                    adTextTV.setText(adText);
                    adTitleTV.setText(adTitle);
                    cTABtn.setText(cTA);

                    if (privacyInformationIconImageUrl == null) {
                        privacyInfoIconIV.setImageDrawable(
                                Drawables.NATIVE_PRIVACY_INFORMATION_ICON.createDrawable(getApplicationContext()));
                    } else {
                        NativeImageHelper.loadImageView(privacyInformationIconImageUrl,
                                privacyInfoIconIV);
                    }

                    privacyInfoIconIV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            new UrlHandler.Builder()
                                    .withSupportedUrlActions(
                                            UrlAction.IGNORE_ABOUT_SCHEME,
                                            UrlAction.OPEN_NATIVE_BROWSER,
                                            UrlAction.OPEN_IN_APP_BROWSER,
                                            UrlAction.HANDLE_SHARE_TWEET,
                                            UrlAction.FOLLOW_DEEP_LINK_WITH_FALLBACK,
                                            UrlAction.FOLLOW_DEEP_LINK)
                                    .build().handleUrl(getApplicationContext(), privacyInformationIconClickThroughUrl);
                        }
                    });


                    NativeImageHelper.loadImageView(iconImageUrl,
                            iconImageIV);

                    NativeImageHelper.loadImageView(mainImageUrl,
                            mainImageIV);

                    staticNativeAd.prepare(parentView);
                }
            }// onNativeLoad

            @Override
            public void onNativeFail(NativeErrorCode errorCode) {
                Log.d(TAG, "onNativeFail: " + errorCode.toString());
            }
        };

        moPubNative = new MoPubNative(getApplicationContext(), AD_UNIT_ID, moPubNativeNetworkListener);
        ViewBinder viewBinder = new ViewBinder(new ViewBinder.Builder(0));

        MoPubStaticNativeAdRenderer moPubStaticNativeAdRenderer = new MoPubStaticNativeAdRenderer(viewBinder);
        moPubNative.registerAdRenderer(moPubStaticNativeAdRenderer);

        // Create third-party renderers as needed

        moPubNative.makeRequest();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        moPubNative.destroy();
        moPubNative = null;

        moPubNativeNetworkListener = null;
        moPubNativeEventListener = null;
    }
}
