package model

import exceptions.PlayerInputException
import utils.Validator

class ParticipantPlayer : Player {

    constructor(type: PlayerType) : super(type) {
        hands = generateAIHands()
    }
    constructor(type: PlayerType, input: String): super(type, input)

    companion object {
        const val INPUT_LENGTH = 2
    }

    object ErrorMessage {
        const val LENGTH = "input should have 2 characters, e.g. CO"
        const val OC = "input should 'O' (open) or 'C' (close) for first two characters, e.g. OC"
    }

    override fun validate() {
        if (Validator.isStringLengthInvalid(hands, INPUT_LENGTH)) {
            throw PlayerInputException(ErrorMessage.LENGTH)
        }
        if (Validator.isStringHaveInvalidCharacters(hands, VALID_INPUT_ARRAY)) {
            throw PlayerInputException(ErrorMessage.OC)
        }
    }
}