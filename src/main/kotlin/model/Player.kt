package model

interface Player {
    enum class Type(val displayName: String, val toBe: String) {
        HUMAN("You", "are"), AI("AI", "is")
    }

    var hands: String?
    var prediction: Int?

    val type: Type
    val winnerMessage: String

    fun play(isPredicting: Boolean)
}

fun Player.reset() {
    this.hands = null
    this.prediction = null
}

fun Player.countOpenHands(): Int {
    return this.hands!!.count { it == Game.ValidInput.OPEN.char }
}