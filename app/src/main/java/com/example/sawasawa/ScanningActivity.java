package com.example.sawasawa;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;
import com.google.zxing.Result;

public class ScanningActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 1;
    public static final String EXTRA_RESULT = "com.example.qrcodescanner.RESULT";

    private CodeScanner codeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);

        // Find the CodeScannerView in the layout
        CodeScannerView scannerView = findViewById(R.id.scannerView);

        // Initialize the CodeScanner
        codeScanner = new CodeScanner(this, scannerView);
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                // Pass the result back to the MainActivity
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(EXTRA_RESULT, result.getText());
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                });
            }
        });


        // Set the error callback for the CodeScanner
        codeScanner.setErrorCallback(new ErrorCallback() {
            @Override
            public void onError(@NonNull final Throwable error) {
                // Handle the error here
                Log.e(TAG, "Camera initialization error: " + error.getMessage());
                Toast.makeText(ScanningActivity.this, "Camera initialization error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start the CodeScanner when the activity resumes
        codeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        // Stop the CodeScanner when the activity pauses
        codeScanner.releaseResources();
        super.onPause();
    }
}