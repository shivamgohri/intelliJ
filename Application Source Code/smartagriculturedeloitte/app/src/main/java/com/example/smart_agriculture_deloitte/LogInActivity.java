package com.example.smart_agriculture_deloitte;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class LogInActivity extends AppCompatActivity {



    EditText name_login, mail_login, password_login;
    Button button_login;

    MainActivity mainActivity;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_login);




        name_login = findViewById(R.id.name_login);
        mail_login = findViewById(R.id.mail_login);
        password_login = findViewById(R.id.password_login);


        button_login = findViewById(R.id.button_login);




        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mainActivity.user_login_name_input = name_login.getText().toString();
                mainActivity.user_login_mail_input = mail_login.getText().toString();
                mainActivity.user_login_password_input = password_login.getText().toString();


                if( (mainActivity.user_login_name_input.isEmpty()) || (mainActivity.user_login_mail_input.isEmpty()) || (mainActivity.user_login_password_input.isEmpty()) ){
                    Toast.makeText(getApplicationContext(), "Fill all credentials!", Toast.LENGTH_LONG).show();
                }
                else{

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);

                }

            }
        });





    }
}
