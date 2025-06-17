package com.example.cancan

class PunctuationMarkBraille(private val letter: Char) {

    companion object {
        private val brailleMap = mapOf(
            '.' to "\u2832",
            '?' to "\u2826",
            '!' to "\u2816",
            ',' to "\u2810",
            '\u00B7' to "\u2810\u2806", // ·
            ':' to "\u2810\u2802",
            ';' to "\u2830\u2806",
            '/' to "\u2838\u280C",
            '“' to "\u2826",
            '”' to "\u2834",
            '‘' to "\u2820\u2826",
            '’' to "\u2834\u2804",
            '(' to "\u2826\u2804",
            ')' to "\u2820\u2834",
            '{' to "\u2826\u2802",
            '}' to "\u2810\u2834",
            '〔' to "\u2826\u2806", // 〔
            '〕' to "\u2830\u2834", // 〕
            '[' to "\u2826\u2806",
            ']' to "\u2830\u2834",
            '『' to "\u2830\u2826", // 『
            '』' to "\u2834\u2806", // 』
            '《' to "\u2830\u2836", // 《
            '》' to "\u2836\u2806", // 》
            '「' to "\u2810\u2826", // 「
            '」' to "\u2834\u2802", // 」
            '〈' to "\u2810\u2836", // 〈
            '〉' to "\u2836\u2802", // 〉
            '―' to "\u2824\u2824", // ―
            '-' to "\u2824",
            '~' to "\u2808\u2814"
        )

        fun isPunctuationMark(letter: Char): Boolean {
            return brailleMap.containsKey(letter)
        }
    }

    override fun toString(): String {
        return brailleMap[letter] ?: letter.toString()
    }
}
