package com.v2soft.AndLib.sketches;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class GeoHelper {
    public static final String PROVIDERS[] = new String[]{LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER};

    public void getLastKnownLocationAsync(Context context, String provider, LocationListener listener, Looper looper) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location = manager.getLastKnownLocation(provider);
        if ( location == null ) {
            manager.requestSingleUpdate(provider, listener, looper);
        } else {
            listener.onLocationChanged(location);
        }
    }
}
