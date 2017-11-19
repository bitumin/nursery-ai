package net.bitumin.wearabletest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class FifteenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_fifteen);
    }

    public void on15Click(View view) {
        Intent intent = new Intent(FifteenActivity.this, UpdateSuccessTimer.class);
        startActivity(intent);
    }
}
