package services

import exceptions.PlayerInputException
import model.HumanPlayer
import model.Player
import java.io.BufferedReader
import java.io.InputStreamReader

open class OpenCloseGameService(private val players: List<Player>) {
    var reader = BufferedReader(InputStreamReader(System.`in`))
    var predictorIndex = 0
    var winner: Player? = null

    companion object {
        private const val WELCOME_MESSAGE = "Welcome to the game!"
        private const val DEFAULT_WINNER_MESSAGE = "No winner"
        private const val PLAY_AGAIN_QUESTION = "Do you want to play again?"
    }

    enum class PlayingAnswer(val char: Char) {
        YES('Y'), NO('N')
    }

    fun playOneRound() {
        players.forEachIndexed { i, player ->
            player.play(predictorIndex == i)
        }
        chooseWinner()
    }

    private fun chooseWinner() {
        val numberOfOpenHand = players.sumOf { it.countOpenHand() }
        if (players[predictorIndex].prediction == numberOfOpenHand) {
            winner = players[predictorIndex]
            println(winner?.winnerMessage)
        } else {
            println(DEFAULT_WINNER_MESSAGE)
        }
    }

    fun startGame() {
        println(WELCOME_MESSAGE)
        var playingAnswer: PlayingAnswer = PlayingAnswer.YES
        do {
            try {
                askInputFromHumanPlayers()
                playOneRound()
                playingAnswer = wrapRound()
            } catch (ex: PlayerInputException) {
                println("Bad input: ${ex.message}")
            }

        } while (playingAnswer != PlayingAnswer.NO)
    }

    private fun wrapRound(): PlayingAnswer {
        var playingAnswer = PlayingAnswer.YES
        if (isThereWinner()) {
            playingAnswer = askToPlayAgain()
        } else {
            changePredictor()
        }
        resetPlayer()
        return playingAnswer
    }

    private fun askToPlayAgain(): PlayingAnswer {
        println(PLAY_AGAIN_QUESTION)
        val rawAnswer: CharArray = reader.readLine().toCharArray()
        val playingAnswer = PlayingAnswer.values().firstOrNull {
            it.char == rawAnswer[0]
        }!!
        println(playingAnswer.char)
        if (playingAnswer == PlayingAnswer.YES) {
            reader.close()
            resetGame()
        }
        return playingAnswer
    }

    private fun resetGame() {
        resetPredictorTurn()
        winner = null
    }

    private fun changePredictor() {
        if (predictorIndex < (players.size - 1)) predictorIndex++
        else resetPredictorTurn()
    }

    private fun resetPredictorTurn() {
        predictorIndex = 0
    }

    private fun isThereWinner() = winner != null

    private fun askInputFromHumanPlayers() {
        val humanPlayers = players.filterIsInstance<HumanPlayer>()
        humanPlayers.forEach {
            it.printQuestion(players[predictorIndex].type)
            val humanInput = reader.readLine()
            it.enterInput(humanInput)
        }
    }

    private fun resetPlayer() {
        players.forEach { it.reset() }
    }


}