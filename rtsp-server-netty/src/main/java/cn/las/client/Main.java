package cn.las.client;

import cn.las.util.Md5Util;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/22
 */
public class Main {
    private static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 5) {
            logger.error("usage [action] [host] [port] [start] [end]");
            return;
        }

//        System.setProperty("io.netty.leakDetection.acquireAndReleaseOnly", "true");
//        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);

        String action = args[0];
        String host = args[1];
        int port = 554;
        try {
            port = Integer.parseInt(args[2]);
        } catch (Exception e) {
        }
        if ("push".equals(action)) {//push 10.100.102.29 554 0 4
            int start = 0;
            int end = 1;
            try {
                start = Integer.parseInt(args[3]);
                end = Integer.parseInt(args[4]);
            } catch (Exception e) {
            }
            final AbstractClient client = new ClientPush();
            for (int i = start; i < end; i++) {
                String url = "rtsp://" + host + ":" + port + "/" + Md5Util.md5(i + "");
                try {
                    client.start(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if ("pull".equals(action)) {//pull 10.100.102.29 554 813CAA9FECBBC2A50F7A8B9F5737C9E7 10
            String token = args[3];
            int total = 0;
            try {
                total = Integer.parseInt(args[4]);
            } catch (Exception e) {
            }
            String url = "rtsp://" + host + ":" + port + "/" + token;
            AbstractClient client = new ClientPull();
            for (int i = 0; i < total; i++) {
                if (i == 0) {
                    Thread.sleep(5000);
                }
                try {
                    client.start(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
