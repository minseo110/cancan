package com.example.cancan



class JumjaroConverter {

    /**
     * 하나의 점자 청크를 나타내는 데이터 클래스
     * @param sourceText 음성으로 읽힐 원문 문자열 (약어 또는 하나의 음절)
     * @param dotArrays 해당 청크에 대응하는 점자 6점 벡터 리스트
     */
    data class BrailleChunk(
        val sourceText: String,
        val dotArrays: List<List<Int>>
    )

    private val hangul = Hangul()
    private var characterMode = CharacterMode.None

    private val rule17StartChars = setOf('나', '다', '마', '바', '자', '카', '타', '파', '하', '따', '빠', '짜')
    private val attachmentMark = '⠤'
    private val rule10Nucleuses = setOf('ㅑ', 'ㅘ', 'ㅜ', 'ㅝ')
    private val mustSpacingOnsetsAfterNumber = setOf('ㄴ', 'ㄷ', 'ㅁ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ')

    private val acronyms = mapOf(
        "그래서" to "⠁⠎",
        "그러나" to "⠁⠉",
        "그러면" to "⠁⠒",
        "그러므로" to "⠁⠢",
        "그런데" to "⠁⠝",
        "그리고" to "⠁⠥",
        "그리하여" to "⠁⠱"
    )

    private fun changeMode(mode: CharacterMode, sb: StringBuilder) {
        if (characterMode == mode) return
        characterMode = mode
        if (characterMode == CharacterMode.Number) {
            sb.append('⠼')
        }
    }

    private fun resetNumberMode() {
        if (characterMode == CharacterMode.Number) {
            characterMode = CharacterMode.None
        }
    }

    /**
     * 문자열 내부의 한 글자씩 변환 (한글, 숫자, 문장부호 처리)
     */
    private fun convertAsChar(str: String): String {
        val sb = StringBuilder()
        for (i in str.indices) {
            val ch = str[i]
            when {
                hangul.isHangulCharacter(ch) -> {
                    if (characterMode == CharacterMode.Number) {
                        val onset = hangul.syllabification(ch, onset = true, nucleus = false, coda = false)?.get(0)
                        if (ch == '운' || (onset != null && mustSpacingOnsetsAfterNumber.contains(onset))) {
                            sb.append('⠀')
                        }
                    }
                    changeMode(CharacterMode.Hangul, sb)

                    val nextChar = str.getOrNull(i + 1)
                    if (nextChar == null || !hangul.isHangulCharacter(nextChar)) {
                        sb.append(HangulBraille(ch).toString())
                        continue
                    }

                    val nextCharWithoutCoda = hangul.removeCoda(nextChar)
                    if (!hangul.hasCoda(ch) && nextCharWithoutCoda == '예') {
                        sb.append(HangulBraille(ch).toString())
                        sb.append(attachmentMark)
                        continue
                    }

                    if (!hangul.hasCoda(ch) && nextCharWithoutCoda == '애') {
                        val syll = hangul.syllabification(ch, onset = false, nucleus = true, coda = false)
                        if (syll != null && rule10Nucleuses.contains(syll[1])) {
                            sb.append(HangulBraille(ch).toString())
                            sb.append(attachmentMark)
                            continue
                        }
                    }

                    if (rule17StartChars.contains(ch)) {
                        val syll = hangul.syllabification(nextChar, onset = true, nucleus = false, coda = false)
                        if (syll != null && syll[0] == 'ㅇ') {
                            sb.append(HangulBraille(ch).toStringWithoutRules())
                            continue
                        }
                    }

                    sb.append(HangulBraille(ch).toString())
                }
                ch.isDigit() -> {
                    changeMode(CharacterMode.Number, sb)
                    sb.append(NumberArithmeticBraille(ch).toString())
                }
                PunctuationMarkBraille.isPunctuationMark(ch) -> {
                    sb.append(PunctuationMarkBraille(ch).toString())
                }
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * 단어 단위 변환 (약어 우선 처리)
     */
    private fun convertAsWord(word: String): String {
        val sb = StringBuilder()
        val matched = acronyms.entries.find { word.startsWith(it.key) }
        if (matched != null) {
            sb.append(matched.value)
            val remaining = word.removePrefix(matched.key)
            if (remaining.isNotEmpty()) sb.append(convertAsChar(remaining))
        } else {
            sb.append(convertAsChar(word))
        }
        resetNumberMode()
        return sb.toString()
    }

    /**
     * 기존 toJumja 로직 유지
     */
    fun toJumja(input: String): String {
        return input.split(" ").joinToString("\u2800") { word ->
            if (word.contains("\n")) {
                word.split("\n").joinToString("\n") { convertAsWord(it) }
            } else {
                convertAsWord(word)
            }
        }
    }

    /**
     * 약어/음절 단위 매핑 정보까지 포함한 점자 청크 리스트 반환
     */
    fun toBrailleChunks(input: String): List<BrailleChunk> {
        val result = mutableListOf<BrailleChunk>()
        var i = 0
        while (i < input.length) {
            val remaining = input.substring(i)
            // 약어 매핑 우선
            val acronymEntry = acronyms.entries.find { remaining.startsWith(it.key) }
            if (acronymEntry != null) {
                val unicodeStr = acronymEntry.value
                val dots = unicodeStr.map { Braille(it).toDotArray() }
                result.add(BrailleChunk(acronymEntry.key, dots))
                i += acronymEntry.key.length
                continue
            }
            val ch = input[i]
            when {
                hangul.isHangulCharacter(ch) -> {
                    val unicodeStr = HangulBraille(ch).toString()
                    val dots = unicodeStr.map { Braille(it).toDotArray() }
                    result.add(BrailleChunk(ch.toString(), dots))
                }
                ch.isDigit() -> {
                    val unicodeStr = NumberArithmeticBraille(ch).toString()
                    val dots = unicodeStr.map { Braille(it).toDotArray() }
                    result.add(BrailleChunk(ch.toString(), dots))
                }
                PunctuationMarkBraille.isPunctuationMark(ch) -> {
                    val unicodeStr = PunctuationMarkBraille(ch).toString()
                    val dots = unicodeStr.map { Braille(it).toDotArray() }
                    result.add(BrailleChunk(ch.toString(), dots))
                }
                else -> { /* 공백 등 기타 문자는 무시 */ }
            }
            i++
        }
        return result
    }

    /**
     * 규칙 없이 순수 변환된 결과
     */
    fun toJumjaWithoutRules(input: String): String {
        val sb = StringBuilder()
        for (ch in input) {
            if (hangul.isHangulCharacter(ch)) {
                changeMode(CharacterMode.Hangul, sb)
                sb.append(HangulBraille(ch).toStringWithoutRules())
            } else if (ch.isDigit()) {
                changeMode(CharacterMode.Number, sb)
                sb.append(NumberArithmeticBraille(ch).toStringWithoutRules())
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }
}
