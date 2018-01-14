package cn.las.client;

import io.netty.handler.ssl.OpenSsl;

/**
 * Created by las on 2017/3/29.
 */
public class TestPush {
    public static void main(String[] args) {
        System.out.println(OpenSsl.availableOpenSslCipherSuites());
        System.out.println("===============");
        System.out.println(OpenSsl.availableJavaCipherSuites());
        System.setProperty("push.file", "/Users/las/Downloads/test.mp4");
        String url = "rtsp://10.100.102.29:1554/3596289F783F87494CE6AF1C273FDCD5&live.sdp";
        try {
            AbstractClient client = new ClientPush();
            client.start(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
