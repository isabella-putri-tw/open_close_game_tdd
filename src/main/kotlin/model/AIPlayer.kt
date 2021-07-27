package model

import utils.GameUtil
import kotlin.random.Random

open class AIPlayer : Player {
    override var hands: String? = null
    override var prediction: Int? = null
    override val type: Player.Type = Player.Type.AI
    override val winnerMessage: String = "AI WIN!!!"

    override fun play(isPredicting: Boolean) {
        hands = (1..2).map { Game.VALID_INPUT_ARRAY.random() }.joinToString("")
        if (isPredicting) prediction = Random.nextInt(
            Game.MIN_RANGE_GUESS,
            Game.MAX_RANGE_GUESS
        )
        GameUtil.printPlayerInput(this)
    }
}