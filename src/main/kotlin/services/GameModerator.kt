package services

import model.Game
import model.HumanPlayer
import model.Player
import utils.GameUtil
import java.io.BufferedReader

class GameModerator(var reader: BufferedReader) {
    companion object {

        private const val PLAY_AGAIN_QUESTION = "Do you want to play again?"
        private const val WELCOME_MESSAGE = "Welcome to the game!"

        private fun chooseWinner(players: List<Player>, predictor: Player): Player? {
            val numberOfOpenHand = players.sumOf { GameUtil.countOpenHands(it.hands.toString()) }
            if (predictor.prediction == numberOfOpenHand) {
                return predictor
            }
            return null
        }
    }

    fun announceWinner(game: Game) {
        game.winner = chooseWinner(game.players, game.players[game.predictorIndex])
        GameUtil.displayWinner(game.winner)
    }

    fun askInputFromHumanPlayers(game: Game) {
        val humanPlayers = game.players.filterIsInstance<HumanPlayer>()
        humanPlayers.forEach {
            GameUtil.printQuestion(game.players[game.predictorIndex].type)
            val humanInput = reader.readLine()
            it.enterInput(humanInput)
        }
    }

    fun wrapRound(game: Game) {
        if (GameUtil.isThereWinner(game)) {
            askToPlayAgain(game)
        } else {
            GameUtil.changePredictor(game)
        }
        GameUtil.resetPlayers(game)
    }

    private fun askToPlayAgain(game: Game) {
        println(PLAY_AGAIN_QUESTION)
        val rawAnswer: CharArray = reader.readLine().toCharArray()
        val playingAnswer = Game.PlayingAnswer.values().firstOrNull {
            it.char == rawAnswer[0]
        }!!
        println(playingAnswer.char)
        if (playingAnswer == Game.PlayingAnswer.YES) {
            reader.close()
            GameUtil.resetGame(game)
        }
        game.playingAnswer = playingAnswer
    }

    fun welcome() {
        println(WELCOME_MESSAGE)
    }
}