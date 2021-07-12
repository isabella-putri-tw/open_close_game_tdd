package model

import kotlin.random.Random

abstract class Player(val type: PlayerType) {

    lateinit var hands: String

    constructor(type: PlayerType, hands: String): this(type) {
        this.hands = hands
    }

    object ValidInput {
        const val OPEN = 'O'
        const val CLOSE = 'C'
    }
    companion object {
        val VALID_INPUT_ARRAY = charArrayOf(ValidInput.OPEN, ValidInput.CLOSE)
    }

    abstract fun validate()

    open fun generateAIHands(): String {
        return (1..2).map { VALID_INPUT_ARRAY[Random.nextInt(0, 2)] }.joinToString("")
    }
}