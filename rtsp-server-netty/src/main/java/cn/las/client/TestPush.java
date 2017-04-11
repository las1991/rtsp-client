package cn.las.client;

/**
 * Created by las on 2017/3/29.
 */
public class TestPush {
    public static void main(String[] args) {
        System.setProperty("push.file", "/Users/las/Downloads/test.mp4");
        String url = "rtsp://10.100.102.29:554/DD69960CE1DF6C27EBED2B7889CD8F5A";
        try {
            AbstractClient client = new ClientPush();
            client.start(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
