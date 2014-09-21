package cc.d_z.jstats.test;

import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Test;

import cc.d_z.jstats.JStats;

/**
 * @author davy <br>
 *         2014年9月21日 下午12:46:47 <br>
 *         <B>The default encoding is UTF-8 </B><br>
 *         email: davy@d-z.cc<br>
 *         <a href="http://d-z.cc">d-z.cc</a><br>
 */
public class JStatsTest {

	/**
	 * Test method for {@link cc.d_z.jstats.JStats#incr(java.lang.String, long)}
	 * .
	 */
	@Test
	public void testIncrSpeed() {
		long num = 100000000L;
		for (long i = 1; i <= num; i++) {
			JStats.metric("incrUseTime", new Runnable() {
				public void run() {
					JStats.incr("a");
				}
			});
		}
		System.out.println(JStats.getMetric("incrUseTime"));
	}

	@Test
	public void testIncr() {
		for (int i = 1; i <= 100; i++) {
			JStats.incr("b", i);
		}
		Assert.assertTrue(JStats.getCounter("b").get() == 5050L);
	}

	@Test
	public void testGauge1() {
		JStats.gauge("c", 1);
		JStats.gauge("c", 2);
		Assert.assertTrue(JStats.<Integer> getGauge("c").get() == 2);
	}
	
	@Test
	public void testGaugeSpeed(){
		long num = 100000000L;
		for (long i = 1; i <= num; i++) {
			JStats.metric("gaugeUseTime", new Runnable() {
				public void run() {
					JStats.gauge("gaugeUseTime", "a");
				}
			});
		}
		System.out.println(JStats.getMetric("gaugeUseTime"));
	}

	@Test
	public void testGauge2() {
		JStats.gauge("d", new Callable<String>() {
			public String call() throws Exception {
				return "ok";
			}
		});
		Assert.assertEquals("ok", JStats.<String> getGauge("d").get());
	}

	@Test
	public void testJson() {
		JStats.incr("a", 2);
		JStats.gauge("b", 3);
		JStats.gauge("c", "ok");
		JStats.gauge("d", new Callable<Double>() {
			public Double call() throws Exception {
				return 1.8d;
			}
		});
		JStats.metric("e", 6);
		JStats.metric("e", 5);
		String json = JStats.toJson();
		System.out.println(json);
	}
}
