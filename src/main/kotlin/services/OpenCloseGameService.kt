package services

import exceptions.PlayerInputException
import model.ParticipantPlayer
import model.PlayerType
import model.PredictorPlayer
import java.io.BufferedReader
import java.io.InputStreamReader

open class OpenCloseGameService() {

    companion object {
        private const val DEFAULT_WINNER = "No winner"
    }

    var reader = BufferedReader(InputStreamReader(System.`in`))
    var win = false

    fun play(predictorPlayer: PredictorPlayer, participantPlayer: ParticipantPlayer): String {
        var result = DEFAULT_WINNER
        try {
            predictorPlayer.validate()
            participantPlayer.validate()
            if (predictorPlayer.isGuessCorrect(participantPlayer.hands)){
                result = predictorPlayer.getWinner()
                win = true
            }


        } catch (ex: PlayerInputException) {
            result = "Bad input: ${ex.message}"
        }
        return result
    }

    fun startGame() {
        println("Welcome to the game!")

        var stillPlaying: String

        do {
            var isUserPredictor = true
            do {
                if (isUserPredictor) userPredictRound()
                else aiPredictRound()
                isUserPredictor = !isUserPredictor
            } while (!win)

            println("Do you want to play again?")
            stillPlaying = reader.readLine()
            println(stillPlaying)
        } while (stillPlaying != "N")

    }

    private fun userPredictRound() {
        val userInput = getUserHands("You are")
        val result = play(PredictorPlayer(PlayerType.USER, userInput), ParticipantPlayer(PlayerType.AI))
        println(result)
    }

    private fun aiPredictRound() {
        val userInput = getUserHands("AI is")
        val result = play(PredictorPlayer(PlayerType.AI), ParticipantPlayer(PlayerType.USER, userInput))
        println(result)
    }

    private fun getUserHands(predictorAlias: String): String {
        println("$predictorAlias the predictor, what is your input?")
        val userInput = reader.readLine()!!
        println("You: $userInput")
        return userInput
    }
}