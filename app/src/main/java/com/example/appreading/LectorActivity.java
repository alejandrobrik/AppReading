package com.example.appreading;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;


import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class LectorActivity extends AppCompatActivity {

    private EditText editText;
    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;
    PDFView pdfView;
    Button selectPdfButton;
    Button startRecordingButton;
    Button stopRecordingButton;

    private static final int REQUEST_PDF_PICK = 1;
    private static final int REQUEST_SPEECH_RECOGNITION = 2;


    private Handler handler = new Handler();
    //private static final int MAX_LISTENING_TIME = 30000; // Tiempo máximo en milisegundos (30 segundos)

    private static final int MAX_LISTENING_TIME = 10000; // 10 segundos

    private long lastSpeechTime = 0;


    private boolean isListening = false;


    ProgressBar progressBar;
    int counter = 0;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    Button closedSessionStudent;

    int contador = 0;

    Button btnSendLecture;

    String percentLecture;
    String namePdf;
    String idStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector);



        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("student").document(uid);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        idStudent = documentSnapshot.getString("id");

                        // ... Obtener otros campos según tu estructura

                       /* Toast.makeText(getApplicationContext(), "El Jovensito tiene este uid: " +idStudent , Toast.LENGTH_LONG).show();*/



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




        closedSessionStudent = findViewById(R.id.btn_closed_student_sesion);

        closedSessionStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });



        pdfView = findViewById(R.id.pdfView);

        selectPdfButton = findViewById(R.id.selectPdfButton);
        selectPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });





        editText = findViewById(R.id.editText);
        Button startButton = findViewById(R.id.btn);

        stopRecordingButton = findViewById(R.id.stopRecordingButton);
        stopRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechRecognizer.stopListening();
            }
        });


        btnSendLecture = findViewById(R.id.btnSendLecture);
        btnSendLecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (namePdf!= null && percentLecture != null ){
                    if (currentUser != null) {
                        String uid = currentUser.getUid();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference userRef = db.collection("student").document(uid);
                        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    idStudent = documentSnapshot.getString("id");

                                    // ... Obtener otros campos según tu estructura

                                    // Crear un mapa con los datos de la lectura
                                    Map<String, Object> lectureData = new HashMap<>();
                                   /* lectureData.put("nameStudent", currentUser.getDisplayName()); */// Opcional, depende de cómo tengas configurado Firebase Auth
                                    lectureData.put("namePDF", namePdf);
                                    lectureData.put("percentLecture", percentLecture);
                                    lectureData.put("id_student", idStudent);

                                    Timestamp timestamp = Timestamp.now();
                                    lectureData.put("dateLecture", timestamp);

                                    // Acceder a la colección "lectures" y agregar un nuevo documento
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("lecture")
                                            .add(lectureData)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    // Éxito al agregar la lectura
                                                    Toast.makeText(getApplicationContext(), "Lectura enviada al docente", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Error al agregar la lectura
                                                    Toast.makeText(getApplicationContext(), "Error al enviar la lectura", Toast.LENGTH_SHORT).show();
                                                }
                                            });



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



                   /* Toast.makeText(getApplicationContext(), "Enviaste al docente tu lectura", Toast.LENGTH_SHORT).show();*/
                }
                else {
                    Toast.makeText(getApplicationContext(), "Aun no ha leido", Toast.LENGTH_SHORT).show();
                }

            }
        });


        checkPermission();

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {}

            @Override
            public void onBeginningOfSpeech() {
/*                isListening = true;
                handler.postDelayed(stopListeningRunnable, MAX_LISTENING_TIME);*/

                isListening = true;
                lastSpeechTime = System.currentTimeMillis();
                handler.postDelayed(stopListeningRunnable, MAX_LISTENING_TIME);

            }


            @Override
            public void onRmsChanged(float rmsdB) {
                if (isListening) {
                    lastSpeechTime = System.currentTimeMillis();
                }
            }

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {
                isListening = false;
                handler.removeCallbacksAndMessages(null); // Elimina cualquier Runnable pendiente

                long currentTime = System.currentTimeMillis();
                if (currentTime - lastSpeechTime >= MAX_LISTENING_TIME) {
                    speechRecognizer.stopListening();
                }
            }

            @Override
            public void onError(int error) {}

/*            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String recognizedText = matches.get(0);
                    editText.setText(recognizedText);
                }
            }*/

            public void onResults(Bundle results) {

                handler.removeCallbacks(stopListeningRunnable);
                // Resultados del reconocimiento de voz
                ArrayList<String> voiceResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (voiceResults != null && !voiceResults.isEmpty()) {
                    processVoiceResults(voiceResults);
                    String recognizedText = voiceResults.get(0);
/*                    editText.setText(recognizedText);
                    compareTextWithPDF(pdfText, recognizedText);*/
                }


            }
            private void processVoiceResults(ArrayList<String> voiceResults) {
                if (voiceResults != null && !voiceResults.isEmpty()) {
                    String recognizedText = voiceResults.get(0);
                    editText.setText(recognizedText);
                    compareTextWithPDF(pdfText, recognizedText);
                }
            }



            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                speechRecognizer.startListening(recognizerIntent);

                isListening = true;
                handler.postDelayed(stopListeningRunnable, MAX_LISTENING_TIME);
            }
        });


    }

/*    private Runnable stopListeningRunnable = new Runnable() {
        @Override
        public void run() {
            if (isListening) {
                speechRecognizer.stopListening();
                isListening = false;
            }
        }
    };*/

    private Runnable stopListeningRunnable = new Runnable() {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            if (isListening && (currentTime - lastSpeechTime >= MAX_LISTENING_TIME)) {
                speechRecognizer.stopListening();
                isListening = false;
            }
            handler.postDelayed(this, 1000); // Programa la ejecución cada segundo
        }
    };

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
    }



    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, REQUEST_PDF_PICK);
    }


    private String pdfText = ""; // Aquí almacenaremos el texto extraído del PDF

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PDF_PICK && resultCode == RESULT_OK && data != null) {
            Uri pdfUri = data.getData();
            // Carga el PDF en el visor
            pdfView.fromUri(pdfUri).load();

            // Obtiene el nombre del archivo de PDF seleccionado
            String pdfFileName = getFileName(pdfUri);

            namePdf = pdfFileName;

            // Extrae el texto del PDF y guárdalo en pdfText
            extractTextFromPDF(pdfUri);
        } else if (requestCode == REQUEST_SPEECH_RECOGNITION && resultCode == RESULT_OK && data != null) {
            speechRecognizer.stopListening();
            // Procesa el texto reconocido en el método onResults
            ArrayList<String> voiceResults = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (voiceResults != null && !voiceResults.isEmpty()) {
                String recognizedText = voiceResults.get(0);
                String recognizedTextv2 = editText.getText().toString();
                compareTextWithPDF(pdfText, recognizedTextv2);
            }

        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (displayNameIndex >= 0) {
                        result = cursor.getString(displayNameIndex);
                    } else {
                        // Si no se encontró la columna DISPLAY_NAME, intenta obtener el nombre desde el URI
                        result = uri.getLastPathSegment();
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }



    private void extractTextFromPDF(Uri pdfUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(pdfUri);
            PdfReader reader = new PdfReader(inputStream);
            int numPages = reader.getNumberOfPages();

            StringBuilder textBuilder = new StringBuilder();
            for (int pageNum = 1; pageNum <= numPages; pageNum++) {
                textBuilder.append(PdfTextExtractor.getTextFromPage(reader, pageNum));
            }

            pdfText = textBuilder.toString();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/*    private void compareTextWithPDF(String pdfText, String recognizedText) {
        // Realiza la comparación entre el texto del PDF y el texto reconocido
        if (pdfText.contains(recognizedText)) {
            // El texto reconocido coincide con el contenido del PDF
            Toast.makeText(getApplicationContext(), "Lectura de alto nivel.", Toast.LENGTH_LONG).show();
        } else {
            // El texto reconocido no coincide con el contenido del PDF
            Toast.makeText(getApplicationContext(), "Lectura de bajo nivel.", Toast.LENGTH_LONG).show();
        }
    }*/

    private void compareTextWithPDF(String pdfText, String recognizedText) {
        // Divide el texto en palabras
        String[] pdfWords = pdfText.toLowerCase().split("\\s+");
        String[] recognizedWords = recognizedText.toLowerCase().split("\\s+");

        // Calcula la cantidad de palabras comunes
        int commonWords = 0;
        for (String word : recognizedWords) {
            if (Arrays.asList(pdfWords).contains(word)) {
                commonWords++;
            }
        }

        // Calcula el porcentaje de similitud en función de las palabras comunes
        int similarityPercentage = (int) ((double) commonWords / recognizedWords.length * 100);

        // Muestra el porcentaje de similitud en un Toast
        String message = "Porcentaje de lectura: " + similarityPercentage + "%";
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        percentLecture = ""+similarityPercentage;

    }

    @Override
    public void onBackPressed() {
        if (contador == 0) {
            Toast.makeText(getApplicationContext(), "Presiona de nuevo para salir", Toast.LENGTH_SHORT).show();
            contador++;

            new CountDownTimer(3000, 1000) {
                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    contador = 0;
                }
            }.start();
        } else {
            super.onBackPressed();
        }
    }






/*    PDFView pdfView;
    Button selectPdfButton;
    Button startRecordingButton;
    Button stopRecordingButton;

    private static final int REQUEST_PDF_PICK = 1;
    private static final int REQUEST_SPEECH_RECOGNITION = 2;

    private SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector);

        pdfView = findViewById(R.id.pdfView);

        selectPdfButton = findViewById(R.id.selectPdfButton);
        selectPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        startRecordingButton = findViewById(R.id.startRecordingButton);
        startRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });

        stopRecordingButton = findViewById(R.id.stopRecordingButton);
        stopRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });

        initializeSpeechRecognition();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, REQUEST_PDF_PICK);
    }

    private Handler handler = new Handler();
    private static final int MAX_LISTENING_TIME = 30000; // Tiempo máximo en milisegundos (30 segundos)

    private void startRecording() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla para grabar...");

*//*        try {
            startActivityForResult(intent, REQUEST_SPEECH_RECOGNITION);
        } catch (ActivityNotFoundException e) {
            // El reconocimiento de voz no es compatible en este dispositivo
        }*//*

        try {
            startActivityForResult(intent, REQUEST_SPEECH_RECOGNITION);

            // Detener la escucha después de MAX_LISTENING_TIME
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopRecording();
                }
            }, MAX_LISTENING_TIME);
        } catch (ActivityNotFoundException e) {
            // El reconocimiento de voz no es compatible en este dispositivo
        }
    }

    private void stopRecording() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
        }
    }

    private void initializeSpeechRecognition() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                // El reconocimiento está listo para recibir voz
            }

            @Override
            public void onBeginningOfSpeech() {
                // Comenzó la grabación de voz
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // Cambios en el nivel de sonido
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // Datos de audio recibidos
            }

            @Override
            public void onEndOfSpeech() {
                // Finalizó la grabación de voz
            }

            @Override
            public void onError(int error) {
                // Ocurrió un error en el reconocimiento de voz
            }

            @Override
            public void onResults(Bundle results) {
                // Resultados del reconocimiento de voz
                ArrayList<String> voiceResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (voiceResults != null && !voiceResults.isEmpty()) {
                    String recognizedText = voiceResults.get(0);
                    // Aquí puedes comparar el texto reconocido con el contenido del PDF
                    // Por ejemplo: compareTextWithPDF(pdfText, recognizedText);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // Resultados parciales del reconocimiento de voz
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // Eventos del reconocimiento de voz
            }
        });
    }

    private String pdfText = ""; // Aquí almacenaremos el texto extraído del PDF

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PDF_PICK && resultCode == RESULT_OK && data != null) {
            Uri pdfUri = data.getData();
            // Carga el PDF en el visor
            pdfView.fromUri(pdfUri).load();

            // Extrae el texto del PDF y guárdalo en pdfText
            extractTextFromPDF(pdfUri);
        } else if (requestCode == REQUEST_SPEECH_RECOGNITION && resultCode == RESULT_OK && data != null) {
            speechRecognizer.stopListening();
            // Procesa el texto reconocido en el método onResults
            ArrayList<String> voiceResults = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (voiceResults != null && !voiceResults.isEmpty()) {
                String recognizedText = voiceResults.get(0);
                compareTextWithPDF(pdfText, recognizedText);
            }
        }
    }

    private void extractTextFromPDF(Uri pdfUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(pdfUri);
            PdfReader reader = new PdfReader(inputStream);
            int numPages = reader.getNumberOfPages();

            StringBuilder textBuilder = new StringBuilder();
            for (int pageNum = 1; pageNum <= numPages; pageNum++) {
                textBuilder.append(PdfTextExtractor.getTextFromPage(reader, pageNum));
            }

            pdfText = textBuilder.toString();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compareTextWithPDF(String pdfText, String recognizedText) {
        // Realiza la comparación entre el texto del PDF y el texto reconocido
        if (pdfText.contains(recognizedText)) {
            // El texto reconocido coincide con el contenido del PDF
            Toast.makeText(getApplicationContext(), "Lectura de alto nivel.", Toast.LENGTH_LONG).show();
        } else {
            // El texto reconocido no coincide con el contenido del PDF
            Toast.makeText(getApplicationContext(), "Lectura de bajo nivel.", Toast.LENGTH_LONG).show();
        }
    }*/


}