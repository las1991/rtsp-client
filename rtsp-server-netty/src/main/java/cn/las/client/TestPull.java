package cn.las.client;

/**
 * Created by las on 2017/4/10.
 */
public class TestPull {
    public static void main(String[] args) {
//        System.setProperty("push.file", "/Users/las/Downloads/test.mp4");
        String url = "rtsp://10.100.102.29:554/813CAA9FECBBC2A50F7A8B9F5737C9E7";
        try {
            AbstractClient client = new ClientPull();
            client.start(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
