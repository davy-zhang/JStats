package cc.d_z.jstats;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author davy <br>
 *         2014年9月21日 下午12:33:14 <br>
 *         <B>The default encoding is UTF-8 </B><br>
 *         email: davy@d-z.cc<br>
 *         <a href="http://d-z.cc">d-z.cc</a><br>
 */
public class Counter implements Serializable{
	private static final long serialVersionUID = 7038837997315072376L;
	protected final AtomicLong value = new AtomicLong();

	public Counter() {
	}

	public Counter(long value) {
		this.value.set(value);
	}

	public long incr(long value) {
		return this.value.addAndGet(value);
	}

	public long get() {
		return this.value.get();
	}

	@Override
	public String toString() {
		return "Counter [" + (value != null ? "value=" + value : "") + "]";
	}

}
