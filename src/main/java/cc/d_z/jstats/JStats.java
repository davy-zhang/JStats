package cc.d_z.jstats;

import com.google.gson.Gson;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.IOException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author davy <br>
 *         2014年9月21日 下午12:30:21 <br>
 *         <B>The default encoding is UTF-8 </B><br>
 *         email: davy@d-z.cc<br>
 *         <a href="http://d-z.cc">d-z.cc</a><br>
 */
public class JStats implements Serializable {

    private static final long serialVersionUID = 4286362397612396589L;
    protected static final ConcurrentHashMap<String, Counter> COUNTER_MAP = new ConcurrentHashMap<String, Counter>();
    protected static final ConcurrentHashMap<String, Gauge> GAUGE_MAP = new ConcurrentHashMap<String, Gauge>();
    protected static final ConcurrentHashMap<String, Metric> METRIC_MAP = new ConcurrentHashMap<String, Metric>();

    static {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName counterName = new ObjectName("cc.d_z.jstats:type=CounterMBean");
            ObjectName gaugeName = new ObjectName("cc.d_z.jstats:type=GaugeMBean");
            ObjectName metricName = new ObjectName("cc.d_z.jstats:type=MetricMBean");
            mbs.registerMBean(new CounterMBean(), counterName);
            mbs.registerMBean(new GaugeMBean(), gaugeName);
            mbs.registerMBean(new MetricMBean(), metricName);

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static long incr(String name, long value) {
        if (!COUNTER_MAP.containsKey(name)) {
            Counter counter = COUNTER_MAP.putIfAbsent(name, new Counter(value));
            if (counter == null) {
                return value;
            }
        }
        Counter counter = COUNTER_MAP.get(name);
        return counter.incr(value);
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

    public static <T> Map<String, Gauge<T>> getGauges() {
        return new HashMap<String, Gauge<T>>(GAUGE_MAP);
    }

    public static void metric(String name, long value) {
        Metric metric = METRIC_MAP.get(name);
        if (metric == null) {
            Metric metricValue = METRIC_MAP.putIfAbsent(name, new Metric(value));
            if (metricValue != null) {
                metricValue.metric(value);
            }
        } else {
            metric.metric(value);
        }
    }

    public static void metric(String name, Runnable runnable, boolean isNano) {
        TimeMetric metric = (TimeMetric) METRIC_MAP.get(name);
        if (metric == null) {
            metric = new TimeMetric();
            metric.metric(runnable, isNano);
            TimeMetric metricValue = (TimeMetric) METRIC_MAP.putIfAbsent(name, metric);
            if (metricValue != null) {
                metricValue.metric(runnable, isNano);
            }
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
            TimeMetric metricValue = (TimeMetric) METRIC_MAP.putIfAbsent(name, metric);
            if (metricValue != null) {
                t = metricValue.metric(callable, isNano);
            }
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

    public static void clear() {
        clearCounters();
        clearGauges();
        clearMetrics();
    }

    public static void clearCounters() {
        COUNTER_MAP.clear();
    }

    public static void clearGauges() {
        GAUGE_MAP.clear();
    }

    public static void clearMetrics() {
        METRIC_MAP.clear();
    }

    public static void openSocketOutput(int port) throws IOException {
        SocketOutput.open(port);
    }


    public static void closeSocketOutput() {
        SocketOutput.shutDown();
    }
}
