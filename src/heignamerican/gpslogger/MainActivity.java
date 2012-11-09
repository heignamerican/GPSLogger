package heignamerican.gpslogger;

import heignamerican.gpslogger.util.android.AbstractForegroundLocalService.LocalBinder;
import heignamerican.gpslogger.util.android.HandlerProxy;
import heignamerican.gpslogger.util.android.HandlerProxy.HasUIMethod;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {
	private boolean mIsBound = false;
	private MyLocalService mLocalService;

	@ViewById(R.id.textview)
	TextView mTextView;

	@ViewById(R.id.start_button)
	Button mStartButton;

	@Click(R.id.start_button)
	void onStartButtonClicked() {
		mLocalService.startWorking();
	}

	@ViewById(R.id.finish_button)
	Button mFinishButton;

	@Click(R.id.finish_button)
	void onFinishButtonClicked() {
		mLocalService.stopWorking();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@AfterViews
	void afterCreate() {
		mStartButton.setText("start");
		mFinishButton.setText("stop");
	}

	@Override
	protected void onStart() {
		super.onStart();

		startService(new Intent(MainActivity.this, MyLocalService.class));
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!mIsBound) {
			bindService(new Intent(MainActivity.this, MyLocalService.class), mConnection, Context.BIND_AUTO_CREATE);
			mIsBound = true;
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName aName, IBinder aService) {
			mLocalService = (MyLocalService) ((LocalBinder) aService).getService();
			mLocalService.setActivityHandler(mActivityHandler);
		}

		@Override
		public void onServiceDisconnected(ComponentName aName) {
			mLocalService.unsetActivityhandler();
			mLocalService = null;
		}

	};

	@Override
	protected void onPause() {
		super.onPause();

		if (mIsBound) {
			unbindService(mConnection);
			mIsBound = false;
		}
	}

	public interface ActivityHandler {
		void postMessage(String aString);

		void postLocation(Location aLocation);
	}

	private ActivityHandler mActivityHandler = HandlerProxy.getUIHandlerProxy(new UIActivityHandler());

	@HasUIMethod
	private class UIActivityHandler implements ActivityHandler {
		@Override
		public void postMessage(String aString) {
			mTextView.append(aString);
			mTextView.append("\n");
		}

		@Override
		public void postLocation(Location aLocation) {
			updateUI(aLocation);
		}
	}

	private void updateUI(Location aLocation) {
		double tLongitude = aLocation.getLongitude();
		double tLatitude = aLocation.getLatitude();
		mTextView.append(String.format("%s %f,%f%n", MyContext.formatDate(MyContext.getCurrentDate()), tLongitude, tLatitude));
	}
}
