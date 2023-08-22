package com.tmalcher.barcodereader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.CamcorderProfile;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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
                calculateMediumSquareGradients("sdcard/cinza.jpg");
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

    private void calculateMediumSquareGradients(String filename) {
        /*Load the image for which you want to calculate mean square gradients.
        This can be done using OpenCV's Utils class to convert a bitmap image into a Mat matrix.*/
        Bitmap bitmap = BitmapFactory.decodeFile(filename);
        Mat imageMat = new Mat();
        Utils.bitmapToMat(bitmap, imageMat);

        /*Compute mean square gradients for each pixel usually involves grayscale analysis.
         So you should convert color image to grayscale image*/
        Mat grayMat = new Mat();
        Imgproc.cvtColor(imageMat, grayMat, Imgproc.COLOR_BGR2GRAY);

        /*Use Sobel filters to calculate the horizontal and vertical derivatives of
        the grayscale image*/

        Mat gradientX = new Mat();
        Mat gradientY = new Mat();

        Imgproc.Sobel(grayMat, gradientX, CvType.CV_64F, 1, 0);
        Imgproc.Sobel(grayMat, gradientY, CvType.CV_64F, 0, 1);

        /*Calculate square gradients by multiplying the horizontal and
        vertical derivatives by the derived value itself.*/

        Mat gradientXSquare = new Mat();
        Mat gradientYSquare = new Mat();

        Core.multiply(gradientX, gradientX, gradientXSquare);
        Core.multiply(gradientY, gradientY, gradientYSquare);

        /*Calculate the mean square gradient for each pixel by adding the
        horizontal and vertical square gradients and then dividing by 2.*/

        Mat msdMat = new Mat();

        Core.addWeighted(gradientXSquare, 0.5, gradientYSquare, 0.5, 0, msdMat);

        /*Convert the msdMat matrix back to an image format
        that can be displayed on the screen, such as a bitmap.*/

        Mat normalizedMSD = new Mat();
        Core.normalize(msdMat, normalizedMSD, 0, 255, Core.NORM_MINMAX);
        normalizedMSD.convertTo(normalizedMSD, CvType.CV_8U);

        Bitmap msdBitmap = Bitmap.createBitmap(normalizedMSD.cols(), normalizedMSD.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(normalizedMSD, msdBitmap);

        saveBitmapInJpg(msdBitmap);
    }

    private void saveBitmapInJpg(Bitmap bitmap) {
        Bitmap msdBitmap = bitmap;
        String filename = "msd_image.jpg";
        FileOutputStream outStream = null;

        try {
            File file = new File(Environment.getExternalStorageDirectory(), filename);
            outStream = new FileOutputStream(file);
            msdBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            MediaScannerConnection.scanFile(
                    this,
                    new String[] { file.getAbsolutePath() },
                    null,
                    null
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}