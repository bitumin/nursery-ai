package net.bitumin.wearabletest;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by SusuReloaded on 19/11/2017.
 */

public class SocketSingleton {

    private static final SocketSingleton INSTANCE = new SocketSingleton();

    private Socket socket = null;

    private SocketSingleton() {
        if (null == socket || socket.equals(null)) {
            try {
                socket = IO.socket("http://f78c1f31.ngrok.io");
                socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                    public void call(Object... arg0) {
                        System.out.println("connected to sockets");
                    }
                }).on("request-nurse-push", new Emitter.Listener() {
                    public void call(Object... arg0) {
                        System.out.println("push");
                    }
                }).on("request-nurse-push-end", new Emitter.Listener() {
                    public void call(Object... arg0) {
                        System.out.println("push end");
                    }
                }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                    public void call(Object... arg0) {
                        System.out.println("disconnected from sockets");
                    }
                });
                socket.connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public static SocketSingleton getInstance() {
        return INSTANCE;
    }

    public Socket getInstanceSocket() {
        if (null == socket || socket.equals(null)) {
            new SocketSingleton();
        }
        return socket;
    }

}
