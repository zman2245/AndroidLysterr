package com.lysterr.Lysterr.globals;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Wraps location service interaction to provide longitude and latitude
 */
public class LocationProvider implements LocationListener {
    private double mLongitude = Integer.MAX_VALUE;
    private double mLatitude = Integer.MAX_VALUE;

    public void startChecking(Context context) {
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        if (lm == null) {
            return;
        }

        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null)
        {
            // a location is available
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 21600000, 500, this);
    }

    public void stopChecking(Context context) {
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        if (lm == null) {
            return;
        }

        lm.removeUpdates(this);
    }

    public boolean hasValidLocation() {
        return mLatitude != Integer.MAX_VALUE;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLongitude = location.getLongitude();
        mLatitude = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
