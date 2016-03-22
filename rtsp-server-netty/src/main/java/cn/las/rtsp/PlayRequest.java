package cn.las.rtsp;

import cn.las.client.Client;
import io.netty.handler.codec.http.HttpRequest;

import java.util.concurrent.Callable;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/22
 */
public class PlayRequest implements Callable<HttpRequest>{
    private Client client;

    public PlayRequest(Client client) {
        this.client = client;
    }

    @Override
    public HttpRequest call() throws Exception {
        return null;
    }
}
