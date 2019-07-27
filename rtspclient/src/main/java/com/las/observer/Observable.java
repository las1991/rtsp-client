package com.las.observer;

/**
 * @author las
 * @date 18-10-11
 */
public interface Observable {

    boolean addObserver(Observer o);

    boolean deleteObserver(Observer o);

    void notifyObservers(Object arg);

    void notifyObservers();

    void deleteObservers();

    int countObservers();

}
