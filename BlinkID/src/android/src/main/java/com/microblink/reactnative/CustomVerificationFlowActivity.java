package com.microblink.reactnative;

import java.util.Arrays;

import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.annotation.TargetApi;
import android.graphics.Rect;

/**
    React Imports
*/
import com.facebook.react.bridge.ReadableMap;

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

public class CustomVerificationFlowActivity extends Activity {
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 69;
    private RecognizerRunnerView mRecognizerRunnerView;
    private RecognizerBundle mRecognizerBundle;

    private int numRecognizers;
    private int currentScanIndex;
    Parcelable[] recognizersParcelable;

    private Recognizer<?, ?> getCastedRecognizer(Parcelable parcelableRecognizer) {
        return (Recognizer) parcelableRecognizer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentScanIndex = 0;

        if(savedInstanceState == null) {
            recognizersParcelable = getIntent().getExtras().getParcelableArray("recognizers");
        } else {
            recognizersParcelable = savedInstanceState.getParcelableArray("recognizers");
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
            Log.d("CUSTOM_VERIFICATION_FLOW", "Scanning done for side: " + currentScanIndex);
            // TK: Handle successframe grabber recognizer data
            if(currentScanIndex < numRecognizers) {
                mRecognizerRunnerView.pauseScanning();
                Recognizer.Result result = getCastedRecognizer(recognizersParcelable[currentScanIndex]).getResult();
                Log.d("CUSTOM_VERIFICATION_FLOW", "Current result of " + recognizersParcelable[currentScanIndex].getClass() + " is: " + result.toString());

                if (result.getResultState() == Recognizer.Result.State.Valid) {
                    currentScanIndex++;
                }
                mRecognizerBundle = new RecognizerBundle(getCastedRecognizer(recognizersParcelable[currentScanIndex]));
                mRecognizerRunnerView.reconfigureRecognizers(mRecognizerBundle);
                mRecognizerRunnerView.resumeScanning(false);
            } else if(currentScanIndex == numRecognizers) {
                mRecognizerRunnerView.pauseScanning();
                Recognizer.Result result = getCastedRecognizer(recognizersParcelable[currentScanIndex]).getResult();
                Log.d("CUSTOM_VERIFICATION_FLOW", "Current result of " + recognizersParcelable[currentScanIndex].getClass() + " is: " + result.toString());
                if (result.getResultState() == Recognizer.Result.State.Valid) {
                    Log.d("CUSTOM_VERIFICATION_FLOW", "Scanning completed");
                    Intent intentResult = new Intent();
                    intentResult.putExtra("results", recognizersParcelable);
                    setResult(RESULT_OK, intentResult);
                    finish();
                } else {
                    mRecognizerRunnerView.resumeScanning(false);
                }
            }
        }
    };

}