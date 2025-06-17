package com.example.cancan

import java.lang.StringBuilder

class HangulBraille {

    private val letter: Char
    private val onset: Char
    private val nucleus: Char
    private val coda: Char

    constructor(hangulCharacter: Char) {
        val hangul = Hangul()
        val syllables = hangul.syllabification(hangulCharacter) ?: arrayOf('\u0000', '\u0000', '\u0000')
        onset = syllables[0]
        nucleus = syllables[1]
        coda = syllables[2]
        letter = hangulCharacter
    }

    constructor(onset: Char = '\u0000', nucleus: Char = '\u0000', coda: Char = '\u0000') {
        this.letter = Hangul().joinSyllables(onset, nucleus, coda)
        this.onset = onset
        this.nucleus = nucleus
        this.coda = coda
    }

    companion object {
        val onsets = arrayOf(
            "\u2808", "\u2820\u2808", "\u2809", "\u280A", "\u2820\u280A", "\u2810", "\u2811",
            "\u2818", "\u2820\u2818", "\u2820", "\u2820\u2820", "\u281B", "\u2828", "\u2820\u2828",
            "\u2830", "\u280B", "\u2813", "\u2819", "\u281A"
        )

        val noAbbrDoubleOnsetMap = mapOf(
            'ㄸ' to 'ㄷ',
            'ㅃ' to 'ㅂ',
            'ㅉ' to 'ㅈ'
        )

        val nucleuses = arrayOf(
            "\u2823", "\u2817", "\u281C", "\u281C\u2817", "\u281E", "\u281D", "\u2831", "\u280C",
            "\u2825", "\u2827", "\u2827\u2817", "\u283D", "\u282C", "\u280D", "\u280F", "\u280F\u2817",
            "\u280D\u2817", "\u2829", "\u282A", "\u283A", "\u2815"
        )

        val codas = arrayOf(
            null, "\u2801", "\u2801\u2801", "\u2801\u2804", "\u2812", "\u2812\u2805", "\u2812\u2834",
            "\u2814", "\u2802", "\u2802\u2801", "\u2802\u2822", "\u2802\u2803", "\u2802\u2804",
            "\u2802\u2826", "\u2802\u2832", "\u2802\u2834", "\u2822", "\u2803", "\u2803\u2804",
            "\u2804", "\u280C", "\u2836", "\u2805", "\u2806", "\u2816", "\u2826", "\u2832", "\u2834"
        )

        val abbreviations = mapOf(
            HangulAbbrMask('ㄱ', 'ㅏ') to "\u282B",
            HangulAbbrMask('ㄴ', 'ㅏ') to "\u2809",
            HangulAbbrMask('ㄷ', 'ㅏ') to "\u280A",
            HangulAbbrMask('ㅁ', 'ㅏ') to "\u2811",
            HangulAbbrMask('ㅂ', 'ㅏ') to "\u2818",
            HangulAbbrMask('ㅅ', 'ㅏ') to "\u2807",
            HangulAbbrMask('ㅈ', 'ㅏ') to "\u2828",
            HangulAbbrMask('ㅋ', 'ㅏ') to "\u280B",
            HangulAbbrMask('ㅌ', 'ㅏ') to "\u2813",
            HangulAbbrMask('ㅍ', 'ㅏ') to "\u2819",
            HangulAbbrMask('ㅎ', 'ㅏ') to "\u281A",
            HangulAbbrMask('ㄱ', 'ㅓ', 'ㅅ') to "\u2838\u280E",
            HangulAbbrMask('ㄲ', 'ㅏ') to "\u2820\u282B",
            HangulAbbrMask('ㅆ', 'ㅏ') to "\u2820\u2807",
            HangulAbbrMask('ㄲ', 'ㅓ', 'ㅅ') to "\u2820\u2838\u280E",
            HangulAbbrMask('ㅅ', 'ㅓ', 'ㅇ') to "\u2820\u283B",
            HangulAbbrMask('ㅆ', 'ㅓ', 'ㅇ') to "\u2820\u2820\u283B",
            HangulAbbrMask('ㅈ', 'ㅓ', 'ㅇ') to "\u2828\u283B",
            HangulAbbrMask('ㅉ', 'ㅓ', 'ㅇ') to "\u2820\u2828\u283B",
            HangulAbbrMask('ㅊ', 'ㅓ', 'ㅇ') to "\u2830\u283B"
        )

        val abbreviationsWithoutOnset = mapOf(
            HangulAbbrMask(nucleus = 'ㅓ', coda = 'ㄱ') to "\u2839",
            HangulAbbrMask(nucleus = 'ㅓ', coda = 'ㄴ') to "\u283E",
            HangulAbbrMask(nucleus = 'ㅓ', coda = 'ㄹ') to "\u281E",
            HangulAbbrMask(nucleus = 'ㅕ', coda = 'ㄴ') to "\u2821",
            HangulAbbrMask(nucleus = 'ㅕ', coda = 'ㄹ') to "\u2833",
            HangulAbbrMask(nucleus = 'ㅕ', coda = 'ㅇ') to "\u283B",
            HangulAbbrMask(nucleus = 'ㅗ', coda = 'ㄱ') to "\u282D",
            HangulAbbrMask(nucleus = 'ㅗ', coda = 'ㄴ') to "\u2837",
            HangulAbbrMask(nucleus = 'ㅗ', coda = 'ㅇ') to "\u283F",
            HangulAbbrMask(nucleus = 'ㅜ', coda = 'ㄴ') to "\u281B",
            HangulAbbrMask(nucleus = 'ㅜ', coda = 'ㄹ') to "\u282F",
            HangulAbbrMask(nucleus = 'ㅡ', coda = 'ㄴ') to "\u2835",
            HangulAbbrMask(nucleus = 'ㅡ', coda = 'ㄹ') to "\u282E",
            HangulAbbrMask(nucleus = 'ㅣ', coda = 'ㄴ') to "\u283F"
        )
    }

    override fun toString(): String {
        val hangul = Hangul()
        val sb = StringBuilder()
        val onsetBase = hangul.syllabification(letter, onset = true, nucleus = false, coda = false)?.get(0)

        // 된소리 표기
        if (onsetBase != null && noAbbrDoubleOnsetMap.containsKey(onsetBase)) {
            val nucleusCoda = hangul.syllabification(letter, onset = false, nucleus = true, coda = true)
            return '\u2820' + HangulBraille(
                noAbbrDoubleOnsetMap[onsetBase]!!,
                nucleusCoda?.get(1) ?: '\u0000',
                nucleusCoda?.get(2) ?: '\u0000'
            ).toString()
        }

        // 받침 ㅆ 예외 처리
        if (hangul.syllabification(letter, onset = false, nucleus = false, coda = true)?.get(2) == 'ㅆ') {
            if (letter == '팠') {
                return HangulBraille('파').toStringWithoutRules() + '\u280C'
            }
            return HangulBraille(hangul.removeCoda(letter)).toString() + '\u280C'
        }

        // 약자 처리
        val processedAbbr = abbreviations.entries.find { it.key.isMatch(letter) }
        if (processedAbbr != null) {
            sb.append(processedAbbr.value)
            val remaining = processedAbbr.key.subtractIfMatched(letter)
            sb.append(HangulBraille(remaining[0], remaining[1], remaining[2]).toStringWithoutRules())
            return sb.toString()
        }

        if (onset != '\u0000' && onset != 'ㅇ') {
            sb.append(onsets[hangul.findIndexOfOnset(onset)])
        }

        sb.append(convertNucleusAndCoda(nucleus, coda))
        return sb.toString()
    }

    fun toStringWithoutRules(): String {
        val hangul = Hangul()
        val sb = StringBuilder()
        if (onset != '\u0000') sb.append(onsets[hangul.findIndexOfOnset(onset)])
        if (nucleus != '\u0000') sb.append(nucleuses[hangul.findIndexOfNucleus(nucleus)])
        if (coda != '\u0000') sb.append(codas[hangul.findIndexOfCoda(coda)])
        return sb.toString()
    }

    private fun convertNucleusAndCoda(nucleus: Char, coda: Char): String {
        val matched = abbreviationsWithoutOnset.entries.find {
            it.key.isMatch('\u0000', nucleus, coda)
        }
        val sb = StringBuilder()
        if (matched != null) {
            sb.append(matched.value)
            val (on, nu, co) = matched.key.subtractIfMatched('\u0000', nucleus, coda)
            if (nu != '\u0000') sb.append(nucleuses[Hangul().findIndexOfNucleus(nu)])
            if (co != '\u0000') sb.append(convertCoda(co))
        } else {
            if (nucleus != '\u0000') sb.append(nucleuses[Hangul().findIndexOfNucleus(nucleus)])
            if (coda != '\u0000') sb.append(convertCoda(coda))
        }
        return sb.toString()
    }

    private fun convertCoda(coda: Char): String {
        return if (coda != '\u0000') {
            codas[Hangul().findIndexOfCoda(coda)] ?: ""
        } else {
            ""
        }
    }
}
