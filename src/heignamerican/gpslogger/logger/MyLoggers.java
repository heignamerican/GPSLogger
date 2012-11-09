package heignamerican.gpslogger.logger;

import heignamerican.myutils.IOUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyLoggers {
	public static class DebugLogger implements MyLogger {
		protected static final Logger LOGGER = LoggerFactory.getLogger(DebugLogger.class);

		@Override
		public void writeLine(String aLog) {
			LOGGER.debug(aLog);
		}

		@Override
		public void close() {
		}
	}

	public static class FileLogger implements MyLogger {
		private static final int BUFFER_SIZE = 10;
		private final File mFile;
		private final ConcurrentLinkedQueue<String> mQueue;
		private final AtomicInteger mCount = new AtomicInteger(0);
		private final ExecutorService mThreadPool;

		public FileLogger(File aFile) {
			mFile = aFile;
			mQueue = new ConcurrentLinkedQueue<String>();
			mThreadPool = Executors.newFixedThreadPool(1);
		}

		@Override
		public void writeLine(String aLog) {
			mQueue.add(aLog);

			if (mCount.get() > BUFFER_SIZE) {
				mThreadPool.execute(new Runnable() {
					@Override
					public void run() {
						save();
					}
				});
			}
		}

		private synchronized void save() {
			BufferedWriter tWriter = null;
			try {
				mFile.getParentFile().mkdirs();
				tWriter = IOUtil.createBufferedWriter(mFile, "UTF-8");

				while (true) {
					final String tLine = mQueue.poll();
					if (tLine == null)
						break;
					tWriter.write(tLine);
					tWriter.newLine();
				}
			} catch (IOException aCause) {
				throw new RuntimeException(aCause);
			} finally {
				IOUtil.closeQuietly(tWriter);
			}
		}

		@Override
		public void close() {
			save();
		}
	}
}
