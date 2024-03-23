package com.example.checkersvisionapp.model.predicted

class PredictedPosition (val game: PredictedGame, val n:Int, squares:MutableList<PredictedSquare>){
    private val squares= squares.toList()

    fun forEachSquare(action:(PredictedSquare)->Unit)
    {
        squares.forEach(action)
    }
    fun forEachSquare(action:(Int, PredictedSquare)->Unit)
    {
        squares.forEachIndexed(action)
    }
}