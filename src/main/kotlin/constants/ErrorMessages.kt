package constants

object ErrorMessages {
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