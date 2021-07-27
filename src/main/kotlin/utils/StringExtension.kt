package utils

fun String.isLengthInvalid(expectedLength: Int): Boolean {
    return this.length != expectedLength
}

fun String.hasInvalidCharacters(expectedCharacters: CharArray): Boolean {
    var i = 0
    do {
        if (!expectedCharacters.contains(this[i])) return true
        i++
    } while (i < this.length)
    return false
}