package com.tmalcher.barcodereader;

import android.media.CamcorderProfile;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.tmalcher.barcodereader.utils.Request;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.Collections;
import java.util.List;

public class MainActivity extends CameraActivity {
    String TAG = "opencv";
    CameraBridgeViewBase mCameraBridgeViewBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //requestpermissions
        Request.permissions(this);

        mCameraBridgeViewBase = findViewById(R.id.cameraView);

        mCameraBridgeViewBase.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {

            }

            @Override
            public void onCameraViewStopped() {

            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                //capture frame and convert to matte alpha RGB
                return inputFrame.rgba();
            }
        });

        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "SUCCESS");
            mCameraBridgeViewBase.enableView();
        } else Log.d(TAG, "FAILURE");
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mCameraBridgeViewBase);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraBridgeViewBase.enableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraBridgeViewBase.disableView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraBridgeViewBase.disableView();
    }

}