package heignamerican.gpslogger;

import heignamerican.gpslogger.MainActivity.ActivityHandler;
import heignamerican.gpslogger.location.MyLocationner;
import heignamerican.gpslogger.location.MyLocationners.LoggerLocationer;
import heignamerican.gpslogger.util.android.AbstractForegroundLocalService;
import heignamerican.gpslogger.util.android.HandlerProxy.NullableContainer;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocalService extends AbstractForegroundLocalService {
	private final NullableContainer<ActivityHandler> mHandler = new NullableContainer<ActivityHandler>(ActivityHandler.class);
	private MyLocationner mLocationner;

	public void setActivityHandler(ActivityHandler aHandler) {
		mHandler.set(aHandler);
	}

	public void unsetActivityhandler() {
		mHandler.unset();
	}

	@Override
	protected void onStartWorking() {
		if (isWorking()) {
			mHandler.get().postMessage("作業中");
			return;
		}

		mLocationner = new LoggerLocationer(this, new LocationListener() {
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
				mHandler.get().postLocation(aLocation);
			}
		}) {
			@Override
			protected String createLog(Location aLocation) {
				return MyContext.createLog(MyContext.getCurrentMillis(), aLocation);
			}
		};

		mLocationner.startLocationing();

		mHandler.get().postMessage("開始");
	}

	@Override
	protected void onStopWorking() {
		if (!isWorking()) {
			mHandler.get().postMessage("停止中");
			return;
		}

		mLocationner.stopLocationing();
		mLocationner = null;

		mHandler.get().postMessage("終了");
	}

	@Override
	protected int getSmallIconDrawable() {
		return R.drawable.ic_launcher;
	}

	@Override
	protected String getNotificationText() {
		return "ロギングなう";
	}

	@Override
	protected Class<?> getActivityClass() {
		return MainActivity_.class;
	}
}
