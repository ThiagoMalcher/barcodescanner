package com.tmalcher.barcodereader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.CamcorderProfile;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.tmalcher.barcodereader.image.Treatments;
import com.tmalcher.barcodereader.utils.Request;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.engine.OpenCVEngineInterface;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.osgi.OpenCVInterface;

import java.io.File;
import java.io.FileOutputStream;
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
                Mat rgbaFrame = inputFrame.rgba();
                // Define the coordinates of the capture rectangle,
                // in the future add to receive by parameter
                Point topLeft = new Point(100, 90);
                Point bottomRight = new Point(200, 600);
                // Set the color of the rectangle (green, in BGR format)
                Scalar rectColor = new Scalar(0, 255, 0);
                Rect captureRect = new Rect(topLeft, bottomRight);
                // Draw the rectangle on the image
                Imgproc.rectangle(rgbaFrame, topLeft, bottomRight, rectColor, 2);

                Treatments.saveCroppedRegionAsGrayImage(rgbaFrame, captureRect, "sdcard/captured_region.jpg");
                Treatments.calculateMediumSquareGradients(getApplicationContext(), "sdcard/captured_region.jpg");
                return rgbaFrame;
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