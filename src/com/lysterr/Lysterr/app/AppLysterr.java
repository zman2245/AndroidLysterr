package com.lysterr.Lysterr.app;

import android.app.Application;
import com.lysterr.Lysterr.fragments.postflow.NewPostDescriptionModel;
import com.lysterr.Lysterr.globals.LocationProvider;
import com.lysterr.Lysterr.util.Registry;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

/**
 * Created by zfoster on 6/28/14.
 */
public class AppLysterr extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        NewPostDescriptionModel.init(getResources());

        Registry.sApp = this;
        Registry.sLocation = new LocationProvider();

        Parse.initialize(this, "i3rGPRho60WTnAB7wOupaqfSojUEYzLAW1bnYo98", "60G9w9O8vGcE5GHq24vQrDJaLvSSCnCO9FC55dSA");
        ParseFacebookUtils.initialize("207954616070416");
    }
}
