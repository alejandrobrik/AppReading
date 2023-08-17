package com.example.appreading;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button btnsingup;
    Button btnlogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnlogin = findViewById(R.id.btn_login);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Aquí puedes poner el código que quieras ejecutar cuando el usuario presione el enlace
                Intent intent = new Intent(getApplicationContext(), LectorActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnsingup = findViewById(R.id.btn_register);
        btnsingup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserAddActivity.class);
                startActivity(intent);
            }
        });

        TextView tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvForgotPassword.setTextColor(getResources().getColor(R.color.colorLinkPressed));
                // Aquí puedes poner el código que quieras ejecutar cuando el usuario presione el enlace
                Intent intent = new Intent(getApplicationContext(), ForgotActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tvForgotPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    tvForgotPassword.setTextColor(getResources().getColor(R.color.colorLink));
                else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    tvForgotPassword.setTextColor(getResources().getColor(R.color.colorLinkPressed));
                }
                return false;
            }
        });
    }
}