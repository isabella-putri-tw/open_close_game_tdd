package exceptions

import java.util.*

class PlayerInputException(override val message: String): InputMismatchException(message) {
}