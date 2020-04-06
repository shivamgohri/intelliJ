package com.example.smart_agriculture_deloitte;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



public class UserDetailsActivity extends Activity {


    TextView user_login_name, user_login_mail, user_id;
    Button edit_user_photo;
    static ImageView user_login_photo;
    public static Uri fullPhotoUri;

    private static int RESULT_LOAD_IMAGE = 1;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdetails);




        user_login_photo = findViewById(R.id.user_login_photo);
        user_login_name = findViewById(R.id.user_login_name);
        user_login_mail = findViewById(R.id.user_login_mail);
        edit_user_photo = findViewById(R.id.edit_user_photo);
        user_id = findViewById(R.id.user_id);





        user_login_name.setText(MainActivity.user_login_name_input);
        user_login_mail.setText(MainActivity.user_login_mail_input);
        user_id.setText("   Farmer ID: (Unique generated ID)");

        edit_user_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23)
                {
                    if (checkPermission())
                    {
                        dotask();

                    } else {
                        requestPermission();
                    }
                }
                else
                {
                }

            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== RESULT_LOAD_IMAGE  && resultCode == RESULT_OK) {
            fullPhotoUri = data.getData();
            user_login_photo.setImageURI(fullPhotoUri);
            MainActivity.user_login_photo_nav.setImageURI(fullPhotoUri);
        }

    }



    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(UserDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(UserDetailsActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(UserDetailsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            ActivityCompat.requestPermissions(UserDetailsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dotask();
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                    Toast.makeText(this, "You won't be able to edit picture until you allow permission.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }



    public void dotask(){

        Intent getImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getImageIntent .setType("image/*");
        startActivityForResult(getImageIntent , RESULT_LOAD_IMAGE );

    }




}