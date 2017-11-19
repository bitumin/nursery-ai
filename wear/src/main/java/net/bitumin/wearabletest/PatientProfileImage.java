package net.bitumin.wearabletest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;

public class PatientProfileImage extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_profile_image);

        final WatchViewStub stub = findViewById(R.id.patient_profile_image);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                String patientImageUrl = getIntent().getStringExtra("patient_image");

                new DownloadImageTask((ImageView) findViewById(R.id.patientProfileImage))
                        .execute(patientImageUrl);
            }
        });
    }

    public void onPPClick(View view) {
        Intent intent = new Intent(PatientProfileImage.this, MainActivity.class);
        startActivity(intent);
    }
}


class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    @SuppressLint("StaticFieldLeak")
    private ImageView bmImage;

    DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0].replace("\"", "");
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}
