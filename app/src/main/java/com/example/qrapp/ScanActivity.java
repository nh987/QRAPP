package com.example.qrapp;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.common.hash.Hashing;
import com.google.mlkit.vision.common.InputImage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;


import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ScanActivity extends AppCompatActivity implements ImageAnalysis.Analyzer {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageButton BACK_ARROW;
    private PreviewView previewView;
    private ImageAnalysis imageAnalysis;
    boolean scannedCode = false; // init false, set true when scanned and close activity
    long score;
    String hashed;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        previewView = findViewById(R.id.ScanActivity_preview_view);
        BACK_ARROW = findViewById(R.id.ScanActivity_back_button);

        //go back to main activity
        BACK_ARROW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Get camera permission
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 0);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            }, getExecutor());

    }
    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder().build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(getExecutor(),this);
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis);
    }

    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        Log.d("ScanActivity_analyze", "analyze: got the frame at: " + imageProxy.getImageInfo().getTimestamp());
//        boolean scannedCode = false;
//        long score;
//        String hashed;
        @SuppressLint("UnsafeOptInUsageError") Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            BarcodeScannerOptions options =
                    new BarcodeScannerOptions.Builder()
                            .setBarcodeFormats(
                                    Barcode.FORMAT_ALL_FORMATS)
                            .build();
            BarcodeScanner scanner = BarcodeScanning.getClient(options);
            Task<List<Barcode>> result = scanner.process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {

                            for (Barcode barcode : barcodes) {
                                Rect bounds = barcode.getBoundingBox();
                                Point[] corners = barcode.getCornerPoints();
                                String rawValue = barcode.getRawValue(); // this is what will be SHA-256 hashed
                                byte[] rawData = barcode.getRawBytes();
                                int valueType = barcode.getValueType();

                                Log.d("barcode", "extractBarCodeInfo: "+rawValue+", extractBarCodeRawBytes: "+rawData+", extractBarCodeType: "+valueType);
                                // TODO: PASS SHA-256 HASHED SCORE, NAME, VISUAL INTO RESULTS VIEW

                                hashed = Hashing.sha256()
                                        .hashString(rawValue, StandardCharsets.UTF_8)
                                        .toString();
                                Log.d("encoded", "sha256 hex string: "+hashed);
                                score = score(hashed);
                                scannedCode = true;
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "Failed: "+ e);
                        }
                    }) .addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<Barcode>> task) {
                            // close working images
                            imageProxy.close();
                            mediaImage.close();
                            if (scannedCode) {
                                Intent toResults = new Intent(ScanActivity.this, ResultsActivity.class);
                                toResults.putExtra("hashed", hashed);
                                toResults.putExtra("score", score);
                                startActivity(toResults);
                                finish();
                            }
                        }
                    });
        }
    }
    public long score(String hex) {
        long score = 0;
        HashMap<Character, Integer> hexMap = new HashMap<Character, Integer>();
        hexMap.put('0', 20); // handle 0 differently because it is built differently
        hexMap.put('1', 1);
        hexMap.put('2', 2);
        hexMap.put('3', 3);
        hexMap.put('4', 4);
        hexMap.put('5', 5);
        hexMap.put('6', 6);
        hexMap.put('7', 7);
        hexMap.put('8', 8);
        hexMap.put('9', 9);
        hexMap.put('a', 10);
        hexMap.put('b', 11);
        hexMap.put('c', 12);
        hexMap.put('d', 13);
        hexMap.put('e', 14);
        hexMap.put('f', 15);

        Pattern pattern = Pattern.compile("([0-9a-f])(\\1+)",  2);
        Matcher matcher = pattern.matcher(hex);
        while (matcher.find()) {
            String repeated = matcher.group();
            Log.d("REPEATED SUBSTRING:", repeated);
            long len = repeated.length() - 1; // subtract 1 from length
            double val = Math.pow(hexMap.get(repeated.charAt(0)),len);
            Log.d("SUBSTRING VALUE", ""+val);
            score += Math.pow(hexMap.get(repeated.charAt(0)),len);
        }
        Log.d("hex SCORE: ", ""+score);
        return score;
    }
}
