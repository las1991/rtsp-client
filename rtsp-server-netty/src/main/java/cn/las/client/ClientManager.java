package cn.las.client;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/22
 */
public class ClientManager {


    private static ConcurrentHashMap<String, AbstractClient.ClientSession> clientMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, AbstractClient.ClientSession> getClientMap() {
        return clientMap;
    }

    public static void put(String channelId, AbstractClient.ClientSession client) {
        clientMap.put(channelId, client);
    }

    public static void remove(String key) {
        clientMap.remove(key);
    }

    public static AbstractClient.ClientSession get(String key) {
        return clientMap.get(key);
    }

}
