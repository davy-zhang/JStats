package cc.d_z.jstats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author davy <br>
 *         2015-04-12 13:38 <br>
 *         <B>The default encoding is UTF-8 </B><br>
 *         email: davy@d-z.cc<br>
 *         <a href="http://d-z.cc">d-z.cc</a><br>
 */
public class SocketOutput {
    private static ServerSocket server;
    private static final AtomicBoolean opened = new AtomicBoolean();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                if (server != null) {
                    try {
                        server.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }));
    }

    public enum Cammand {
        Json("json") {
            public void execute(PrintStream out) {
                out.println(JStats.toJson());
            }
        }, Help("help") {
            public void execute(PrintStream out) {
                out.print("支持的命令有:");
                for (Cammand cammand : Cammand.values()) {
                    out.println(cammand.cammand);
                }
            }
        }, Show("show") {
            public void execute(PrintStream out) {
                showStart(out);
                showCounters(out);
                showGauge(out);
                showMetric(out);
                showEnd(out);
            }
        }, ShowCounters("sc") {
            public void execute(PrintStream out) {
                showStart(out);
                showCounters(out);
                showEnd(out);
            }
        }, ShowGauges("sg") {
            public void execute(PrintStream out) {
                showStart(out);
                showGauge(out);
                showEnd(out);
            }
        }, ShowMetrics("sm") {
            public void execute(PrintStream out) {
                showStart(out);
                showMetric(out);
                showEnd(out);
            }
        };

        private String cammand;

        private Cammand(String cammand) {
            this.cammand = cammand;
        }

        public static Cammand build(String ca) {
            Cammand[] cammands = Cammand.values();
            for (Cammand cammand : cammands) {
                if (cammand.cammand.equals(ca)) {
                    return cammand;
                }
            }
            return Help;
        }

        public abstract void execute(PrintStream out);
    }

    public static void open(final int port) throws IOException {
        if (opened.compareAndSet(false, true)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        server = new ServerSocket(port);
                        for (; ; ) {
                            Socket socket = server.accept();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            PrintStream out = new PrintStream(socket.getOutputStream());
                            String cammand;
                            for (; (cammand = reader.readLine()) != null; ) {
                                Cammand.build(cammand).execute(out);
                            }
                            socket.close();
                        }
                    } catch (Exception e) {
                        new RuntimeException(e);
                    }
                }
            }, "socketOutput").start();
        }
    }


    public static void showGauge(PrintStream out) {
        Map<String, Gauge> gauges = JStats.getGauges();
        for (String key : gauges.keySet()) {
            out.println("gauge." + key + ":" + gauges.get(key).get());
        }
    }

    public static void showStart(PrintStream out) {
        out.println("--------------------------------------- Show Start -------------------------------------");
    }

    public static void showEnd(PrintStream out) {
        out.println("--------------------------------------- Show End ---------------------------------------");
    }


    public static void showCounters(PrintStream out) {
        Map<String, Counter> counters = JStats.getCounters();
        for (String key : counters.keySet()) {
            out.println("counter." + key + ":" + counters.get(key).get());
        }
    }

    public static void showMetric(PrintStream out) {
        Map<String, Metric> metrics = JStats.getMetrics();
        for (String key : metrics.keySet()) {
            out.println("metric." + key + ".sum:" + metrics.get(key).getSum());
            out.println("metric." + key + ".count:" + metrics.get(key).getCount());
            out.println("metric." + key + ".avg:" + new DecimalFormat("#.00").format(metrics.get(key).getAvg()));
            out.println("metric." + key + ".max:" + metrics.get(key).getMax());
            out.println("metric." + key + ".min:" + metrics.get(key).getMin());
        }
    }
}
