package com.example.cancan

object NumberFunc {
    val number_braille = "⠬"

    val number_braille_dict = mapOf(
        "⠚" to "0", // ⠪ = ⠚
        "⠁" to "1",
        "⠃" to "2",
        "⠉" to "3",
        "⠙" to "4",
        "⠑" to "5",
        "⠋" to "6",
        "⠛" to "7",
        "⠓" to "8",
        "⠊" to "9"
    )

    val number_punctuation_invalid = listOf("~", " ", "⠀") // ⠀ = blank braille (⠀)
    val number_punctuation_valid = listOf(":", "-", ".", "·", "⠢", "⠐", "⠤")

    fun changeToNumber(c: String): String {
        return number_braille_dict[c] ?: c
    }

    fun translateNumber(text: String): String {
        val result = StringBuilder()
        var isDigit = false

        for (i in text.indices) {
            val ch = text[i].toString()

            if (ch == number_braille) {
                isDigit = true
                continue
            } else if (ch in number_punctuation_valid && isDigit) {
                when (ch) {
                    "⠢" -> result.append(".")
                    "⠐" -> result.append(",")
                    else -> result.append(ch)
                }
                isDigit = true
                continue
            } else if (ch in number_punctuation_invalid) {
                result.append(ch)
                isDigit = false
                continue
            } else if (ch in number_braille_dict.keys && isDigit) {
                result.append(changeToNumber(ch))
                isDigit = true
                continue
            } else {
                isDigit = false
                result.append(ch)
                continue
            }
        }

        return result.toString()
    }

}