package com.example.checkersvisionapp.controller

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.checkersvisionapp.R
import com.example.checkersvisionapp.model.checkers.CheckersGame
import com.example.checkersvisionapp.model.checkers.MutableCheckersGame
import com.example.checkersvisionapp.model.predicted.PredictedPosition
import com.example.checkersvisionapp.model.predicted.SquareClass
import com.example.checkersvisionapp.model.predicted.PredictedGame
import com.example.checkersvisionapp.persistence.StorageManager

class PredictionActivity : AppCompatActivity() {

    // variables
    private lateinit var game: CheckersGame
    private lateinit var predictedGame: PredictedGame
    private var currentImgIndex = 0

    // views
    private lateinit var imgToAnalyze: ImageView
    private lateinit var predictionImg: ImageView
    private lateinit var previousBtn: ImageButton
    private lateinit var nextBtn: ImageButton

    companion object {
        const val PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prediction)

        // getting layout elements
        imgToAnalyze = findViewById(R.id.imgToAnalyze)
        predictionImg = findViewById(R.id.predictionImg)
        previousBtn = findViewById(R.id.previousBtn)
        nextBtn = findViewById(R.id.nextBtn)

        // game and predictedGame initialization
        game=loadCheckersGame()
        predictedGame=
            PredictedGame.of(game, StorageManager.loadModel("100_ep_v1_3_color.ptl",this))


        // prediction initialization
        updatePrediction()


        // previousBtn initialization
        previousBtn.setOnClickListener {
            if (currentImgIndex - 1 >= 0 && game.numberOfPositions!=0) {
                currentImgIndex--
                updatePrediction()
            }
        }

        // nextBtn initialization
        nextBtn.setOnClickListener {
            if (currentImgIndex + 1 < game.numberOfPositions) {
                currentImgIndex++
                updatePrediction()
            }
        }
    }

    private fun loadCheckersGame(): CheckersGame {
        val gameName = this.intent.getStringExtra("gameName").orEmpty()
        return StorageManager.loadLazyCheckersGame(this,gameName)
    }

    private fun updatePrediction()
    {
        if (game.numberOfPositions!=0) {
            imgToAnalyze.setImageBitmap(game.getPosition(currentImgIndex).img)
            predictionImg.setImageBitmap(visualRepresentationOf(predictedGame.getPosition(currentImgIndex)))
        }
    }

    private fun visualRepresentationOf(position: PredictedPosition):Bitmap{
        val predictedImage = Bitmap.createBitmap(game.getPosition(currentImgIndex).img.width,game.getPosition(currentImgIndex).img.height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(predictedImage)
        val cropSize=game.getPosition(currentImgIndex).img.width

        // Create the virtual chessboard with the prediction results
        position.forEachSquare{ index,square->
            val predictedIcon = when (square.predictedClass) {
                SquareClass.WHITE_SQUARE -> BitmapFactory.decodeResource(resources,R.drawable.white_square)
                SquareClass.BLACK_SQUARE -> BitmapFactory.decodeResource(resources,R.drawable.black_square)
                SquareClass.WHITE_MAN -> BitmapFactory.decodeResource(resources,R.drawable.white_man)
                SquareClass.BLACK_MAN -> BitmapFactory.decodeResource(resources,R.drawable.black_man)
            }
            val left=index/4 * cropSize/8
            val right=left+cropSize/8
            val top=((index*2+(index/4)%2)%8)*cropSize/8
            val bottom=top+cropSize/8

            val resizedIcon = resizePhoto(predictedIcon, cropSize)
            val rect= Rect(left,top, right, bottom)
            canvas.drawBitmap(resizedIcon, null, rect, null)
        }
        return predictedImage
    }
    private fun resizePhoto(bitmap: Bitmap, size: Int): Bitmap {
        // Set the new dimensions of the image
        val desiredW = size / 8
        val desiredH = size / 8

        // Create and return a new image with the given dimensions
        return Bitmap.createScaledBitmap(bitmap, desiredW, desiredH, false)
    }
}