package heignamerican.gpslogger.location;
import heignamerican.gpslogger.MyContext;
import heignamerican.gpslogger.logger.MyLogger;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;

public class MyLocationners {
	private MyLocationners() {
		// インスタンス化禁止
	}

	public static abstract class LoggerLocationer extends MyLocationner {
		private MyLogger mLogger;

		public LoggerLocationer(Context aContext, LocationListener... aLocationListener) {
			super(aContext, aLocationListener);
		}

		@Override
		protected void setUp() {
			mLogger = MyContext.createLogger();
		}

		@Override
		protected void tearDown() {
			mLogger.close();
		}

		@Override
		protected void locationChanged(Location aLocation) {
			mLogger.writeLine(createLog(aLocation));
		}

		protected abstract String createLog(Location aLocation);
	}
}
