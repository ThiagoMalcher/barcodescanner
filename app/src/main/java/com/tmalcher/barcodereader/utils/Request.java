package com.tmalcher.barcodereader.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

public class Request {
    private static ArrayList<String> notGrantedPermissions = new ArrayList<>();

    public static void permissions (Context context) {
        final String[] _dangerousPermissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_DOCUMENTS,
                Manifest.permission.READ_MEDIA_IMAGES
        };

        for (final String perm : _dangerousPermissions) {
            if (ContextCompat.checkSelfPermission(context, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                notGrantedPermissions.add(perm);
            }
        }

        if (notGrantedPermissions.size() > 0) {
            String[] requestedPerms = new String[notGrantedPermissions.size()];
            ActivityCompat.requestPermissions((Activity) context,
                    notGrantedPermissions.toArray(requestedPerms),
                    0);
        }

        if(Build.VERSION.SDK_INT >= 30) {
            if(!Environment.isExternalStorageManager())
            {
                Uri uri = Uri.parse("package:" + context.getPackageName());

                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(uri);

                if (intent == null ) {
                    intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(uri);
                }
                context.startActivity(intent);
            }
        }
    }
}
