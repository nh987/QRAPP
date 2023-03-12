package com.example.qrapp;

import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import android.Manifest;
import android.widget.Toast;

public class PictureActivity extends AppCompatActivity {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageButton backButton;
    private PreviewView previewView;
    private Button takePicture;
    private ImageCapture imageCapture;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String hashed;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            hashed = extras.getString("hashed"); // for use as image docID.
        }

        setContentView(R.layout.picture_activity);
        previewView = findViewById(R.id.picture_preview);
        backButton = findViewById(R.id.picture_back_btn);
        takePicture = findViewById(R.id.picture_take_photo);

//        String[] permissionsStorage = {Manifest.permission.READ_EXTERNAL_STORAGE};
//        int requestExternalStorage = 1;
//        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, permissionsStorage, requestExternalStorage);
//        }

        //go back to main activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capturePicture();
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
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
    }
    private void capturePicture() {

        imageCapture.takePicture(getExecutor(), new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        super.onCaptureSuccess(image);
                        Log.d("TAG", "onError: Successfully captured image");
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        super.onError(exception);
                        Log.d("TAG", "onError: Failed to capture image");
                    }
                });

//        File file = new File( getExternalFilesDir(null), hashed + ".jpg");
//        imageCapture.takePicture(
//                new ImageCapture.OutputFileOptions.Builder(file).build(),
//                getExecutor(),
//                new ImageCapture.OnImageSavedCallback() {
//                    @Override
//                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                        Toast.makeText(PictureActivity.this, "Photo added", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onError(@NonNull ImageCaptureException exception) {
//                        Toast.makeText(PictureActivity.this, "Photo failed", Toast.LENGTH_SHORT).show();
//                    }
//                }
//        );

    }

    private void uploadImage() {
//        StorageReference storageRef = storage.getReference();
//        StorageReference imagesRef = storageRef.child(hashed);
    }
}
