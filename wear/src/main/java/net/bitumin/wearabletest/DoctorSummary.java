package net.bitumin.wearabletest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DoctorSummary extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_summary);
    }

    public void onDSClick(View view) {
        Intent intent = new Intent(DoctorSummary.this, MainActivity.class);
        startActivity(intent);
    }
}
