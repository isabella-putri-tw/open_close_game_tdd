package utils

import constants.ErrorMessages
import exceptions.PlayerInputException
import model.Game
import model.HumanPlayer
import model.Player

class GameUtil {
    companion object {
        private const val NO_WINNER_MESSAGE = "No winner"

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

        fun validate(input: String, isPredicting: Boolean) {
            var errorMessageLength = ErrorMessages.Length.PARTICIPANT
            var errorMessageOC = ErrorMessages.OC.PARTICIPANT
            var validInputLength = Game.ValidLength.PARTICIPANT

            if (isPredicting) {
                errorMessageLength = ErrorMessages.Length.PREDICTOR
                errorMessageOC = ErrorMessages.OC.PREDICTOR
                validInputLength = Game.ValidLength.PREDICTOR
            }

            if (input.isLengthInvalid(validInputLength)) {
                throw PlayerInputException(errorMessageLength)
            }

            val hands = input.substring(0, 2)
            if (hands.hasInvalidCharacters(Game.VALID_INPUT_ARRAY)) {
                throw PlayerInputException(errorMessageOC)
            }
            if (isPredicting) {
                val guess = input[Game.GUESS_INDEX].digitToInt()
                if (guess < Game.MIN_RANGE_GUESS || guess > Game.MAX_RANGE_GUESS) {
                    throw PlayerInputException(ErrorMessages.GUESS)
                }
            }
        }
    }
}