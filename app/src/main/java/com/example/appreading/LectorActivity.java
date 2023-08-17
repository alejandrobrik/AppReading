package com.example.appreading;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

public class LectorActivity extends AppCompatActivity {


    PDFView pdfView;
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

/*        try {
            startActivityForResult(intent, REQUEST_SPEECH_RECOGNITION);
        } catch (ActivityNotFoundException e) {
            // El reconocimiento de voz no es compatible en este dispositivo
        }*/

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
    }


}