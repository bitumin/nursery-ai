package net.bitumin.wearabletest;

import io.socket.client.Socket;

public class SocketSingleton {

    private static final SocketSingleton INSTANCE = new SocketSingleton();
    private Socket socket = null;
    private long lastTimestamp;

    private SocketSingleton() {
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

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }
}
