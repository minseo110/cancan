package com.example.cancan

class Braille {

    private val brailleChar: Char

    constructor(index: Int) {
        brailleChar = (0x2800 + index).toChar()
    }

    constructor(unicodeBraille: Char) {
        brailleChar = unicodeBraille
    }

    override fun toString(): String {
        return brailleChar.toString()
    }

    /**
     * 점자 유니코드 문자를 6점 배열로 변환
     * ex) ⠓ → [1, 1, 0, 0, 1, 0]
     */
    fun toDotArray(): List<Int> {
        val offset = brailleChar.code - 0x2800
        return List(6) { i -> if ((offset shr i) and 1 == 1) 1 else 0 }
    }

    companion object {

        fun createFromIndexNotation(indexNotation: String): Braille {
            val index = convertIndexNotationToInt(indexNotation)
            return Braille(index)
        }

        fun convertIndexNotationToInt(indexNotation: String): Int {
            return indexNotation
                .replace(" ", "")
                .split("-")
                .sumOf { 1.shl(it.toInt() - 1) }
        }

        fun createBraillesFromMultipleIndexNotation(multipleIndexNotation: String): List<Braille>? {
            if (multipleIndexNotation.isBlank()) return null
            return multipleIndexNotation
                .split(",")
                .map { createFromIndexNotation(it.trim()) }
        }

        fun createBraillesFromBrailleASCIICode(brailleAsciiCode: String): List<Braille> {
            val unicodeString = BrailleASCII.toUnicode(brailleAsciiCode)
            return unicodeString.map { Braille(it) }
        }
    }
}
