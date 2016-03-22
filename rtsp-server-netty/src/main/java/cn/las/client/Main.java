package cn.las.client;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/22
 */
public class Main {
    public static void main(String[] args) {
        String url = "rtsp://54.222.135.41:554/4FE2A3AE23D4C959419186930DC9CE98.sdp";
        try {
            Client client = new Client(url);
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
