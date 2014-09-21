package cc.d_z.jstats;

import java.util.concurrent.Callable;

/**
 * @author davy <br>
 *         2014年9月21日 下午6:47:59 <br>
 *         <B>The default encoding is UTF-8 </B><br>
 *         email: davy@d-z.cc<br>
 *         <a href="http://d-z.cc">d-z.cc</a><br>
 */
public class TimeMetric extends Metric {

	private static final long serialVersionUID = 8883729037210352915L;

	public TimeMetric() {
		super();
	}

	public TimeMetric(long value) {
		super(value);
	}

	public void metric(Runnable runnable, boolean isNano) {
		long start = isNano ? System.nanoTime() : System.currentTimeMillis();
		runnable.run();
		long end = isNano ? System.nanoTime() : System.currentTimeMillis();
		metric(end - start);
	}

	public void metric(Runnable runnable) {
		metric(runnable, true);
	}

	public <T> T metric(Callable<T> callable, boolean isNano) {
		long start = isNano ? System.nanoTime() : System.currentTimeMillis();
		T t = null;
		try {
			t = callable.call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		long end = isNano ? System.nanoTime() : System.currentTimeMillis();
		metric(end - start);
		return t;
	}

	public <T> T metric(Callable<T> callable) {
		return metric(callable, true);
	}

}
