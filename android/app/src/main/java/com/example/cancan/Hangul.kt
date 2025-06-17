package com.example.cancan

class Hangul {

    private val onsets = arrayOf(
        'ㄱ','ㄲ','ㄴ','ㄷ','ㄸ','ㄹ','ㅁ','ㅂ','ㅃ','ㅅ','ㅆ','ㅇ','ㅈ',
        'ㅉ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'
    )

    private val nucleuses = arrayOf(
        'ㅏ','ㅐ','ㅑ','ㅒ','ㅓ','ㅔ','ㅕ','ㅖ','ㅗ','ㅘ','ㅙ','ㅚ','ㅛ',
        'ㅜ','ㅝ','ㅞ','ㅟ','ㅠ','ㅡ','ㅢ','ㅣ'
    )

    private val codas = arrayOf(
        '\u0000', 'ㄱ','ㄲ','ㄳ','ㄴ','ㄵ','ㄶ','ㄷ','ㄹ','ㄺ','ㄻ','ㄼ','ㄽ','ㄾ',
        'ㄿ','ㅀ','ㅁ','ㅂ','ㅄ','ㅅ','ㅆ','ㅇ','ㅈ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'
    )

    fun isHangulCharacter(ch: Char): Boolean {
        return ch in '가'..'힣'
    }

    fun findIndexOfOnset(ch: Char): Int {
        return onsets.indexOf(ch)
    }

    fun findIndexOfNucleus(ch: Char): Int {
        return nucleuses.indexOf(ch)
    }

    fun findIndexOfCoda(ch: Char): Int {
        return codas.indexOf(ch)
    }

    fun hasCoda(ch: Char): Boolean {
        val syllables = syllabification(ch, onset = false, nucleus = false, coda = true)
        return syllables?.get(2) != '\u0000'
    }

    fun removeCoda(ch: Char): Char {
        val syllables = syllabification(ch, onset = true, nucleus = true, coda = false)
            ?: throw IllegalArgumentException("$ch is not a Hangul character.")
        return joinSyllables(syllables[0], syllables[1])
    }

    fun syllabification(
        letter: Char,
        onset: Boolean = true,
        nucleus: Boolean = true,
        coda: Boolean = true
    ): Array<Char>? {
        if (!isHangulCharacter(letter)) return null

        val offset = letter.code - '가'.code
        val result = arrayOf('\u0000', '\u0000', '\u0000')

        if (onset) {
            result[0] = onsets[offset / (nucleuses.size * codas.size)]
        }
        if (nucleus) {
            result[1] = nucleuses[(offset / codas.size) % nucleuses.size]
        }
        if (coda) {
            result[2] = codas[offset % codas.size]
        }

        return result
    }

    fun joinSyllables(onset: Char, nucleus: Char, coda: Char = '\u0000'): Char {
        val onsetIndex = onsets.indexOf(onset)
        val nucleusIndex = nucleuses.indexOf(nucleus)
        val codaIndex = codas.indexOf(coda)

        val code = ((onsetIndex * nucleuses.size + nucleusIndex) * codas.size) + codaIndex + '가'.code
        return code.toChar()
    }
}
