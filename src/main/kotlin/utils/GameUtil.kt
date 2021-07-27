package utils

import exceptions.PlayerInputException
import model.Game
import model.HumanPlayer
import model.Player

class GameUtil {
    object ErrorMessage {
        object Length {
            const val PREDICTOR = "input should have 3 characters, e.g. OC2"
            const val PARTICIPANT = "input should have 2 characters, e.g. CO"
        }
        object OC {
            const val PREDICTOR = "input should 'O' (open) or 'C' (close) for first two characters, e.g. OC2"
            const val PARTICIPANT = "input should 'O' (open) or 'C' (close) for first two characters, e.g. OC"
        }
        const val GUESS = "3rd character (guess) should be 0-4, e.g. OC2"
    }
    companion object {
        private const val NO_WINNER_MESSAGE = "No winner"

        fun countOpenHands(hands: String): Int {
            return hands.count { it == Game.ValidInput.OPEN.char }
        }

        private fun reset(player: Player) {
            player.hands = null
            player.prediction = null
        }

        fun isThereWinner(game: Game) = game.winner != null

        private fun isPredictorTheLastPerson(game: Game) = game.predictorIndex == (game.players.size - 1)

        fun displayWinner(winner: Player?) {
            if (winner == null)
                println(NO_WINNER_MESSAGE)
            else
                println(winner.winnerMessage)
        }

        fun printPlayerInput(humanPlayer: HumanPlayer) {
            println("${humanPlayer.type.displayName}: ${humanPlayer.input}")
        }

        fun printPlayerInput(player: Player) {
            var input = "${player.type.displayName}: ${player.hands}"
            if (player.prediction != null) input = input.plus(player.prediction)
            println(input)
        }

        fun printQuestion(predictorType: Player.Type) {
            println("${predictorType.displayName} ${predictorType.toBe} the predictor, what is your input?")
        }

        fun resetGame(game: Game) {
            resetPredictorTurn(game)
            game.winner = null
        }

        fun changePredictor(game: Game) {
            if (isPredictorTheLastPerson(game)) resetPredictorTurn(game)
            else game.predictorIndex++
        }

        private fun resetPredictorTurn(game: Game) {
            game.predictorIndex = 0
        }

        fun resetPlayers(game: Game) {
            game.players.forEach { reset(it) }
        }

        fun isStillPlaying(game: Game) = game.playingAnswer != Game.PlayingAnswer.NO

        fun validate(input: String, isPredicting: Boolean) {
            var errorMessageLength = ErrorMessage.Length.PARTICIPANT
            var errorMessageOC = ErrorMessage.OC.PARTICIPANT
            var validInputLength = Game.ValidLength.PARTICIPANT

            if (isPredicting) {
                errorMessageLength = ErrorMessage.Length.PREDICTOR
                errorMessageOC = ErrorMessage.OC.PREDICTOR
                validInputLength = Game.ValidLength.PREDICTOR
            }

            if (Validator.isStringLengthInvalid(input, validInputLength)) {
                throw PlayerInputException(errorMessageLength)
            }

            val hands = input.substring(0, 2)
            if (Validator.isStringHaveInvalidCharacters(hands!!, Game.VALID_INPUT_ARRAY)) {
                throw PlayerInputException(errorMessageOC)
            }
            if (isPredicting) {
                val guess = input[Game.GUESS_INDEX].digitToInt()
                if (guess < Game.MIN_RANGE_GUESS || guess > Game.MAX_RANGE_GUESS) {
                    throw PlayerInputException(ErrorMessage.GUESS)
                }
            }
        }
    }
}