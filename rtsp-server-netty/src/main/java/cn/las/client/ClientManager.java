package cn.las.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/22
 */
public class ClientManager {
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private static Map<String, AbstractClient> clientMap = new HashMap<>();

    public static Map<String, AbstractClient> getClientMap() {
        return clientMap;
    }

    public static void put(String channelId, AbstractClient client) {
        lock.writeLock().lock();
        try {
            clientMap.put(channelId, client);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void remove(String key) {
        lock.writeLock().lock();
        try {
            clientMap.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static AbstractClient get(String key) {
        lock.readLock().lock();
        try {
            return clientMap.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }

}
