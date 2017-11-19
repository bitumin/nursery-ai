package net.bitumin.wearabletest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Metadata;
import ai.api.model.ResponseMessage;
import ai.api.model.Result;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends Activity {

    private static final int SPEECH_RECOGNIZER_REQUEST_CODE = 100;
    private static final String BOT_CLIENT_KEY = "9ecd9dc048a446a1a29a48b3235c914f";
    private static final String INTENT_NEXT_PATIENT = "next";
    private static final String INTENT_UPDATE_RECEIVED = "enviado";
    private static final String INTENT_DOCTOR_SUMMARY = "doctor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSocketListeners();

        final WatchViewStub stub = findViewById(R.id.activity_main);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                startSpeechRecognition();
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
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.speech_not_supported),
                                Toast.LENGTH_SHORT).show();
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
                        return;
                    } else if (response.getStatus().getCode() == 200) {
                        final Result result = response.getResult();
                        final Metadata metadata = result.getMetadata();
                        if (metadata == null) {
                            return;
                        }

                        String intent = metadata.getIntentName();
                        switch (intent) {
                            case INTENT_DOCTOR_SUMMARY: {
                                loadDoctorSummaryView();
                                break;
                            }
                            case INTENT_NEXT_PATIENT: {
                                List<ResponseMessage> messages = result.getFulfillment().getMessages();
                                ResponseMessage.ResponsePayload payload = (ResponseMessage.ResponsePayload) messages.get(1);
                                String specialNeeds = payload.getPayload().get("special_needs").toString();
                                String name = payload.getPayload().get("name").toString();
                                String gender = payload.getPayload().get("genre").toString();
                                String age = payload.getPayload().get("age").toString();
                                String image = payload.getPayload().get("image").toString();

                                loadPatientInformationView(name, age, gender, specialNeeds, image);
                                break;
                            }
                            case INTENT_UPDATE_RECEIVED: {
                                loadUpdateReceivedView();
                                break;
                            }
                        }
                    } else {
                        response.getStatus().getErrorDetails();
                    }
                }
            }.execute(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadUpdateReceivedView() {
        Intent intent = new Intent(MainActivity.this, UpdateSuccess.class);
        startActivity(intent);
    }

    private void loadPatientInformationView(String name, String age, String gender, String specialNeeds, String image) {
        Intent intent = new Intent(MainActivity.this, PatientProfileInformation.class);

        intent.putExtra("patient_name", name);
        intent.putExtra("patient_age", age);
        intent.putExtra("patient_gender", gender);
        intent.putExtra("patient_needs", specialNeeds);
        intent.putExtra("patient_image", image);

        startActivity(intent);
    }

    private void loadDoctorSummaryView() {
        Intent intent = new Intent(MainActivity.this, DoctorSummary.class);
        startActivity(intent);
    }

    public void loadBlueNotificationView() {
        Intent intent = new Intent(MainActivity.this, FifteenActivity.class);
        startActivity(intent);
    }

    public void loadYellowNotificationView() {
        Intent intent = new Intent(MainActivity.this, SevenActivity.class);
        startActivity(intent);
    }

    private void initSocketListeners() {
        MyApp myApp = (MyApp) getApplication();
        Socket socket = myApp.getSocket();
        socket.off("request-nurse-push").on("request-nurse-push", new Emitter.Listener() {
            public void call(Object... arg0) {
                loadBlueNotificationView();
            }
        }).off("request-nurse-end-push").on("request-nurse-end-push", new Emitter.Listener() {
            public void call(Object... arg0) {
                loadYellowNotificationView();
            }
        });
    }
}
