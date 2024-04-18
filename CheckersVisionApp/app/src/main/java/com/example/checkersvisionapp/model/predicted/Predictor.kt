package com.example.checkersvisionapp.model.predicted

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import com.example.checkersvisionapp.model.checkers.CheckersPosition
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils

class Predictor(private val module: Module) {

    fun predictedSquaresOf(position: CheckersPosition): MutableList<PredictedSquare> {
        val imgToPredict = position.img
        //val imgToPredict =convertToGrayscale(position.img)
        val squareSize = imgToPredict.width /10
        val borderSize=5
        val squarePredictions = mutableListOf<PredictedSquare>()

        var counter = 0
        for (i in 0..7) {
            counter = (counter + 1) % 2
            for (j in 0..7) {
                if ((counter + j) % 2 != 0) {
                    val squareImg = Bitmap.createBitmap(
                        imgToPredict,
                        (i+1) * squareSize-borderSize,
                        (j+1) * squareSize-borderSize,
                        squareSize+2*borderSize,
                        squareSize+2*borderSize
                    )
                    val squareImgResized = Bitmap.createScaledBitmap(squareImg, 65, 65, false)
                    val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                        squareImgResized,
                        TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                        TensorImageUtils.TORCHVISION_NORM_STD_RGB
                    )
                    val outputTensor = module.forward(IValue.from(inputTensor)).toTensor()
                    val scores = outputTensor.dataAsFloatArray


                    var maxIndex = 0
                    var max = -Float.MAX_VALUE
                    for (k in scores.indices) {
                        if (scores[k] >= max && k > 0) { // excluding white_squares
                            max = scores[k]
                            maxIndex = k
                        }

                    }
                    squarePredictions.add(
                        PredictedSquare(
                            SquareClass.entries[maxIndex],
                            max
                        )
                    )
                }
            }
        }
        return squarePredictions
    }

    private fun convertToGrayscale(inputBitmap: Bitmap): Bitmap {
        // Create a new Bitmap with the same dimensions as the input Bitmap
        val outputBitmap =
            Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, inputBitmap.config)

        // Create a Canvas and link it to the new Bitmap
        val canvas = Canvas(outputBitmap)

        // Create a Paint object with a ColorMatrixColorFilter to convert to grayscale
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f) // 0 means grayscale
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

        // Draw the input Bitmap onto the Canvas with the grayscale Paint
        canvas.drawBitmap(inputBitmap, 0f, 0f, paint)

        return outputBitmap
    }


}