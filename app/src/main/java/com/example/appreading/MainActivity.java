package com.example.appreading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    Button btnsingup;
    Button btnlogin;

    EditText email, password;
    private FirebaseAuth mAuth;

    FirebaseUser currentUser;

    String typeCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("user").document(uid);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        typeCurrentUser = documentSnapshot.getString("type");

                        // ... Obtener otros campos según tu estructura

                        Toast.makeText(getApplicationContext(), "El usuario es:" + typeCurrentUser, Toast.LENGTH_LONG).show();


                    } else {
                        // El documento del usuario no existe en la colección
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Error al obtener el documento
                }
            });
        } else {
            // El usuario no está autenticado
        }


       /* setTheme(R.style.Theme_AppReading_DarkActionBar);*/
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);

        email.setText("violet@gmail.com");
        password.setText("123456");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        btnlogin = findViewById(R.id.btn_login);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Aquí puedes poner el código que quieras ejecutar cuando el usuario presione el enlace

                String emailUser = email.getText().toString().trim();
                String passUser = password.getText().toString().trim();

                if (emailUser.isEmpty() && passUser.isEmpty()){
                    Toast.makeText(MainActivity.this, "Ingresar los datos", Toast.LENGTH_SHORT).show();
                }else{
                    loginUser(emailUser, passUser);
                }


/*                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
                finish();*/
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

    private void loginUser(String emailUser, String passUser) {
        mAuth.signInWithEmailAndPassword(emailUser, passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    mAuth = FirebaseAuth.getInstance();
                    currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        String uid = currentUser.getUid();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference userRef = db.collection("user").document(uid);
                        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    typeCurrentUser = documentSnapshot.getString("type");

                                    // ... Obtener otros campos según tu estructura
                                    if (typeCurrentUser.equals("teacher")) {
                                        finish();

                                        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                                        Toast.makeText(getApplicationContext(), "Bienvenido Docente", Toast.LENGTH_SHORT).show();

                                    }else {
                                        finish();
                                        startActivity(new Intent(MainActivity.this, LectorActivity.class));
                                        Toast.makeText(MainActivity.this, "Bienvenido Jovensito", Toast.LENGTH_SHORT).show();
                                    }


                                    Toast.makeText(getApplicationContext(), "El usuario que ahorita inicio es: " + typeCurrentUser, Toast.LENGTH_LONG).show();


                                } else {
                                    // El documento del usuario no existe en la colección
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error al obtener el documento
                            }
                        });
                    } else {
                        // El usuario no está autenticado
                    }
/*                    Intent intent = new Intent(getApplicationContext(), UserAddActivity.class);
                    startActivity(intent);*/






                }else{
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error al inciar sesión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
       /* updateUI(currentUser);*/
    }
}