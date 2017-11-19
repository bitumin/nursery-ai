package net.bitumin.wearabletest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class UpdateSuccess extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_success);
    }

    public void onUSClick(View view) {
        Intent intent = new Intent(UpdateSuccess.this, MainActivity.class);
        startActivity(intent);
    }
}
