package com.example.checkersvisionapp.controller

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.checkersvisionapp.R
import com.example.checkersvisionapp.model.checkers.MutableCheckersGame
import com.example.checkersvisionapp.persistence.StorageManager

class NewGameActivity : AppCompatActivity() {

    // variables
    private val game = MutableCheckersGame("Game_" + System.currentTimeMillis())

    // views
    private lateinit var previewView: PreviewView
    private lateinit var takePhotoBtn: ImageButton
    private lateinit var finishBtn: ImageButton

    companion object {
        const val PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_game)

        // getting layout elements
        previewView = findViewById(R.id.previewView)
        takePhotoBtn = findViewById(R.id.takePhotoBtn)
        finishBtn = findViewById(R.id.finishBtn)


        // camera initialization
        setupCamera(takePhotoBtn, previewView)


        // finishBtn initialization
        finishBtn.setOnClickListener {
            moveToPrediction()
        }

        if (!hasPermission()) {
            requestPermission()
        }


    }

    private fun hasPermission(): Boolean {
        var result = true
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            result = false
        }
        return result
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CODE
        )

    }

    private fun moveToPrediction() {
        StorageManager.saveCheckersGame(this, game)
        val intent = Intent(this, PredictionActivity::class.java)
        intent.putExtra("gameName", game.name)
        startActivity(intent)
    }

    private fun setupCamera(takePhotoBtn: ImageButton, previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            startCamera(cameraProvider, takePhotoBtn, previewView)
        }, ContextCompat.getMainExecutor(this))

    }

    private fun startCamera(
        cameraProvider: ProcessCameraProvider,
        takePhotoBtn: ImageButton,
        previewView: PreviewView
    ) {
        val preview = Preview.Builder().build()
        val imageCapture =
            ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
        val useCasesGroup =
            UseCaseGroup.Builder().setViewPort(previewView.viewPort!!)
                .addUseCase(imageCapture).addUseCase(preview).build()

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        preview.setSurfaceProvider(previewView.surfaceProvider)
        takePhotoBtn.setOnClickListener {
            imageCapture.takePicture(this.mainExecutor,
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        super.onCaptureSuccess(image)
                        val cropRect=image.cropRect
                        val croppedImg=Bitmap.createBitmap(image.toBitmap(),cropRect.left,cropRect.top,cropRect.width(),cropRect.height())
                        game.addPosition(croppedImg)
                        image.close()
                    }
                })

        }
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            this as LifecycleOwner,
            cameraSelector,
            useCasesGroup
        )

    }


}