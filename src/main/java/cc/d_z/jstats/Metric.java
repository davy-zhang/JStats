package cc.d_z.jstats;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author davy <br>
 *         2014年9月21日 下午1:32:30 <br>
 *         <B>The default encoding is UTF-8 </B><br>
 *         email: davy@d-z.cc<br>
 *         <a href="http://d-z.cc">d-z.cc</a><br>
 */
public class Metric implements Serializable {
	private static final long serialVersionUID = 3094336070708206143L;
	protected AtomicLong sum = new AtomicLong();
	protected AtomicLong max = new AtomicLong();
	protected AtomicLong min = new AtomicLong();
	protected AtomicLong count = new AtomicLong();
	protected AtomicBoolean init = new AtomicBoolean(false);

	public Metric(long value) {
		this.metric(value);
	}

	public Metric() {
	}

	public void metric(long value) {
		sum.addAndGet(value);
		count.incrementAndGet();
		if (init.compareAndSet(false, true)) {
			min.set(value);
			max.set(value);
		} else {
			if (value > max.get())
				max.set(value);
			if (value < min.get())
				min.set(value);
		}
	}

	public long getSum() {
		return sum.get();
	}

	public long getCount() {
		return count.get();
	}

	public long getMax() {
		return max.get();
	}

	public long getMin() {
		return min.get();
	}

	public double getAvg() {
		return count.get() == 0 ? 0 : sum.get() * 1.0d / count.get();
	}

	@Override
	public String toString() {
		return "Metric [sum=" + getSum() + ", count=" + getCount() + ", max=" + getMax() + ", min=" + getMin() + ", avg=" + getAvg() + "]";
	}

	/**
	 * @return
	 */
	public Map<String, Number> toMap() {
		Map<String, Number> map = new HashMap<String, Number>();
		map.put("sum", getSum());
		map.put("count", getCount());
		map.put("max", getMax());
		map.put("min", getMin());
		map.put("average", getAvg());
		return map;
	}

}
