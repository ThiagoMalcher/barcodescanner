package com.tmalcher.barcodereader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.tmalcher.barcodereader.utils.Request;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    String TAG = "opencv";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //requestpermissions
        Request.permissions(getApplicationContext());

        if(OpenCVLoader.initDebug()) Log.d(TAG, "SUCCESS");
        else Log.d(TAG, "FAILURE");
    }
}