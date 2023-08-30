package com.example.appreading.ui.course;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appreading.MainActivity;
import com.example.appreading.R;
import com.example.appreading.UserAddActivity;
import com.example.appreading.models.Course;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseAddFragment extends Fragment{

    FirebaseAuth mAuth;

    FirebaseFirestore mFirestore;

    FirebaseUser currentUser;

    EditText txtAddPillName;
    EditText txtAddPillDescription;
    Button btnPillSave;

/*    private Dosage dosage;
    private MedicalTreatment treatment;*/

    public CourseAddFragment()
    {
        super(R.layout.fragment_course_add);
    }
/*    public void addPill(Course pill)
    {
        pillService = Apis.getPillService();
        Call<Pill> call = pillService.addPill(pill);
        call.enqueue(new Callback<Pill>() {
            @Override
            public void onResponse(Call<Pill> call, Response<Pill> response) {
                if(response!=null) {
                    //Se pierde el contexto por lo queel objeto es nulo asi que no se puede mostrar el dialogo
                    //Toast.makeText(getContext(), "Successful registration.",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Pill> call, Throwable t) {
                Log.e("Error:",t.getMessage());
            }
        });
    }*/
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

/*        if (getArguments() != null){
            dosage = (Dosage) getArguments().getSerializable("dosage");
            treatment = (MedicalTreatment) getArguments().getSerializable("treatment");

        }*/

        //implementacion de firabase para instanciar el guardado de usuario
        mFirestore = FirebaseFirestore.getInstance();
        //Siguiendo la implementacion firebase

        mAuth = FirebaseAuth.getInstance();

        txtAddPillName = view.findViewById(R.id.txt_name_course);
        txtAddPillDescription = view.findViewById(R.id.txt_code_course);
        btnPillSave = view.findViewById(R.id.btnCourseSave);
        btnPillSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Course pill = new Course();
                pill.setName(txtAddPillName.getText().toString());
                pill.setCode(txtAddPillDescription.getText().toString());


                if (pill.getName().isEmpty() || pill.getCode().isEmpty()) {
                    Toast.makeText(getContext(), "Please chek the fields", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Successful registration.", Toast.LENGTH_LONG).show();
                    /*addPill(pill);*/
                    registerCourse(pill.getName(), pill.getCode());
                    Bundle bundle = new Bundle();
/*                    bundle.putSerializable("dosage", dosage);*/

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Navigation.findNavController(view).navigate(R.id.action_courseAddFragment_to_nav_course, bundle);
                }
            }
        });
    }
    private void registerCourse(String nameCourse, String codeCourse) {
        String userId = mAuth.getCurrentUser().getUid(); // Obtener UID del usuario

        // Datos para el documento en la colección "course"
        Map<String, Object> courseData = new HashMap<>();
        String courseId = mFirestore.collection("course").document().getId();
        courseData.put("id", courseId);
        courseData.put("name", nameCourse);
        courseData.put("code", codeCourse);
        courseData.put("teacher_reference", "teacher/" + userId); // Ruta al documento en la colección "teacher"
        // Agregar otros campos relevantes para el curso

        // Crear documento en la colección "course" con el UID como identificador
        mFirestore.collection("course").document().set(courseData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(), "Creado exitosamente", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error al ingresar", Toast.LENGTH_SHORT).show();
            }
        });



/*                .set(courseData)
                .addOnSuccessListener(aVoid -> {*/
                    // Documento creado con éxito en la colección "course"

                    // Datos para el documento en la colección "user"
/*                    Map<String, Object> userData = new HashMap<>();
                    if (registerAs.equals("Docente")) {
                        userData.put("type", "teacher"); // Agregar algún indicador para distinguir el tipo de usuario
                    } else {
                        userData.put("type", "Student");
                    }
                    // Agregar otros campos relevantes para el usuario
                    userData.put("name", nameUser);
                    userData.put("lastName", lastNameUser);
                    userData.put("dniUser", dniUser);*/

                    // Crear documento en la colección "user" con el UID como identificador
/*                    mFirestore.collection("user").document(userId)
                            .set(userData)
                            .addOnSuccessListener(unused -> {
                                // Documento creado con éxito en la colección "user"
                                try {

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Toast.makeText(getContext(), "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error al guardar usuario", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Manejar el fallo al crear el documento en la colección "course"
                    Toast.makeText(getContext(), "Error al guardar el curso", Toast.LENGTH_SHORT).show();
                });*/
    }

/*    private void registerCourse(String nameCourse, String codeCourse) {
        String idUser = mAuth.getCurrentUser().getUid();
        // String userId = mAuth.getCurrentUser().getUid();
        mAuth.createUserWithEmailAndPassword(emailUser, passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    String userId = mAuth.getCurrentUser().getUid(); // Obtener UID del usuario

                    // Datos para el documento en la colección "teacher"
                    Map<String, Object> teacherData = new HashMap<>();
                    teacherData.put("id", userId);
                    teacherData.put("name", nameCourse);
                    teacherData.put("codeCourse", codeCourse);


                    teacherData.put("teacher_reference", "teacher/" + userId); // Ruta al documento en la colección "user"


                    // Agregar otros campos relevantes

                    // Crear documento en la colección "teacher" con el UID como identificador
                    mFirestore.collection("course").document(userId)
                            .set(teacherData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    // Documento creado con éxito en la colección "teacher"

                                    // Datos para el documento en la colección "user"
                                    Map<String, Object> userData = new HashMap<>();
                                    if (registerAs.equals("Docente")) {
                                        userData.put("type", "teacher"); // Agregar algún indicador para distinguir el tipo de usuario
                                    }else {
                                        userData.put("type", "Student");
                                    }
                                    // Agregar otros campos relevantes
                                    userData.put("name", nameUser);
                                    userData.put("lastName", lastNameUser);
                                    userData.put("dniUser", dniUser);
                                    userData.put("email", emailUser);
                                    userData.put("password", passUser);
                                    if (imageUserDatabase!= null)
                                        userData.put("photo_url",imageUserDatabase);

                                    // Crear documento en la colección "user" con el UID como identificador
                                    mFirestore.collection("course").document(userId)
                                            .set(userData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    // Documento creado con éxito en la colección "user"
                                                    try {
                                                        subirPhoto(image_url); // Llamar a subirPhoto con el ID correcto
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                    Toast.makeText(getContext(), "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Manejar el fallo al crear el documento en la colección "teacher"
                                    Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Manejar el fallo al crear el usuario en Firebase Authentication
                    Toast.makeText(getContext(), "Error al registrar", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }*/

}