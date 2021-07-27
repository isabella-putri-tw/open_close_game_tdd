package model

import utils.GameUtil

class HumanPlayer : Player {
    var input: String? = null
    override var hands: String? = null
    override var prediction: Int? = null

    override val type: Player.Type = Player.Type.HUMAN
    override val winnerMessage: String = "You WIN!!!"

    fun enterInput(input:String) {
        this.input = input
    }

    override fun play(isPredicting: Boolean) {
        GameUtil.printPlayerInput(this)
        GameUtil.validate(input.toString(), isPredicting)
        hands = input?.substring(0, 2)
        if (isPredicting) prediction = input!!.substring(2, 3).toInt()
    }
}