package com.lysterr.Lysterr.app;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ui.ParseLoginActivity;
import com.parse.ui.ParseLoginConfig;

/**
 * Created by zfoster on 6/28/14.
 */
public class AppLysterr extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "i3rGPRho60WTnAB7wOupaqfSojUEYzLAW1bnYo98", "60G9w9O8vGcE5GHq24vQrDJaLvSSCnCO9FC55dSA");
        ParseFacebookUtils.initialize("207954616070416");
    }
}
