package model

import exceptions.PlayerInputException
import utils.Validator
import kotlin.random.Random

class PredictorPlayer : Player {

    constructor(type: PlayerType) : super(type) {
        hands = generateAIHands()
    }

    constructor(type: PlayerType, input: String): super(type, input)

    object ErrorMessage {
        const val LENGTH = "input should have 3 characters, e.g. OC2"
        const val GUESS = "3rd character (guess) should be 0-4, e.g. OC2"
        const val OC = "input should 'O' (open) or 'C' (close) for first two characters, e.g. OC2"
    }


    companion object {
        const val INPUT_LENGTH = 3
        const val MIN_RANGE_GUESS = 0
        const val MAX_RANGE_GUESS = 4
        const val GUESS_INDEX = 2
        val WINNER = mapOf(
            PlayerType.USER to "You WIN!!!",
            PlayerType.AI to "AI WIN!!!"
        )
    }

    override fun validate() {
        if (Validator.isStringLengthInvalid(hands, INPUT_LENGTH)) {
            throw PlayerInputException(ErrorMessage.LENGTH)
        }
        if (Validator.isStringHaveInvalidCharacters(hands.substring(0, GUESS_INDEX), Player.VALID_INPUT_ARRAY)) {
            throw PlayerInputException(ErrorMessage.OC)
        }
        val guess = hands[GUESS_INDEX].digitToInt()
        if (guess < MIN_RANGE_GUESS || guess > MAX_RANGE_GUESS) {
            throw PlayerInputException(ErrorMessage.GUESS)
        }
    }

    fun isGuessCorrect(otherHands: String): Boolean {
        val numberOfOpen = getNumberOfOpen(hands.plus(otherHands))
        val guess = hands[2].digitToInt()

        return numberOfOpen == guess
    }

    fun getWinner(): String {
        return WINNER[type]!!
    }

    private fun getNumberOfOpen(input: String): Int {
        return input.count { it == ValidInput.OPEN }
    }

    private fun generatePrediction(): Int {
        return Random.nextInt(MIN_RANGE_GUESS, MAX_RANGE_GUESS)
    }

    override fun generateAIHands(): String {
        return super.generateAIHands().plus(generatePrediction().toString())
    }
}