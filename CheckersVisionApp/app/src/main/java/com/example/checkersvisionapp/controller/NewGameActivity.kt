package com.example.checkersvisionapp.controller

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
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
import arduino.Arduino
import arduino.ArduinoListener
import com.example.checkersvisionapp.R
import com.example.checkersvisionapp.model.checkers.MutableCheckersGame
import com.example.checkersvisionapp.persistence.StorageManager


class NewGameActivity : AppCompatActivity() {

    // variables
    private val game = MutableCheckersGame("Game_" + System.currentTimeMillis())
    private lateinit var preview: Preview;
    private lateinit var imageCapture: ImageCapture;
    private lateinit var useCasesGroup: UseCaseGroup


    // views
    private lateinit var previewView: PreviewView
    private lateinit var saveBtn: ImageButton
    private lateinit var takePhotoBtn: ImageButton
    private lateinit var finishBtn: ImageButton
    private lateinit var arduino: Arduino

    companion object {
        const val PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_game)

        // getting layout elements
        previewView = findViewById(R.id.previewView)
        saveBtn=findViewById(R.id.saveBtn)
        takePhotoBtn = findViewById(R.id.takePhotoBtn)
        finishBtn = findViewById(R.id.finishBtn)

        // arduino setup
        val context = this;
        arduino = Arduino(this)
        arduino.setArduinoListener(object : ArduinoListener {
            override fun onArduinoAttached(device: UsbDevice?) {
                arduino.open(device)
                runOnUiThread {
                    Toast.makeText(context, "arduino connected", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onArduinoDetached() {
                runOnUiThread {
                    Toast.makeText(context, "arduino disconnected", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onArduinoMessage(bytes: ByteArray?) {
                bytes?.let {
                    Log.w("arduino_msg", "messaggio")
                    val message = String(bytes).lowercase().trim()
                    if (message == "shift") {
                        takePhoto()
                    }
                }

            }

            override fun onArduinoOpened() {
                runOnUiThread {
                    Toast.makeText(context, "arduino open", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onUsbPermissionDenied() {
                arduino.reopen()
            }


        })

        // saveBtn initialization
        saveBtn.setOnClickListener {
            saveGame()
        }


        // camera initialization
        setupCamera(takePhotoBtn, previewView)


        // finishBtn initialization
        finishBtn.setOnClickListener {
            saveGame()
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
            || ContextCompat.checkSelfPermission(
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

    private fun saveGame()
    {
        StorageManager.saveCheckersGame(this,game)
    }

    private fun moveToPrediction() {
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

    private fun takePhoto() {
        imageCapture.takePicture(this.mainExecutor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    val cropRect = image.cropRect
                    val croppedImg = Bitmap.createBitmap(
                        image.toBitmap(),
                        cropRect.left,
                        cropRect.top,
                        cropRect.width(),
                        cropRect.height()
                    )
                    game.addPosition(croppedImg)
                    image.close()
                }
            })
    }

    private fun startCamera(
        cameraProvider: ProcessCameraProvider,
        takePhotoBtn: ImageButton,
        previewView: PreviewView
    ) {
        preview = Preview.Builder().build()
        imageCapture =
            ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
        useCasesGroup =
            UseCaseGroup.Builder().setViewPort(previewView.viewPort!!)
                .addUseCase(imageCapture).addUseCase(preview).build()

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        preview.setSurfaceProvider(previewView.surfaceProvider)
        takePhotoBtn.setOnClickListener {
            takePhoto()
        }
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            this as LifecycleOwner,
            cameraSelector,
            useCasesGroup
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        arduino.unsetArduinoListener()
        arduino.close()
    }


}