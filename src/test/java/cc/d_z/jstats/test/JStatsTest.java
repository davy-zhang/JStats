package cc.d_z.jstats.test;

import cc.d_z.jstats.JStats;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.*;

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
        for (int i = 1; i <= 10000; i++) {
            JStats.incr("b", i);
        }
        Assert.assertTrue(JStats.getCounter("b").get() == 5050L);
    }

    @Test
    public void testGauge1() {
        JStats.gauge("c", 1);
        JStats.gauge("c", 2);
        Assert.assertTrue(JStats.<Integer>getGauge("c").get() == 2);
    }

    @Test
    public void testGaugeSpeed() {
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
        Assert.assertEquals("ok", JStats.<String>getGauge("d").get());
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

    @Test
    public void testIncr2() {
        ExecutorService pool = Executors.newFixedThreadPool(8);
        final CountDownLatch latch = new CountDownLatch(1);
        int all = 10;
        for (int i = 0; i < all; i++) {
            pool.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        latch.await();
                        for (int i = 0; i < 1000; i++) {
                            JStats.incr("a");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        latch.countDown();
        pool.shutdown();
        try {
            pool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(JStats.toJson());
    }


    @Test
    public void testIncr3() throws InterruptedException {
        for (int i = 0; i < 1000000000; i++) {
            JStats.incr("test");
            Thread.sleep(100L);
        }
        System.out.println(JStats.toJson());
    }

    @Test
    public void testMetric3() throws InterruptedException {
        for (int i = 0; i < 100000; i++) {
            JStats.metric("m1", i);
            JStats.metric("m2", i);
            Thread.sleep(100L);
        }
    }

    @Test
    public void testMetric() {
        ExecutorService pool = Executors.newFixedThreadPool(8);
        final CountDownLatch latch = new CountDownLatch(1);
        int all = 10;
        for (int i = 0; i < all; i++) {
            pool.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        latch.await();
                        for (int i = 0; i < 1000; i++) {
                            JStats.metric("a", 1);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        latch.countDown();
        pool.shutdown();
        try {
            pool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(JStats.toJson());
    }


}