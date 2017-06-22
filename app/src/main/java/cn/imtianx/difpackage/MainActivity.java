package cn.imtianx.difpackage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import cn.imtianx.difpackage.view.CircleImageView;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private CircleImageView mCivPhoto;
    private Button mBtnTakePhoto;
    private boolean isPermissionGranted = false;
    private String tempCameraFilePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCivPhoto = (CircleImageView) findViewById(R.id.civ_head);
        mBtnTakePhoto = (Button) findViewById(R.id.btn_take_photo);


        //6.0后动态申请权限
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) ||
                    !(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1996);
            }else {
                isPermissionGranted = true;
            }
        }else
        {
            isPermissionGranted = true;
        }


        mBtnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPermissionGranted)
                {
                    takePhoto();

                }else {
                    Toast.makeText(MainActivity.this,"无权限",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void takePhoto(){
         tempCameraFilePath = Environment.getExternalStorageDirectory()
                + "/TestCamera/" + System.currentTimeMillis() + ".jpg";
        File file = new File(tempCameraFilePath);
        if (!file.exists())
            file.getParentFile().mkdirs();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri mUriPhoto= Uri.fromFile(new File(tempCameraFilePath));

        //适配7.0，使用 content uri 替换 File uri
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            mUriPhoto = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider", file);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriPhoto);
        startActivityForResult(intent, 2017);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (null== permissions||permissions.length == 0)
        {
            isPermissionGranted = true;
            return;
        }
        int grantNum = 0;
        if (grantResults.length>0)
        {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED)
                {
                    grantNum++;
                }
            }
            if (grantNum == grantResults.length)
            {
                isPermissionGranted = true;
            }else
            {
                isPermissionGranted = false;
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2017&&resultCode == RESULT_OK)
        {
            Bitmap bitmap = BitmapFactory.decodeFile(tempCameraFilePath);
            mCivPhoto.setImageBitmap(bitmap);
        }
    }
}
