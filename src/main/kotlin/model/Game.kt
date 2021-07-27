package model

data class Game(
    val players: List<Player>,
    var predictorIndex: Int = 0,
    var winner: Player? = null,
    var playingAnswer: PlayingAnswer = PlayingAnswer.YES
) {
    enum class PlayingAnswer(val char: Char) {
        YES('Y'), NO('N')
    }
    enum class ValidInput(val char: Char) {
        OPEN('O'), CLOSE('C')
    }
    object ValidLength {
        const val PREDICTOR = 3
        const val PARTICIPANT = 2
    }

    companion object {
        val VALID_INPUT_ARRAY = ValidInput.values().map { it.char }.toCharArray()
        const val GUESS_INDEX = 2
        const val MIN_RANGE_GUESS = 0
        const val MAX_RANGE_GUESS = 4
    }
}
