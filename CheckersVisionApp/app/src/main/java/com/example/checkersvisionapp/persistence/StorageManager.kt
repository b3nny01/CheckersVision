package com.example.checkersvisionapp.persistence

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.example.checkersvisionapp.model.CheckersGame
import org.pytorch.Module
import java.io.File

object StorageManager {


    fun saveCheckersGame(context: Context, game: CheckersGame) {
        // directory creation
        val gameDir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            game.name
        )
        gameDir.mkdirs()

        // position files creation
        game.forEachPosition { position ->
            val rawImg = position.img
            val centerX = rawImg.width / 2
            val centerY = rawImg.height / 2
            val cropSize = rawImg.height
            val left = centerX - (cropSize / 2)
            val top = centerY - (cropSize / 2)
            val imgToSave = Bitmap.createBitmap(rawImg, left, top, cropSize, cropSize)
            val positionFile = File(gameDir, "Position_${position.n}.jpg")
            positionFile.createNewFile()
            imgToSave.compress(Bitmap.CompressFormat.JPEG, 100, positionFile.outputStream())
        }

    }

    fun deleteCheckersGame(context: Context, gameName: String): Boolean {
        var result = false
        val gameDir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            gameName
        )
        gameDir.takeIf { gameDir.exists() && gameDir.isDirectory }?.let { gameDir ->
            result = gameDir.deleteRecursively()
        }
        return result
    }

    fun loadCheckersGame(context: Context, gameName: String): CheckersGame {
        val gameDir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            gameName
        )
        val positionImgList = mutableListOf<Bitmap>()
        gameDir.takeIf { gameDir -> gameDir.isDirectory }?.let { gameDir ->
            gameDir.listFiles()
                .filter { positionImgFile -> positionImgFile.canRead() && positionImgFile.isFile }
                .sorted()
                .map { positionImgFile ->
                    positionImgList.add(BitmapFactory.decodeFile(positionImgFile.absolutePath))
                }
        }
        val game = CheckersGame(gameName)
        positionImgList.forEachIndexed { index, bitmap -> game.addPosition(index, bitmap) }
        return game

    }

    fun loadCheckersGames(context: Context): List<CheckersGame> {
        val parentDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        val games = mutableListOf<CheckersGame>()

        parentDir?.let { parentDir ->
            parentDir.listFiles().filter { it.isDirectory }.forEach { gameDir ->
                games.add(loadCheckersGame(context, gameDir.nameWithoutExtension))
            }
        }
        return games
    }


    fun loadModel(modelName: String, context: Context): Module {
        val modelFile = File(context.filesDir, modelName)
        if (!modelFile.exists()) {
            context.assets.open("""models/${modelName}""").transferTo(modelFile.outputStream())
        }
        return Module.load(modelFile.absolutePath)
    }
}