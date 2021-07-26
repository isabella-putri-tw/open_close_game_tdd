package model

import exceptions.PlayerInputException
import utils.Validator

class HumanPlayer: Player() {
    var input: String? = null

    override val type: Type = Type.HUMAN
    override val winnerMessage: String = "You WIN!!!"

    fun enterInput(input:String) {
        this.input = input
    }

    companion object {
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
    }

    override fun printPlayerInput() {
        println("${type.displayName}: $input")
    }

    fun printQuestion(predictorType: Type) {
        println("${predictorType.displayName} ${predictorType.toBe} the predictor, what is your input?")
    }

    override fun play(isPredicting: Boolean) {
        printPlayerInput()
        validate(isPredicting)
        hands = input?.substring(0, 2)
        if (isPredicting) prediction = input!!.substring(2, 3).toInt()
    }

    private fun validate(isPredicting: Boolean) {
        var errorMessageLength = ErrorMessage.Length.PARTICIPANT
        var errorMessageOC = ErrorMessage.OC.PARTICIPANT
        var validInputLength = ValidLength.PARTICIPANT

        if (isPredicting) {
            errorMessageLength = ErrorMessage.Length.PREDICTOR
            errorMessageOC = ErrorMessage.OC.PREDICTOR
            validInputLength = ValidLength.PREDICTOR
        }

        if (Validator.isStringLengthInvalid(input!!, validInputLength)) {
            throw PlayerInputException(errorMessageLength)
        }

        val hands = input?.substring(0, 2)
        if (Validator.isStringHaveInvalidCharacters(hands!!, VALID_INPUT_ARRAY)) {
            throw PlayerInputException(errorMessageOC)
        }
        if (isPredicting) {
            val guess = input!![GUESS_INDEX].digitToInt()
            if (guess < MIN_RANGE_GUESS || guess > MAX_RANGE_GUESS) {
                throw PlayerInputException(ErrorMessage.GUESS)
            }
        }
    }

    override fun reset() {
        super.reset()
        input = null
    }
}