package net.bitumin.wearabletest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class PatientProfileInformation extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_profile_information);

        final WatchViewStub stub = findViewById(R.id.patient_profile_information);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                String patientName = getIntent().getStringExtra("patient_name").replaceAll("^\"|\"$", "");
                String patientAge = getIntent().getStringExtra("patient_age").replaceAll("^\"|\"$", "") + " años";
                String patientGender = getIntent().getStringExtra("patient_gender").replaceAll("^\"|\"$", "");
                String patientNeeds = getIntent().getStringExtra("patient_needs").replaceAll("^\"|\"$", "");

                final String patientImage = getIntent().getStringExtra("patient_image");

                TextView patientNameTextView = findViewById(R.id.patientName);
                patientNameTextView.setText(patientName);

                TextView patientAgeTextView = findViewById(R.id.patientAge);
                patientAgeTextView.setText(patientAge);

                TextView additionalInfoTextView = findViewById(R.id.patientGenre);
                additionalInfoTextView.setText(patientGender);

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        loadPatientImageView(patientImage);
                    }
                }, 4000);
            }
        });
    }

    private void loadPatientImageView(String patientImage) {
        Intent intent = new Intent(PatientProfileInformation.this, PatientProfileImage.class);

        intent.putExtra("patient_image", patientImage);

        startActivity(intent);
    }
}
