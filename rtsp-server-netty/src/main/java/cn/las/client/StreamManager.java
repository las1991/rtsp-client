package cn.las.client;

import cn.las.mp4parser.H264Sample;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/4/21
 */
public class StreamManager {

    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private static Map<String, H264Sample> map = new HashMap<>();

    public static Map<String, H264Sample> getMap() {
        return map;
    }

    public static void put(String channelId, H264Sample h264Sample) {
        lock.writeLock().lock();
        try {
            map.put(channelId, h264Sample);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void remove(String key) {
        lock.writeLock().lock();
        try {
            map.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static H264Sample get(String key) {
        lock.readLock().lock();
        try {
            return map.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }
}
