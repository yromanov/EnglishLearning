package group.ius.englishlearning.utils

fun isValidPassword(str: String): Boolean {
    return str.length > 2
}

fun isValidNick(str: String): Boolean {
    return str.length > 1
}