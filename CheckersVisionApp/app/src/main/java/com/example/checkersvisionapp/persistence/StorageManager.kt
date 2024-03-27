package com.example.checkersvisionapp.persistence

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.example.checkersvisionapp.controller.oldGames.CheckersGameView
import com.example.checkersvisionapp.model.checkers.CheckersGame
import com.example.checkersvisionapp.model.checkers.MutableCheckersGame
import com.example.checkersvisionapp.model.checkers.ImmutableLazyCheckersGame
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
            val positionFile = File(gameDir, "Position_${String.format("%03d",position.n)}.jpg")
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
        gameDir.takeIf { it.exists() && it.isDirectory }?.let {
            result = it.deleteRecursively()
        }
        return result
    }

    fun loadEagerCheckersGame(context: Context, gameName: String): CheckersGame {
        val gameDir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            gameName
        )
        val positionImgList = mutableListOf<Bitmap>()
        gameDir.takeIf { it.exists() && it.isDirectory }?.let {
            it.listFiles()
                .filter { positionImgFile -> positionImgFile.canRead() && positionImgFile.isFile }
                .sorted()
                .forEachIndexed(){ index,positionImgFile ->
                    positionImgList.add(index,BitmapFactory.decodeFile(positionImgFile.absolutePath))
                }
        }
        val game = MutableCheckersGame(gameName)
        positionImgList.forEachIndexed { index, bitmap -> game.addPosition(index, bitmap) }
        return game

    }

    fun loadLazyCheckersGame(context: Context, gameName: String): CheckersGame {
        // directory creation
        val gameDir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            gameName
        )
        gameDir.mkdirs()
        return ImmutableLazyCheckersGame(gameName,gameDir.listFiles().size,context)

    }

    fun loadPositionImg(context:Context,gameDir: File,positionNumber:Int):Bitmap
    {
        val positionFile = File(gameDir, "Position_${String.format("%03d",positionNumber)}.jpg")

        return BitmapFactory.decodeFile(positionFile.absolutePath);
    }

    fun loadCheckersGamesView(context: Context): List<CheckersGameView> {
        val parentDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val games = mutableListOf<CheckersGameView>()
        var id=0L

        parentDir?.takeIf { it.exists() && it.isDirectory }?.let {
            it.listFiles().filter { gameDir->gameDir.isDirectory }.forEach { gameDir ->
                id++
                games.add(CheckersGameView(id,gameDir.nameWithoutExtension))
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