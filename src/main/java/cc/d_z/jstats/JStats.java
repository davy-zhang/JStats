package cc.d_z.jstats;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

/**
 * @author davy <br>
 *         2014年9月21日 下午12:30:21 <br>
 *         <B>The default encoding is UTF-8 </B><br>
 *         email: davy@d-z.cc<br>
 *         <a href="http://d-z.cc">d-z.cc</a><br>
 */
public class JStats implements Serializable{

	private static final long serialVersionUID = 4286362397612396589L;
	private static final ConcurrentHashMap<String, Counter> COUNTER_MAP = new ConcurrentHashMap<String, Counter>();
	private static final ConcurrentHashMap<String, Gauge> GAUGE_MAP = new ConcurrentHashMap<String, Gauge>();
	private static final ConcurrentHashMap<String, Metric> METRIC_MAP = new ConcurrentHashMap<String, Metric>();

	public static long incr(String name, long value) {
		Counter counter = COUNTER_MAP.get(name);
		if (counter == null) {
			COUNTER_MAP.put(name, new Counter(value));
			return value;
		} else {
			return counter.incr(value);
		}
	}

	public static long incr(String name) {
		return incr(name, 1);
	}

	public static Counter getCounter(String name) {
		return COUNTER_MAP.get(name);
	}

	public static Map<String, Counter> getCounters() {
		return getNewMap(COUNTER_MAP);
	}

	public static <T> T gauge(String name, T value) {
		Gauge<T> gauge = GAUGE_MAP.get(name);
		if (gauge == null) {
			GAUGE_MAP.put(name, new Gauge<T>(value));
			return value;
		} else {
			return gauge.gauge(value);
		}
	}

	public static <T> void gauge(String name, Callable<T> callable) {
		GAUGE_MAP.putIfAbsent(name, new Gauge<T>(callable));
	}

	public static <T> Gauge<T> getGauge(String name) {
		return GAUGE_MAP.get(name);
	}

	public static Map<String, Gauge> getGauges() {
		return getNewMap(GAUGE_MAP);
	}

	public static void metric(String name, long value) {
		Metric metric = METRIC_MAP.get(name);
		if (metric == null) {
			METRIC_MAP.put(name, new Metric(value));
		} else {
			metric.metric(value);
		}
	}

	public static void metric(String name, Runnable runnable, boolean isNano) {
		TimeMetric metric = (TimeMetric) METRIC_MAP.get(name);
		if (metric == null) {
			metric = new TimeMetric();
			metric.metric(runnable, isNano);
			METRIC_MAP.put(name, metric);
		} else {
			metric.metric(runnable, isNano);
		}
	}

	public static Map<String, Map<String, ? extends Object>> get() {
		HashMap<String, Map<String, ? extends Object>> newMap = new HashMap<String, Map<String, ? extends Object>>();
		newMap.put("counters", COUNTER_MAP);
		newMap.put("gauges", GAUGE_MAP);
		newMap.put("metrics", METRIC_MAP);
		return newMap;
	}

	public static void metric(String name, Runnable runnable) {
		metric(name, runnable, true);
	}

	public static <T> T metric(String name, Callable<T> callable, boolean isNano) {
		TimeMetric metric = (TimeMetric) METRIC_MAP.get(name);
		T t = null;
		if (metric == null) {
			metric = new TimeMetric();
			t = metric.metric(callable, isNano);
			METRIC_MAP.put(name, metric);
		} else {
			t = metric.metric(callable, isNano);
		}
		return t;
	}

	public static <T> T metric(String name, Callable<T> callable) {
		return metric(name, callable, true);
	}

	public static <T extends Metric> T getMetric(String name) {
		return (T) METRIC_MAP.get(name);
	}

	public static Map<String, Metric> getMetrics() {
		return getNewMap(METRIC_MAP);
	}

	private static <T> Map<String, T> getNewMap(Map<String, T> oldMap) {
		return new HashMap<String, T>(oldMap);
	}

	public static String toJson() {
		Map<String, Map<String, ? extends Object>> map = new HashMap<String, Map<String, ? extends Object>>();
		Map<String, Long> countMap = new HashMap<String, Long>();
		for (String key : COUNTER_MAP.keySet()) {
			countMap.put(key, COUNTER_MAP.get(key).get());
		}
		Map<String, Object> gaugeMap = new HashMap<String, Object>();
		for (String key : GAUGE_MAP.keySet()) {
			gaugeMap.put(key, GAUGE_MAP.get(key).get());
		}
		Map<String, Map<String, Number>> metricMap = new HashMap<String, Map<String, Number>>();
		for (String key : METRIC_MAP.keySet()) {
			metricMap.put(key, METRIC_MAP.get(key).toMap());
		}
		map.put("counters", countMap);
		map.put("gauges", gaugeMap);
		map.put("metrics", metricMap);
		return new Gson().toJson(map);
	}
}
