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
            // this method is from ScanResultListener and will be called when scanning completes
            // you can obtain scanning result by calling getResult on each
            // recognizer that you bundled into RecognizerBundle.
            // for example:
            Log.d("CUSTOM_VERIFICATION_FLOW", "Scanning done for side: " + currentScanIndex);
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

            // Note that mRecognizer is stateful object and that as soon as
            // scanning either resumes or its state is reset
            // the result object within mRecognizer will be changed. If you
            // need to create a immutable copy of the result, you can do that
            // by calling clone() on it, for example:

            // Pdf417Recognizer.Result immutableCopy = result.clone();
            // Log.d("Result: ", immutableCopy.toString());

            // After this method ends, scanning will be resumed and recognition
            // state will be retained. If you want to prevent that, then
            // you should call:
            // mRecognizerRunnerView.resetRecognitionState();
            // Note that reseting recognition state will clear internal result
            // objects of all recognizers that are bundled in RecognizerBundle
            // associated with RecognizerRunnerView.

            // If you want to pause scanning to prevent receiving recognition
            // results or mutating result, you should call:

            // mRecognizerRunnerView.pauseScanning();

            // if scanning is paused at the end of this method, it is guaranteed
            // that result within mRecognizer will not be mutated, therefore you
            // can avoid creating a copy as described above

            // After scanning is paused, you will have to resume it with:

            // mRecognizerRunnerView.resumeScanning(true);

            // boolean in resumeScanning method indicates whether recognition
            // state should be automatically reset when resuming scanning - this
            // includes clearing result of mRecognizer
        }
    };

}