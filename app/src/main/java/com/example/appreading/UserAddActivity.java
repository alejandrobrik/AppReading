package com.example.appreading;

import static android.content.ContentValues.TAG;

import static com.example.appreading.ui.course.CourseListFragment.deleteCache;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.appreading.models.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAddActivity extends AppCompatActivity {



    EditText txtname;
    EditText txtDni;
    EditText txtLastName;


    EditText txtemail;
    EditText txtpassword;
    EditText txtRepeatPassword;
    Boolean validaEmail = false;

    Spinner spinerGenderCarer;
    Spinner spinnerRegisterAs;
    String genero;
    String registerAs;
    EditText txtbirthdateCarer;


    Button save;
    Button btnClean;

    //Implementacion firebase
    ShapeableImageView fotoCarer;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    FirebaseFirestore mfirestore;

    StorageReference storageReference;
    String storage_path = "user/*";

    private static final int COD_SEL_STORAGE = 200;
    private static final int COD_SEL_IMAGE = 300;

    private final String SAMPLE_CROPPED_IMG_NAME = "SampleCropImg";
    private int contador = 0;

    Button btnPhotoSelect;
    private Uri image_url;
    String imageUserDatabase;
    String photo = "photoCarer";
    String idd;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add);

/*        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }*/

        //implementacion de firabase para instanciar el guardado de usuario
        mFirestore = FirebaseFirestore.getInstance();
        //Siguiendo la implementacion firebase

        mfirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        btnPhotoSelect = findViewById(R.id.btn_select_electoral_document_photo);
        fotoCarer = findViewById(R.id.imgCarerPhoto);


        //Para la foto luego
        btnPhotoSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //         getCacheDir().delete();
                deleteCache(getApplication());
                uploadPhoto();
            }
        });


        txtname = findViewById(R.id.txt_name);

/*        txtbirthdateCarer = findViewById(R.id.txtBirthDateCarer);
        txtbirthdateCarer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });*/


/*        spinerGenderCarer = findViewById(R.id.spinerGenderCarer);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.combo_gender, R.layout.spiner_item_patient);

        spinerGenderCarer.setAdapter(adapter);
        spinerGenderCarer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                genero = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

                spinnerRegisterAs  =  findViewById(R.id.spinerRegisterAs);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.combo_register_as, R.layout.spiner_item_patient);

        spinnerRegisterAs.setAdapter(adapter2);
        spinnerRegisterAs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                registerAs = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        txtLastName = findViewById(R.id.txt_last_name);
        txtDni = findViewById(R.id.txt_dni);
        txtemail = findViewById(R.id.txt_email);
        txtemail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && txtemail.getText().toString() != null) {

                    validaEmail = validaEmail(txtemail);
                    if (validaEmail) {


                    } else {
                        txtemail.setError("Email not valid");
                    }
                    //      Toast.makeText(getApplicationContext(), txtemail.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        txtpassword = findViewById(R.id.txt_password);
        txtRepeatPassword = findViewById(R.id.txt_repeatPassword);

        /*        Button btnBack = findViewById(R.id.btnBack);*//*
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                finish();
                startActivity(intent);

            }
        });*/

        save = findViewById(R.id.btn_register);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validaEmail = validaEmail(txtemail);
                Teacher t = new Teacher();
                t.setName(txtname.getText().toString());
                t.setLastName(txtLastName.getText().toString());
                t.setEmail(txtemail.getText().toString());
                t.setPassword(txtpassword.getText().toString());
                t.setDni(txtDni.getText().toString());
/*                c.setGender(genero);
                c.setBirthDate(txtbirthdateCarer.getText().toString());*/
                //Inserta imagen en el carer
/*                if (imageCarerDatabase!=null)
                    c.setUrlImage(imageCarerDatabase);

                c.setState((true));*/

                String pass = txtpassword.getText().toString();
                String repeatPass = txtRepeatPassword.getText().toString();
                String name = txtname.getText().toString();
                String lastName = txtLastName.getText().toString();
                String phone = txtDni.getText().toString();
                String email = txtemail.getText().toString();
//                String birthDate = txtbirthdateCarer.getText().toString();


                if (pass.equals(repeatPass) && !(pass.isEmpty() || repeatPass.isEmpty() || name.isEmpty() || lastName.isEmpty() || phone.isEmpty() || email.isEmpty() ) && validaEmail) {
                    //Encriptar
//                    encrypt = new EncryptHelper();
                    try {
//                        encryptValue = encrypt.encriptar(c.getPassword(),encrypt.apiKey);
//                        c.setPassword(encryptValue);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    /* addCarer(c);*/
                    registerUser(t.getName(), t.getLastName(), t.getDni(), t.getEmail(), t.getPassword());
/*                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);*/
                } else if (pass.isEmpty() || repeatPass.isEmpty() || name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                    Toast.makeText(UserAddActivity.this, "Error, check the fields. ", Toast.LENGTH_SHORT).show();
                } else if (!validaEmail) {
                    Toast.makeText(UserAddActivity.this, "Error, check the email entered. ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserAddActivity.this, "Error, check the password. ", Toast.LENGTH_SHORT).show();
                }

            }
        });

/*        btnClean = findViewById(R.id.btnClean);

        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCache(getApplication());
                txtname.setText("");
                txtphoneNumber.setText("");
                txtemail.setText("");
                txtpassword.setText("");
                txtRepeatPassword.setText("");

                Glide.with(CarerAddActivity.this)
                        .load(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .into(fotoCarer);
            }
        });

*/
    }

    private void registerUser(String nameUser, String lastNameUser, String dniUser,  String emailUser, String passUser) {

       // String userId = mAuth.getCurrentUser().getUid();
        mAuth.createUserWithEmailAndPassword(emailUser, passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    String userId = mAuth.getCurrentUser().getUid(); // Obtener UID del usuario

                    // Datos para el documento en la colección "teacher"
                    Map<String, Object> teacherData = new HashMap<>();
                    teacherData.put("id", userId);
                    teacherData.put("name", nameUser);
                    teacherData.put("lastName", lastNameUser);
                    teacherData.put("dniUser", dniUser);
                    teacherData.put("email", emailUser);
                    teacherData.put("password", passUser);

                    teacherData.put("user_reference", "user/" + userId); // Ruta al documento en la colección "user"


                    // Agregar otros campos relevantes
                    String typeUser;
                    if (registerAs.equals("Docente")){
                         typeUser = "teacher";
                    }else {
                        typeUser = "student";
                    }
                    // Crear documento en la colección "teacher" con el UID como identificador
                    mFirestore.collection(typeUser).document(userId)
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
                                    mFirestore.collection("user").document(userId)
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
                                                    finish();
                                                    startActivity(new Intent(UserAddActivity.this, MainActivity.class));
                                                    Toast.makeText(UserAddActivity.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(UserAddActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Manejar el fallo al crear el documento en la colección "teacher"
                                    Toast.makeText(UserAddActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Manejar el fallo al crear el usuario en Firebase Authentication
                    Toast.makeText(UserAddActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                }
            }
        });



      /*        String id = mAuth.getCurrentUser().getUid();
                Map<String, Object> map = new HashMap<>();
                map.put("id", id);
                map.put("name", nameUser);
                map.put("lastName", lastNameUser);
                map.put("dniUser", dniUser);
                map.put("email", emailUser);
                map.put("password", passUser);
                if (imageUserDatabase!= null)
                    map.put("photo_url",imageUserDatabase);



               mFirestore.collection("user").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                    public void onSuccess(Void unused) {
                        idd = id; // Assign the value of the newly created user's ID to idd
                        // Now you can call subirPhoto() with the correct idd value
                        try {
                            subirPhoto(image_url);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        finish();
                        startActivity(new Intent(UserAddActivity.this, MainActivity.class));
                        Toast.makeText(UserAddActivity.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserAddActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserAddActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        });*/
    }




    private void uploadPhoto() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");

        startActivityForResult(i, COD_SEL_IMAGE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK){
            if (requestCode == COD_SEL_IMAGE){
                image_url = data.getData();
                if (image_url!=null){
                    starCrop(image_url);
                }
                try {
                     //subirPhoto(image_url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){

            Uri imageUriResultCrop = UCrop.getOutput(data);
            if (imageUriResultCrop!= null){
                try {
                    subirPhoto(imageUriResultCrop);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //        fotoCarer.setImageURI(imageUriResultCrop);
                Glide.with(UserAddActivity.this)
                        .load(imageUriResultCrop)
                        .error(R.drawable.ic_user)
                        .into(fotoCarer);

            }

        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
    //Metodo para recortar la foto precisamente
    private  void starCrop(@NonNull Uri uri) {

        Date date = (Calendar.getInstance().getTime());
        String destinationFileName = SAMPLE_CROPPED_IMG_NAME+ date +contador;
        contador++;
        destinationFileName +=".jpg";
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));

        uCrop.withAspectRatio(1, 1);
//        uCrop.withAspectRatio(3, 4);
//
//        uCrop.useSourceImageAspectRatio();
//
//        uCrop.withAspectRatio(2, 3;
//        uCrop.withAspectRatio(16, 9);
        uCrop.withMaxResultSize(450, 450);

        uCrop.withOptions(getCropOptions());

        uCrop.start(UserAddActivity.this);

    }

    public  UCrop.Options getCropOptions(){
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(70);

        //CompressType
        //   options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        //   options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

        //UI
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);

        //colors
        options.setStatusBarColor(getResources().getColor(com.google.android.material.R.color.design_default_color_primary_dark));
        options.setToolbarColor(getResources().getColor(com.google.android.material.R.color.design_default_color_primary));

        options.setToolbarTitle("Recortar imagen");
        return  options;

    }


    private void subirPhoto(Uri image_url) throws Exception {
        //     progressDialog.setMessage("Actualizando foto");
        //     progressDialog.show();
        Random numAleatorio1 = new Random();
        int aleatorio = numAleatorio1.nextInt(3000- 25+1) +25;
        //     idd = aleatorio + "";

        this.image_url = image_url;
        /*getCarer(image_url);*/
        try {

            Toast toast = Toast.makeText(getApplicationContext(), "Cargando foto", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP,0,200);
            toast.show();
            Glide.with(UserAddActivity.this)
                    .load(image_url)
                    .error(R.drawable.ic_user)
                    .into(fotoCarer);

        }catch (Exception e){
            Log.v("Error", "e: " + e);
        }
        String rute_storage_photo = storage_path + "" + photo + "" + mAuth.getUid() +""+ idd;
        StorageReference reference = storageReference.child(rute_storage_photo);
        reference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                if (uriTask.isSuccessful()){
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String download_uri = uri.toString();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("user", download_uri);
                            imageUserDatabase = download_uri;
                       //     mfirestore.collection("user").document(idd).update(map);
                            Toast.makeText(UserAddActivity.this, "Foto actualizada", Toast.LENGTH_SHORT).show();
                         //   progressDialog.dismiss();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserAddActivity.this, "Error al cargar foto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private  boolean validaEmail(EditText email){

        String emailInput = email.getText().toString();
        if (!emailInput.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            // Toast.makeText(this, "Email valitated succesfully", Toast.LENGTH_SHORT).show();
            return true;
        }else{
            // Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

/*    public void getCarer(Uri image_url) throws Exception {

        Call<List<Carer>> doctorList = Apis.getCarerService().getCarer();

        doctorList.enqueue(new Callback<List<Carer>>() {
            @Override
            public void onResponse(Call<List<Carer>> call, Response<List<Carer>> response) {
                if(response.isSuccessful()){
                    List <Carer>  carers = response.body();
                    int lastIdx = carers.size() - 1;
                    Carer lastElment = carers.get(lastIdx);
                    idd = "" +lastElment.getId()+1;
                    String rute_storage_photo = storage_path + "" + photo + "" + mAuth.getUid() +""+ idd;
                    StorageReference reference = storageReference.child(rute_storage_photo);
                    reference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            if (uriTask.isSuccessful()){
                                uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String download_uri = uri.toString();
                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("carer", download_uri);
                                        imageCarerDatabase = download_uri;
                                        mfirestore.collection("carer").document(idd).update(map);
                                        //        Toast.makeText(CarerAddActivity.this, "Foto actualizada", Toast.LENGTH_SHORT).show();
                                        //   progressDialog.dismiss();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CarerAddActivity.this, "Error al cargar foto", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<List<Carer>> call, Throwable t) {
                Log.e("faliure", t.getLocalizedMessage());
            }
        });
    }*/

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // do your stuff
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "signInAnonymously:FAILURE", exception);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(intent);
        super.onBackPressed();

    }


}