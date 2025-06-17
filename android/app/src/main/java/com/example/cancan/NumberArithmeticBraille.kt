package com.example.cancan

class NumberArithmeticBraille(private val character: Char) {

    companion object {
        val convertData = mapOf(
            '0' to "2-4-5",
            '1' to "1",
            '2' to "1-2",
            '3' to "1-4",
            '4' to "1-4-5",
            '5' to "1-5",
            '6' to "1-2-4",
            '7' to "1-2-4-5",
            '8' to "1-2-5",
            '9' to "2-4"
            // 연산자는 주석처리 (필요시 확장 가능)
            // '+': "2-6",
            // '−': "3-5",  // Unicode minus
            // '×': "1-6",
            // '÷': "3-4,3-4",
            // '=': "2-5,2-5"
        )
    }

    override fun toString(): String {
        val indexNotation = convertData[character]
            ?: throw IllegalArgumentException("Unsupported number character: $character")
        return Braille.createFromIndexNotation(indexNotation).toString()
    }

    fun toStringWithoutRules(): String {
        return toString() // 숫자에 대한 규칙은 동일
    }
}
