package com.tmalcher.barcodereader.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Environment;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;

public class Treatments {

    public static void saveCroppedRegionAsGrayImage(Mat rgbaFrame, Rect captureRect, String filename) {
        Mat capturedRegion = new Mat(rgbaFrame, captureRect);
        Mat grayCapturedRegion = new Mat();
        Imgproc.cvtColor(capturedRegion, grayCapturedRegion, Imgproc.COLOR_RGBA2GRAY);
        Imgcodecs.imwrite(filename, grayCapturedRegion);
    }
    public static void calculateMediumSquareGradients(Context context, String filename) {
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

        saveBitmapInJpg(context, msdBitmap);
    }

    private static void saveBitmapInJpg(Context context, Bitmap bitmap) {
        Bitmap msdBitmap = bitmap;
        String filename = "output.jpg";
        FileOutputStream outStream = null;

        try {
            File file = new File(Environment.getExternalStorageDirectory(), filename);
            outStream = new FileOutputStream(file);
            msdBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            MediaScannerConnection.scanFile(
                    (Context) context,
                    new String[] { file.getAbsolutePath() },
                    null,
                    null
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
