package net.bitumin.wearabletest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PatientProfileImage extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_profile_image);

//        final WatchViewStub stub = findViewById(R.id.patient_profile_image);
//        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
//            @Override
//            public void onLayoutInflated(WatchViewStub stub) {
//                String patientImage = getIntent().getStringExtra("patient_image");
//            }
//        });
    }

    public void onPPClick(View view) {
        Intent intent = new Intent(PatientProfileImage.this, MainActivity.class);
        startActivity(intent);
    }
}
