package net.bitumin.wearabletest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class PatientProfileInformation extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_profile_information);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    loadPatientImageView();
                }
            }, 2000);
    }

    private void loadPatientImageView() {
        Intent intent = new Intent(PatientProfileInformation.this, PatientProfileImage.class);
        startActivity(intent);
    }
}
