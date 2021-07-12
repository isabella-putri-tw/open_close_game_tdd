package utils

class Validator {
    companion object {
        fun isStringLengthInvalid(string: String, expectedLength: Int): Boolean {
            return string.length!= expectedLength
        }

        fun isStringHaveInvalidCharacters(string: String, expectedCharacters: CharArray): Boolean {
            var i = 0
            do {
                if (!expectedCharacters.contains(string[i])) return true
                i++
            } while (i < string.length)
            return false
        }
    }
}