package com.microblink.reactnative;

import java.util.Arrays;
import java.lang.Runnable;

import android.os.Handler;
import android.content.res.Resources;
import android.widget.ImageButton;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.content.Context;

import android.content.Intent;
import android.os.Parcelable;
import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.annotation.TargetApi;
import android.graphics.Rect;
import com.microblink.reactnative.overlays.serialization.MultipleScanSetting;

/**
    Microblink Imports
*/
import com.microblink.entities.recognizers.Recognizer;
import com.microblink.entities.recognizers.RecognizerBundle;
import com.microblink.view.recognition.RecognizerRunnerView;
import com.microblink.view.CameraEventsListener;
import com.microblink.view.recognition.ScanResultListener;
import com.microblink.recognition.RecognitionSuccessType;

/**
    Microblink react-nativei mports
*/
import com.microblink.reactnative.recognizers.RecognizerSerializers;

public class MultipleScanDocumentActivity extends Activity {
    // To delay next scan for success display
    private Handler handler = new Handler();

    private static final int PERMISSION_CAMERA_REQUEST_CODE = 69;
    private RecognizerRunnerView mRecognizerRunnerView;
    private RecognizerBundle mRecognizerBundle;

    private Resources resources;
    private int numRecognizers;
    private int currentScanIndex;
    Parcelable[] recognizersParcelable;
    MultipleScanSetting[] overlaySettings;
    private TextView instructions;
    private TextView title;
    private AppCompatImageView overlayImage;

    private Recognizer<?, ?> getCastedRecognizer(Parcelable parcelableRecognizer) {
        return (Recognizer) parcelableRecognizer;
    }

    private View initOverlay() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
        int overlayID = resources.getIdentifier("blinkid_scanner_overlay", "layout", getPackageName());
        View overlay = inflater.inflate(overlayID, mRecognizerRunnerView, false);
        if(overlay != null) {
            int backButtonID = resources.getIdentifier("blinkid_scan_backbutton", "id", getPackageName());

            if(backButtonID != 0) {
                ImageButton backButton = overlay.findViewById(backButtonID);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });
            }

            int overlayImageID = resources.getIdentifier("blinkid_scan_overlayimage", "id", getPackageName());
            overlayImage = overlay.findViewById(overlayImageID);

            int instructionsID = resources.getIdentifier("blinkid_scan_description", "id", getPackageName());
            instructions = overlay.findViewById(instructionsID);

            int titleID = resources.getIdentifier("blinkid_scan_title", "id", getPackageName());
            title = overlay.findViewById(titleID);
        }

        return overlay;
    }

    private Runnable setOverlayContent = new Runnable() {
        @Override
        public void run() {
            if(overlayImage != null) {
                int overlayImageResourceID = resources.getIdentifier(overlaySettings[currentScanIndex].getOverlayImage(), "drawable", getPackageName());
                overlayImage.setImageResource(overlayImageResourceID);
            }

            if(instructions != null){
                instructions.setText(overlaySettings[currentScanIndex].getDescription());
            }

            if(title != null){
                title.setText(overlaySettings[currentScanIndex].getTitle());
            }
        }
    };

    private Runnable setOverlaySuccess = new Runnable() {
        @Override
        public void run() {
            if(overlayImage != null) {
                int overlayImageResourceID = resources.getIdentifier(overlaySettings[currentScanIndex].getSuccessImage(), "drawable", getPackageName());
                overlayImage.setImageResource(overlayImageResourceID);
            }

            if(instructions != null){
                instructions.setText(overlaySettings[currentScanIndex].getSuccessMessage());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resources = getResources();

        currentScanIndex = 0;

        if(savedInstanceState == null) {
            recognizersParcelable = getIntent().getExtras().getParcelableArray("recognizers");
            Parcelable[] overlaySettingsParcelables = getIntent().getExtras().getParcelableArray("overlaySettings");
            overlaySettings = Arrays.copyOf(overlaySettingsParcelables, overlaySettingsParcelables.length, MultipleScanSetting[].class);
        } else {
            recognizersParcelable = savedInstanceState.getParcelableArray("recognizers");
            Parcelable[] overlaySettingsParcelables = getIntent().getExtras().getParcelableArray("overlaySettings");
            overlaySettings = Arrays.copyOf(overlaySettingsParcelables, overlaySettingsParcelables.length, MultipleScanSetting[].class);
        }

        numRecognizers = recognizersParcelable.length - 1;

        // bundle recognizers into RecognizerBundle
        mRecognizerBundle = new RecognizerBundle(getCastedRecognizer(recognizersParcelable[currentScanIndex]));
        // create RecognizerRunnerView
        mRecognizerRunnerView = new RecognizerRunnerView(this);

        // associate RecognizerBundle with RecognizerRunnerView
        mRecognizerRunnerView.setRecognizerBundle(mRecognizerBundle);

        // scan result listener will be notified when scanning is complete
        mRecognizerRunnerView.setScanResultListener(mScanResultListener);
        // camera events listener will be notified about camera lifecycle and errors
        mRecognizerRunnerView.setCameraEventsListener(mCameraEventsListener);

        mRecognizerRunnerView.create();

        setContentView(mRecognizerRunnerView);
        View overlay = this.initOverlay();
        setOverlayContent.run();
        mRecognizerRunnerView.addChildView(overlay, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // you need to pass all activity's lifecycle methods to RecognizerRunnerView
        mRecognizerRunnerView.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // you need to pass all activity's lifecycle methods to RecognizerRunnerView
        mRecognizerRunnerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // you need to pass all activity's lifecycle methods to RecognizerRunnerView
        mRecognizerRunnerView.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // you need to pass all activity's lifecycle methods to RecognizerRunnerView
        mRecognizerRunnerView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // you need to pass all activity's lifecycle methods to RecognizerRunnerView
        mRecognizerRunnerView.destroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // you need to pass all activity's lifecycle methods to RecognizerRunnerView
        mRecognizerRunnerView.changeConfiguration(newConfig);
    }


    private final CameraEventsListener mCameraEventsListener = new CameraEventsListener() {
        @Override
        public void onCameraPreviewStarted() {
            // this method is from CameraEventsListener and will be called when camera preview starts
        }

        @Override
        public void onCameraPreviewStopped() {
            // this method is from CameraEventsListener and will be called when camera preview stops
        }

        @Override
        public void onError(Throwable exc) {
            /**
             * This method is from CameraEventsListener and will be called when
             * opening of camera resulted in exception or recognition process
             * encountered an error. The error details will be given in exc
             * parameter.
             */
        }

        @Override
        @TargetApi(23)
        public void onCameraPermissionDenied() {
            /**
             * Called in Android 6.0 and newer if camera permission is not given
             * by user. You should request permission from user to access camera.
             */
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_REQUEST_CODE);
            /**
             * Please note that user might have not given permission to use
             * camera. In that case, you have to explain to user that without
             * camera permissions scanning will not work.
             * For more information about requesting permissions at runtime, check
             * this article:
             * https://developer.android.com/training/permissions/requesting.html
             */
        }

        @Override
        public void onAutofocusFailed() {
            /**
             * This method is from CameraEventsListener will be called when camera focusing has failed.
             * Camera manager usually tries different focusing strategies and this method is called when all
             * those strategies fail to indicate that either object on which camera is being focused is too
             * close or ambient light conditions are poor.
             */
        }

        @Override
        public void onAutofocusStarted(Rect[] areas) {
            /**
             * This method is from CameraEventsListener and will be called when camera focusing has started.
             * You can utilize this method to draw focusing animation on UI.
             * Areas parameter is array of rectangles where focus is being measured.
             * It can be null on devices that do not support fine-grained camera control.
             */
        }

        @Override
        public void onAutofocusStopped(Rect[] areas) {
            /**
             * This method is from CameraEventsListener and will be called when camera focusing has stopped.
             * You can utilize this method to remove focusing animation on UI.
             * Areas parameter is array of rectangles where focus is being measured.
             * It can be null on devices that do not support fine-grained camera control.
             */
        }
    };

    private final ScanResultListener mScanResultListener = new ScanResultListener() {
        @Override
        public void onScanningDone(@NonNull RecognitionSuccessType recognitionSuccessType) {
            // TK: Handle successframe grabber recognizer data
            if(currentScanIndex < numRecognizers) {
                mRecognizerRunnerView.pauseScanning();
                Recognizer.Result result = getCastedRecognizer(recognizersParcelable[currentScanIndex]).getResult();

                if (result.getResultState() == Recognizer.Result.State.Valid) {
                    setOverlaySuccess.run();
                    currentScanIndex++;
                    handler.postDelayed(setOverlayContent, 1000);
                    mRecognizerBundle = new RecognizerBundle(getCastedRecognizer(recognizersParcelable[currentScanIndex]));
                    mRecognizerRunnerView.reconfigureRecognizers(mRecognizerBundle);
                    mRecognizerRunnerView.resumeScanning(false);
                } else {
                    mRecognizerRunnerView.resumeScanning(false);
                }
            } else if(currentScanIndex == numRecognizers) {
                mRecognizerRunnerView.pauseScanning();
                Recognizer.Result result = getCastedRecognizer(recognizersParcelable[currentScanIndex]).getResult();
                if (result.getResultState() == Recognizer.Result.State.Valid) {
                    Recognizer<?,?>[] recognizersFinal = Arrays.copyOf(recognizersParcelable, recognizersParcelable.length, Recognizer[].class);
                    mRecognizerBundle = new RecognizerBundle(recognizersFinal);
                    Intent intentResult = new Intent();
                    mRecognizerBundle.saveToIntent(intentResult);
                    setResult(RESULT_OK, intentResult);
                    finish();
                } else {
                    mRecognizerRunnerView.resumeScanning(false);
                }
            }
        }
    };

}