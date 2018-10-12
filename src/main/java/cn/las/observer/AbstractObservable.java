package cn.las.observer;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author las
 * @date 18-10-11
 */
public abstract class AbstractObservable implements Observable {

    private CopyOnWriteArrayList<Observer> obs;

    public AbstractObservable() {
        this.obs = new CopyOnWriteArrayList<>();
    }

    @Override
    public boolean addObserver(Observer o) {
        return obs.add(o);
    }

    @Override
    public boolean deleteObserver(Observer o) {
        return obs.remove(o);
    }

    @Override
    public void notifyObservers(Object arg) {
        obs.forEach(x -> {
            x.update(this, arg);
        });
    }

    @Override
    public void notifyObservers() {
        obs.forEach(x -> {
            x.update(this, null);
        });
    }

    @Override
    public void deleteObservers() {
        obs.clear();
    }

    @Override
    public int countObservers() {
        return obs.size();
    }
}
