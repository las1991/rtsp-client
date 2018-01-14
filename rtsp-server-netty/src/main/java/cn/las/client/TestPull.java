package cn.las.client;

/**
 * Created by las on 2017/4/10.
 */
public class TestPull {
    public static void main(String[] args) {
//        System.setProperty("push.file", "/Users/las/Downloads/test.mp4");
//        String url = "rtsp://10.100.102.29:554/210360B871EECBD4D0AE1B9DCC24C568";
        String url = "rtsp://127.0.0.1:5454/210360B871EECBD4D0AE1B9DCC24C568";
        try {
            AbstractClient client = new ClientPull();
            client.start(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
