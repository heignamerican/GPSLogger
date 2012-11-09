package heignamerican.gpslogger.location;

import java.util.Arrays;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public abstract class MyLocationner {
	private final Context mContext;
	private final LocationListener[] mLocationListeners;
	private LocationManager mLocationManager;

	public MyLocationner(Context aContext, LocationListener... aLocationListener) {
		mContext = aContext;

		mLocationListeners = Arrays.copyOf(aLocationListener, aLocationListener.length + 1);
		mLocationListeners[mLocationListeners.length - 1] = new LocationListener() {
			@Override
			public void onStatusChanged(String aProvider, int aStatus, Bundle aExtras) {
			}

			@Override
			public void onProviderEnabled(String aProvider) {
			}

			@Override
			public void onProviderDisabled(String aProvider) {
			}

			@Override
			public void onLocationChanged(Location aLocation) {
				locationChanged(aLocation);
			}
		};
	}

	public void startLocationing() {
		setUp();

		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		clearLocationListener();
		init();
	}

	public void stopLocationing() {
		clearLocationListener();

		tearDown();
	}

	protected abstract void locationChanged(Location aLocation);

	protected abstract void setUp();

	protected abstract void tearDown();

	private static final int MIN_TIME_MILLIS = 1000;
	private static final int MIN_DISTANCE = 0;

	private void init() {
		final String tProvider = getProvider();
		if (mLocationManager.isProviderEnabled(tProvider)) {
			for (LocationListener tLocationListener : mLocationListeners) {
				mLocationManager.requestLocationUpdates(tProvider, MIN_TIME_MILLIS, MIN_DISTANCE, tLocationListener);
			}

			Location tLastKnownLocation = mLocationManager.getLastKnownLocation(tProvider);
			if (tLastKnownLocation != null)
				locationChanged(tLastKnownLocation);
		}
	}

	private String getProvider() {
		return getBestProvide();
		// return getGpsProvider();
	}

	String getBestProvide() {
		Criteria tCriteria = new Criteria();
		tCriteria.setAccuracy(Criteria.ACCURACY_FINE);
		tCriteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		return mLocationManager.getBestProvider(tCriteria, true);
	}

	String getGpsProvider() {
		return LocationManager.GPS_PROVIDER;
	}

	private void clearLocationListener() {
		for (LocationListener tLocationListener : mLocationListeners) {
			mLocationManager.removeUpdates(tLocationListener);
		}
	}
}
