package services

import exceptions.PlayerInputException
import model.Game
import model.HumanPlayer
import model.Player
import utils.GameUtil
import java.io.BufferedReader
import java.io.InputStreamReader

open class GameService() {
    var reader = BufferedReader(InputStreamReader(System.`in`))
    var gameModerator = GameModerator(reader)

    var game: Game = Game(listOf())

    fun playOneRound() {
        game.players.forEachIndexed { i, player ->
            player.play(game.predictorIndex == i)
        }
        gameModerator.announceWinner(game)
    }

    fun startGame(game: Game) {
        this.game = game
        gameModerator.welcome()

        do {
            try {
                gameModerator.askInputFromHumanPlayers(this.game)
                playOneRound()
                gameModerator.wrapRound(this.game)
            } catch (ex: PlayerInputException) {
                println("Bad input: ${ex.message}")
            }
        } while (GameUtil.isStillPlaying(this.game))
    }








}