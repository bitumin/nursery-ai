package net.bitumin.wearabletest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import io.socket.client.Socket;

public class FifteenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_fifteen);
    }

    public void on15Click(View view) {
        emitAlert();
        Intent intent = new Intent(FifteenActivity.this, UpdateSuccessTimer.class);
        startActivity(intent);
    }

    private void emitAlert() {
        MyApp myApp = (MyApp) getApplication();
        Socket socket = myApp.getSocket();
        socket.emit("message-alert");
    }
}
