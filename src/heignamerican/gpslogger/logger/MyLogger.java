package heignamerican.gpslogger.logger;

import java.io.Closeable;

public interface MyLogger extends Closeable {
	/**
	 * @param aLog
	 *            NEW_LINE 不要
	 */
	public void writeLine(String aLog);

	/**
	 */
	public void close();
}
