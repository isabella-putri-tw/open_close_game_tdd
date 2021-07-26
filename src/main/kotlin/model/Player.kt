package model

abstract class Player {
    enum class Type(val displayName: String, val toBe: String) {
        HUMAN("You", "are"), AI("AI", "is")
    }

    enum class ValidInput(val char: Char) {
        OPEN('O'), CLOSE('C')
    }

    companion object {
        val VALID_INPUT_ARRAY = ValidInput.values().map { it.char }.toCharArray()

        const val GUESS_INDEX = 2

        const val MIN_RANGE_GUESS = 0
        const val MAX_RANGE_GUESS = 4
    }

    object ValidLength {
        const val PREDICTOR = 3
        const val PARTICIPANT = 2
    }

    var hands: String? = null
    var prediction: Int? = null

    abstract val type: Type
    abstract val winnerMessage: String

    abstract fun play(isPredicting: Boolean)

    fun countOpenHand(): Int {
        return hands!!.count { it == ValidInput.OPEN.char }
    }

    open fun printPlayerInput() {
        var input = "${type.displayName}: $hands"
        if (prediction != null) input = input.plus(prediction)
        println(input)
    }
    open fun reset() {
        hands = null
        prediction = null
    }
}