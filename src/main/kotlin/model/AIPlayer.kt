package model

import kotlin.random.Random

open class AIPlayer: Player() {
    override val type: Type = Type.AI
    override val winnerMessage: String = "AI WIN!!!"

    override fun play(isPredicting: Boolean) {
        hands = (1..2).map { VALID_INPUT_ARRAY.random() }.joinToString("")
        if (isPredicting) prediction = Random.nextInt(
            MIN_RANGE_GUESS,
            MAX_RANGE_GUESS
        )
        printPlayerInput()
    }
}