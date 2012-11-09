package heignamerican.gpslogger;

import heignamerican.gpslogger.logger.MyLogger;
import heignamerican.gpslogger.logger.MyLoggers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.location.Location;
import android.os.Environment;

public class MyContext {
	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public static MyLogger createLogger() {
		// return new MyLoggers.DebugLogger();
		return new MyLoggers.FileLogger(new File(MyContext.getMyappDirectory(), MyContext.getCurrentMillis() + ".log"));
	}

	public static long getCurrentMillis() {
		return System.currentTimeMillis();
	}

	public static Date getCurrentDate() {
		return new Date(getCurrentMillis());
	}

	public static String formatDate(Date aDate) {
		return DATE_FORMAT.format(aDate);
	}

	/**
	 * @param aTimeFromMillis
	 *            Java Epoch (long)
	 * @param aLocation
	 * @return
	 */
	public static String createLog(long aTimeFromMillis, Location aLocation) {
		return String.format("%s,%d,%f,%f,%f", DATE_FORMAT.format(new Date(aTimeFromMillis)), aTimeFromMillis, aLocation.getLongitude(), aLocation.getLatitude(), aLocation.getAltitude());
	}

	public static File getMyappDirectory() {
		return new File(Environment.getExternalStorageDirectory(), "Android/data/heignamerican.GPSLogger");
	}
}
