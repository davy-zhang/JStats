package cc.d_z.jstats;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author davy <br>
 *         2014年9月21日 下午1:05:46 <br>
 *         <B>The default encoding is UTF-8 </B><br>
 *         email: davy@d-z.cc<br>
 *         <a href="http://d-z.cc">d-z.cc</a><br>
 */
public class Gauge<T> implements Serializable {
	private static final long serialVersionUID = 5888965390118460983L;
	protected AtomicReference<T> value = new AtomicReference<T>();
	protected Callable<T> callable;

	public Gauge(Callable<T> callable) {
		this.callable = callable;
	}

	public Gauge() {
	}

	public Gauge(T value) {
		this.value.set(value);
	}

	public T gauge(T value) {
		this.value.set(value);
		return value;
	}

	public T get() {
		try {
			return callable != null ? callable.call() : value.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return "Gauge [" + (value != null ? "value=" + value + ", " : "") + (callable != null ? "callable=" + callable : "") + "]";
	}

}
