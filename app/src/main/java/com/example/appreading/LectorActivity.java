package com.example.appreading;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;


import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
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







        editText = findViewById(R.id.editText);
        Button startButton = findViewById(R.id.btn);

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
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {}

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
                // Resultados del reconocimiento de voz
                ArrayList<String> voiceResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
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
            }
        });
    }

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

    private Handler handler = new Handler();
    private static final int MAX_LISTENING_TIME = 30000; // Tiempo máximo en milisegundos (30 segundos)

    private void startRecording() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla para grabar...");

        try {
            startActivityForResult(intent, REQUEST_SPEECH_RECOGNITION);
        } catch (ActivityNotFoundException e) {
            // El reconocimiento de voz no es compatible en este dispositivo
        }

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

/*            @Override
            public void onResults(Bundle results) {
                // Resultados del reconocimiento de voz
                ArrayList<String> voiceResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (voiceResults != null && !voiceResults.isEmpty()) {
                    String recognizedText = voiceResults.get(0);
                    // Aquí puedes comparar el texto reconocido con el contenido del PDF
                    // Por ejemplo: compareTextWithPDF(pdfText, recognizedText);
                }
            }*/

            public void onResults(Bundle results) {
                // Resultados del reconocimiento de voz
                ArrayList<String> voiceResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (voiceResults != null && !voiceResults.isEmpty()) {
                    String recognizedText = voiceResults.get(0);
                    compareTextWithPDF(pdfText, recognizedText);
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
                String recognizedTextv2 = editText.getText().toString();
                compareTextWithPDF(pdfText, recognizedTextv2);
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
        String message = "Porcentaje de similitud: " + similarityPercentage + "%";
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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