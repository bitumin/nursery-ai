package net.bitumin.wearabletest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import io.socket.client.Socket;

public class SevenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_seven);
    }

    public void on7Click(View view) {
        emitAlert();
        Intent intent = new Intent(SevenActivity.this, UpdateSuccessTimer.class);
        startActivity(intent);
    }

    private void emitAlert() {
        MyApp myApp = (MyApp) getApplication();
        Socket socket = myApp.getSocket();
        socket.emit("message-alert");
    }
}
