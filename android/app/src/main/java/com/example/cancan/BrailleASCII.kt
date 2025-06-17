package com.example.cancan

object BrailleASCII {

    // SimBraille ASCII 대응 문자 (예: "a" → ⠁)
    private const val brailleASCII = " a1b'k2l@cif/msp\"e3h9o6r^djg>ntq,*5<-u8v.%[$+x!&;:4\\0z7(_?w]#y)="
    private const val brailleASCIItoUnicode = "⠀⠁⠂⠃⠄⠅⠆⠇⠈⠉⠊⠋⠌⠍⠎⠏⠐⠑⠒⠓⠔⠕⠖⠗⠘⠙⠚⠛⠜⠝⠞⠟⠠⠡⠢⠣⠤⠥⠦⠧⠨⠩⠪⠫⠬⠭⠮⠯⠰⠱⠲⠳⠴⠵⠶⠷⠸⠹⠺⠻⠼⠽⠾⠿"

    private fun tryGetUnicode(ch: Char): Char {
        val index = brailleASCII.indexOf(ch)
        return if (index != -1) brailleASCIItoUnicode[index] else ch
    }

    fun toUnicode(input: String): String {
        return input
            .lowercase()
            .replace(' ', '⠀') // 공백은 점자 공백으로
            .map { tryGetUnicode(it) }
            .joinToString("")
    }

    private fun tryGetASCII(ch: Char): Char {
        val index = brailleASCIItoUnicode.indexOf(ch)
        return if (index != -1) brailleASCII[index] else ch
    }

    fun fromUnicode(brailleUnicode: String): String {
        return brailleUnicode
            .map { tryGetASCII(it) }
            .joinToString("")
    }
}
