package cn.las.client;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/22
 */
public class Main {
    public static void main(String[] args) {
        String url = "rtsp://54.222.135.41:554/662B69D43FA7224DC8ADB27C8F904F7A.sdp";
        try {
            AbstractClient client = new ClientPull(url);
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
