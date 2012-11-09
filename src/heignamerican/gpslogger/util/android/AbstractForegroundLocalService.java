package heignamerican.gpslogger.util.android;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public abstract class AbstractForegroundLocalService extends Service {
	protected static int ONGOING_NOTIFICATION = 1;

	@Override
	public int onStartCommand(Intent aIntent, int aFlags, int aStartId) {
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent aIntent) {
		return new LocalBinder();
	}

	private boolean mWorking = false;

	public boolean isWorking() {
		return mWorking;
	}

	public void startWorking() {
		if (!mWorking) {
			startForeground(ONGOING_NOTIFICATION, createNotification());
			onStartWorking();
			mWorking = true;
		}
	}

	public void stopWorking() {
		if (mWorking) {
			stopForeground(true);
			onStopWorking();
			mWorking = false;
		}
	}

	protected abstract void onStartWorking();

	protected abstract void onStopWorking();

	private Notification createNotification() {
		String tNotificationText = getNotificationText();
		Intent tNotificationIntent = new Intent(this, getActivityClass());
		PendingIntent tPendingIntent = PendingIntent.getActivity(this, 0, tNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Builder tBuilder = new Notification.Builder(this) //
				.setSmallIcon(getSmallIconDrawable()) //
				.setLargeIcon(null) //
				.setOngoing(true) //
				.setWhen(System.currentTimeMillis()) //
				.setContentText(tNotificationText) //
				.setTicker(tNotificationText) //
				.setContentTitle(tNotificationText) //
				.setContentText(tNotificationText) //
				.setContentIntent(tPendingIntent) //
		;
		final Notification tNotification = tBuilder.getNotification();
		tNotification.flags = Notification.FLAG_FOREGROUND_SERVICE | Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
		return tNotification;
	}

	/**
	 * この通知を使う場合は、AndroidManifest.xml の activity 要素(このメソッドで指定する Activity) の属性値として<br>
	 * {@code android:launchMode="singleTask"}<br>
	 * をつけるべき、かも？
	 */
	protected abstract Class<?> getActivityClass();

	protected abstract String getNotificationText();

	protected abstract int getSmallIconDrawable();

	public class LocalBinder extends Binder {
		public AbstractForegroundLocalService getService() {
			return AbstractForegroundLocalService.this;
		}
	}
}
