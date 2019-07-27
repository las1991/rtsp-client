package com.darkmi.server.core;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/2/15
 */
public class RtspClient extends Thread {

    private enum Status {
        init, options, describe, setup, play, pause, teardown
    }

    private Integer status;

    public void run() {

    }


    public static void main(String[] args) {

    }

}
