package com.example.cancan

class HangulAbbrMask(
    private val onset: Char = '\u0000',
    private val nucleus: Char = '\u0000',
    private val coda: Char = '\u0000'
) {

    companion object {
        private val doubleCodaMap = mapOf(
            'ㄲ' to arrayOf('ㄱ', 'ㄱ'),
            'ㄳ' to arrayOf('ㄱ', 'ㅅ'),
            'ㄵ' to arrayOf('ㄴ', 'ㅈ'),
            'ㄶ' to arrayOf('ㄴ', 'ㅎ'),
            'ㄺ' to arrayOf('ㄹ', 'ㄱ'),
            'ㄻ' to arrayOf('ㄹ', 'ㅁ'),
            'ㄼ' to arrayOf('ㄹ', 'ㅂ'),
            'ㄽ' to arrayOf('ㄹ', 'ㅅ'),
            'ㄾ' to arrayOf('ㄹ', 'ㅌ'),
            'ㄿ' to arrayOf('ㄹ', 'ㅍ'),
            'ㅀ' to arrayOf('ㄹ', 'ㅎ'),
            'ㅄ' to arrayOf('ㅂ', 'ㅅ')
        )
    }

    fun isMatch(hangulLetter: Char): Boolean {
        val (on, nu, co) = Hangul().syllabification(hangulLetter) ?: return false
        return isMatch(on, nu, co)
    }

    fun isMatch(on: Char, nu: Char, co: Char): Boolean {
        var matched = true
        if (onset != '\u0000') {
            matched = matched && (onset == on)
        }
        if (nucleus != '\u0000') {
            matched = matched && (nucleus == nu)
        }
        if (coda != '\u0000') {
            val codas = dissembleCoda(co)
            matched = matched && (coda == codas[0])
        }
        return matched
    }

    fun subtractIfMatched(hangulLetter: Char): Array<Char> {
        val (on, nu, co) = Hangul().syllabification(hangulLetter) ?: return arrayOf('\u0000', '\u0000', '\u0000')
        return subtractIfMatched(on, nu, co)
    }

    fun subtractIfMatched(on: Char, nu: Char, co: Char): Array<Char> {
        if (isMatch(on, nu, co)) {
            var newOn = on
            var newNu = nu
            var newCo = co

            if (onset != '\u0000') newOn = '\u0000'
            if (nucleus != '\u0000') newNu = '\u0000'
            if (coda != '\u0000') {
                val codas = dissembleCoda(newCo)
                newCo = if (codas.size == 2) codas[1] else '\u0000'
            }

            return arrayOf(newOn, newNu, newCo)
        }
        return arrayOf(on, nu, co)
    }

    private fun dissembleCoda(coda: Char): Array<Char> {
        return doubleCodaMap[coda] ?: arrayOf(coda)
    }

    // Kotlin에서는 연산자 오버로딩을 아래와 같이 구현합니다.
    operator fun contains(hangulLetter: Char): Boolean {
        return isMatch(hangulLetter)
    }
}
