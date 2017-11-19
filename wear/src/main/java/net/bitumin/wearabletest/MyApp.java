package net.bitumin.wearabletest;

import android.app.Application;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MyApp extends Application {
    private Socket socket;

    public MyApp() {
        try {
            socket = IO.socket("http://f78c1f31.ngrok.io");
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
