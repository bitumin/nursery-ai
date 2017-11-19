package net.bitumin.wearabletest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateSuccessTimer extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_success);

        final WatchViewStub stub = findViewById(R.id.update_success);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(UpdateSuccessTimer.this, MainActivity.class);
                        startActivity(intent);
                    }
                }, 1200);
            }
        });
    }
}
