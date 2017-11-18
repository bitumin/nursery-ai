package net.bitumin.wearabletest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends Activity {

    private static final int SPEECH_RECOGNIZER_REQUEST_CODE = 100;
    //private static final String BOT_CLIENT_KEY = "0f7eaf5e97204360bf2251d0982c361e";
    private static final String BOT_CLIENT_KEY = "9ecd9dc048a446a1a29a48b3235c914f";
    private static final String INTENT_NEXT_PATIENT = "next";
    private static final String INTENT_UPDATE_RECEIVED = "enviado";
    private static final String INTENT_DOCTOR_SUMMARY = "doctor";
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        initSocketIO();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.activity_main);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
    }

    /**
     * Start speech recognizer
     */
    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));

        // Start the Activity that handles the recognised text
        try {
            startActivityForResult(intent, SPEECH_RECOGNIZER_REQUEST_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SPEECH_RECOGNIZER_REQUEST_CODE: {

                if (resultCode == RESULT_OK && null != data) {

                    String recognizedText;
                    List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    recognizedText = results.get(0);


                    try {
                        getResponseFromBot(getApplicationContext(), recognizedText);
                    } catch (Exception ex) {
                        mTextView.setText(R.string.error_getting_response);
                    }
                }
                break;
            }
        }
    }

    public void onMicrophoneClick(View view) {
        startSpeechRecognition();
    }

    @SuppressLint("StaticFieldLeak")
    private void getResponseFromBot(final Context context, String recognizedText) {
//        if (!isInternetAvailable()) {
//            Toast.makeText(getApplicationContext(),
//                    R.string.no_internet_error,
//                    Toast.LENGTH_SHORT).show();
//            return;
//        }

        AIConfiguration configuration = new AIConfiguration(BOT_CLIENT_KEY,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        final AIDataService dataService = new AIDataService(context, configuration);

        try {
            AIRequest request = new AIRequest();
            request.setQuery(recognizedText);

            new AsyncTask<AIRequest, Void, AIResponse>() {
                @Override
                protected AIResponse doInBackground(AIRequest... requests) {
                    final AIRequest request = requests[0];
                    try {
                        return dataService.request(request);
                    } catch (AIServiceException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(AIResponse response) {
                    if (null == response) {
                        // do nothing
                    } else if (response.getStatus().getCode() == 200) {
                        String intent = ""; // TODO: get the fucking intent from the response!
                        switch (intent) {
                            case INTENT_DOCTOR_SUMMARY: {
                                loadDoctorSummaryView();
                                break;
                            }
                            case INTENT_NEXT_PATIENT: {
                                loadNextPatientView();
                                break;
                            }
                            case INTENT_UPDATE_RECEIVED: {
                                loadUpdateReceivedView();
                                break;
                            }
                        }

                        readSpeech(response.getResult().getFulfillment().getSpeech());
                    } else {
                        response.getStatus().getErrorDetails();
                    }
                }
            }.execute(request);

            // AIResponse response = dataService.request(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void readSpeech(String speech) {
        // TODO: speech not supported!
    }

    private void loadUpdateReceivedView() {
        setContentView(R.layout.update_success);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.update_success);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                // TODO: display the received message!
            }
        });
    }

    private void loadNextPatientView() {
        setContentView(R.layout.patient_profile_information);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.patient_profile_information);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                // Display the patient personal information
                loadPatientInformationView();

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Display the patient image (fullscreen, dynamic)
                        new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                                .execute("https://i.pinimg.com/736x/03/73/94/037394216591a5a8ebc906bb5ad2c4c9--monica-bellucci-makeup-monica-monica.jpg");
                    }
                }, 2000);
            }
        });
    }

    private void loadPatientInformationView() {
        setContentView(R.layout.patient_profile_information);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.patient_profile_image);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                // TODO: display the patient data on screen! (dynamic!)
            }
        });
    }

    private void loadDoctorSummaryView() {
        setContentView(R.layout.activity_main);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.activity_main);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                // TODO: display the doctor summary (hardcoded!)
            }
        });
    }

//    public boolean isInternetAvailable() {
//        try {
//            InetAddress ipAddr = InetAddress.getByName("google.com");
//            return !ipAddr.equals("");
//        } catch (Exception e) {
//            return false;
//        }
//    }

//    private void initSocketIO() {
//        Socket socket;
//        try {
//            socket = IO.socket("http://f78c1f31.ngrok.io");
//            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
//                public void call(Object... arg0) {
//                    Toast.makeText(getApplicationContext(),
//                            R.string.warning_connected_sockets,
//                            Toast.LENGTH_SHORT).show();
//                }
//            }).on("request-nurse-push", new Emitter.Listener() {
//                public void call(Object... arg0) {
//                    System.out.println("push");
//                }
//            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
//                public void call(Object... arg0) {
//                    Toast.makeText(getApplicationContext(),
//                            R.string.warning_disconnected_sockets,
//                            Toast.LENGTH_SHORT).show();
//                }
//            });
//            socket.connect();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//    }
}

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    @SuppressLint("StaticFieldLeak")
    private ImageView bmImage;

    DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}
