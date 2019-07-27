package com.las.observer;

/**
 * @author cn.las
 * @date 18-10-11
 */
public interface Observer {

    void update(Observable o, Object arg);
}
