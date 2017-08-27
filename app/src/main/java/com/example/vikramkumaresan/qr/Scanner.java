package com.example.vikramkumaresan.qr;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;

public class Scanner extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    CameraManager manager;
    TextureView texture;
    CameraDevice.StateCallback stateCallback;
    ImageReader imgRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Permission Check
        String[] permissions = {android.Manifest.permission.CAMERA};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 102);
        }

        texture = (TextureView) findViewById(R.id.texture);
        texture.setSurfaceTextureListener(this);
        SurfaceTexture surfTex = texture.getSurfaceTexture();

        stateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                try {
                    final CaptureRequest.Builder captureRequest = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                    SurfaceTexture surfaceTexture = texture.getSurfaceTexture();
                    captureRequest.addTarget(new Surface(surfaceTexture));
                    camera.createCaptureSession(Arrays.asList(new Surface(texture.getSurfaceTexture())), new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            Log.d("test", "onConfigured");
                            try {
                                session.setRepeatingRequest(captureRequest.build(),null,null);
                            } catch (CameraAccessException e) {
                                Log.d("test","SetRepeatingRequest   >>ERROR");
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Log.d("test", "onConfigureFailed");
                        }
                    }, null);

                } catch (CameraAccessException e) {
                    Log.d("test", "Capture Request >>ERROR");
                }
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
                Log.d("test", "Disconnected");
            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {
                Log.d("test", "Error");
            }
        };

        manager = (CameraManager) getSystemService(CAMERA_SERVICE);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d("test", "TEXTURE AVAILABLE");
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                return;
            }
            manager.openCamera(manager.getCameraIdList()[0], stateCallback, null);

        } catch (CameraAccessException e) {
            Log.d("test","manager.openCamera()  >>>ERROR");
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
