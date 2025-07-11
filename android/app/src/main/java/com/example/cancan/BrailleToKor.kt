package com.example.cancan

import com.example.cancan.BrailleData.CHO_braille
import com.example.cancan.BrailleData.abb_CHO_braille
import com.example.cancan.BrailleData.double_CHO_braille
import com.example.cancan.BrailleData.JUNG_braille
import com.example.cancan.BrailleData.double_JUNG_braille
import com.example.cancan.BrailleData.JONG_braille
import com.example.cancan.BrailleData.double_JONG_braille
import com.example.cancan.BrailleData.abb_word_dict
import com.example.cancan.BrailleData.abb_cho_dict
import com.example.cancan.BrailleData.abb_jung_jong_dict
import com.example.cancan.BrailleData.abb_cho_jung_jong_dict
import com.example.cancan.KorData.CHO
import com.example.cancan.KorData.JONG
import com.example.cancan.KorData.JUNG
import com.example.cancan.NumberFunc.translateNumber
import com.example.cancan.PunctuationFunc.translatePunc

class BrailleToKor {

    val brailles = listOf(
        '⠀','⠮','⠐','⠼','⠫','⠩','⠯','⠄','⠷','⠾','⠡','⠬','⠠','⠤','⠨','⠌','⠴','⠂','⠆','⠒','⠲','⠢',
        '⠖','⠶','⠦','⠔','⠱','⠰','⠣','⠿','⠜','⠹','⠈','⠁','⠃','⠉','⠙','⠑','⠋','⠛','⠓','⠊','⠚','⠅',
        '⠇','⠍','⠝','⠕','⠏','⠟','⠗','⠎','⠞','⠥','⠧','⠺','⠭','⠽','⠵','⠪','⠳','⠻','⠘','⠸'
    )

    // 생성자
    constructor()

    // 점자인지 확인하는 함수
    fun isBraille(input: String): Boolean {
        for (i in input) {
            if (brailles.contains(i)) {
                return true
            } else {
                return false
            }
        }
        return false
    }

    // 초성 + 중성 (+ 종성)을 합쳐 하나의 음절을 반환
    fun jamoCombination(c1: String, c2: String, c3: String): Char {
        var choI = 0
        var jungI = 0
        var jongI = 0

        for (i in CHO.indices) {
            if (CHO[i] == c1) choI = i
        }
        val choValue = choI * 21 * 28

        for (i in JUNG.indices) {
            if (JUNG[i] == c2) jungI = i
        }
        val jungValue = jungI * 28

        for (i in JONG.indices) {
            if (JONG[i] == c3) jongI = i
        }
        val jongValue = if (jongI == 0) 0 else jongI

        val uniValue = choValue + jungValue + jongValue + 0xAC00
        return uniValue.toChar()
    }

    // 끊은 '단어' 점자를 한글로 바꾸는 역할
    fun brailleToSyllable(word: String): String {
        var brailleWord = word
        var abbBraille = ""
        var abbKor = ""
        var wordResult = ""

        var cho = ""
        var jung = ""
        var jong = ""

        var selectedCho = false
        var selectedJung = false
        var selectedJong = false

        var yeJong = false
        var flag14 = false

        var last = brailleWord.length - 1

        // 단어 자체가 약어라면 바로 점자로 번역해서 리턴
        if (abb_word_dict.containsKey(word)) {
            wordResult += abb_word_dict[word]
            return wordResult
        } else {
            for ((key, value) in abb_word_dict) {
                if (word.contains(key)) {
                    abbBraille = key
                    abbKor = value
                }
            }
            if (abbBraille != "") {
                if (word[0] != abbBraille[0]) {
                    // 종성이 가장 먼저 오는 경우 X
                } else {
                    wordResult += abbKor
                    brailleWord = brailleWord.replace(abbBraille, "")
                    last = brailleWord.length - 1
                }
            }
        }

        var i = 0
        while (i < brailleWord.length) {
            var letterFront = ""
            val letter = brailleWord[i].toString()
            var letterBack = ""
            var letterBackBack = ""

            val letterIsBraille = isBraille(letter)
            var letterBackIsBraille = false
            var letterBackBackIsBraille = false

            if (!letterIsBraille) {
                wordResult += letter
                i++
                continue
            }

            if (i > 0) letterFront = brailleWord[i - 1].toString()
            if (i < last) {
                letterBack = brailleWord[i + 1].toString()
                letterBackIsBraille = isBraille(letterBack)
            }
            if (i < last - 1) {
                letterBackBack = brailleWord[i + 2].toString()
                letterBackBackIsBraille = isBraille(letterBackBack)
            }

            if (letter == "⠤") {
                cho = ""
                jung = ""
                jong = ""
                selectedCho = false
                selectedJung = false
                selectedJong = false
                i++
                continue
            }

            if (flag14) {
                flag14 = false
                i++
                continue
            }

            if (letter == "⠠" && abb_cho_jung_jong_dict.containsKey(letterBack + letterBackBack)) {
                cho = "ㄲ"
                jung = "ㅓ"
                jong = "ㅅ"
                selectedCho = true
                selectedJung = true
                selectedJong = true
                flag14 = true
                i++
                continue
            } else if (!selectedCho && abb_cho_jung_jong_dict.containsKey(letter + letterBack)) {
                cho = "ㄱ"
                jung = "ㅓ"
                jong = "ㅅ"
                selectedCho = true
                selectedJung = true
                selectedJong = true
                i++
                continue
            }

            if (yeJong) {
                yeJong = false
                i++
                continue
            }

            // 초성 처리
            if (!selectedCho) {
                if (JUNG_braille.containsKey(letter)) {
                    cho = "ㅇ"
                    selectedCho = true
                    selectedJung = true

                    if (i < last && double_JUNG_braille.containsKey(letter + letterBack)) {
                        jung = double_JUNG_braille[letter + letterBack] ?: ""
                        if (i < last - 1 && !JONG_braille.containsKey(letterBackBack)) {
                            jong = " "
                            selectedJong = true
                        }
                        i++
                        continue
                    } else {
                        jung = JUNG_braille[letter] ?: ""
                    }

                    if (i < last && !JONG_braille.containsKey(letterBack)) {
                        jong = " "
                        selectedJong = true
                    } else {
                        i++
                        continue
                    }
                } else if (abb_jung_jong_dict.containsKey(letter)) {
                    cho = "ㅇ"
                    selectedCho = true
                    jung = abb_jung_jong_dict[letter]?.get(0) ?: ""
                    jong = abb_jung_jong_dict[letter]?.get(1) ?: ""
                    selectedJung = true
                    selectedJong = true
                    if (i < last && JONG_braille.containsKey(letterBack)) {
                        jong = double_JONG_braille[jong + letterBack] ?: " "
                        i++
                        continue
                    }
                } else if (abb_CHO_braille.containsKey(letter) &&
                    ((i < last && (CHO_braille.containsKey(letterBack)
                            || JONG_braille.containsKey(letterBack)
                            || letterBack == "⠫" || letterBack == "⠇" || letterBack == "⠤"))
                            || i == last || !letterBackIsBraille)
                ) {
                    cho = CHO_braille[letter] ?: ""
                    jung = "ㅏ"
                    selectedCho = true
                    selectedJung = true

                    if (letterBack == "⠌") {
                        if (i < last - 1 && JONG_braille.containsKey(letterBackBack)) {
                            jung = "ㅖ"
                            yeJong = true
                            i++
                            continue
                        }
                        if (cho == "ㅎ") {
                            jung = "ㅖ"
                            jong = " "
                        } else {
                            jung = "ㅏ"
                            jong = "ㅆ"
                        }
                        selectedJong = true
                        i++
                        continue
                    } else if (letterBack == "⠤") {
                        jong = " "
                        selectedJong = true
                    } else if (!JONG_braille.containsKey(letterBack)) {
                        jong = " "
                        selectedJong = true
                    }
                } else if (abb_cho_dict.containsKey(letter)) {
                    cho = abb_cho_dict[letter]?.get(0) ?: ""
                    jung = "ㅏ"
                    selectedCho = true
                    selectedJung = true

                    if (letterBack == "⠌") {
                        jung = "ㅏ"
                        jong = "ㅆ"
                        selectedJong = true
                        i++
                        continue
                    } else if (!JONG_braille.containsKey(letterBack)) {
                        jong = " "
                        selectedJong = true
                    }
                } else if (letter == "⠠") {
                    if (letterBack in BrailleData.abb_CHO_braille.keys &&
                        ((i < last - 1 && (CHO_braille.containsKey(letterBackBack)
                                || JONG_braille.containsKey(letterBackBack)
                                || letterBackBack == "⠫" || letterBackBack == "⠇"))
                                || i + 1 == last || !letterBackBackIsBraille)
                    ) {
                        cho = CHO_braille[letter + letterBack] ?: " "
                        jung = "ㅏ"
                        selectedCho = true
                        selectedJung = true
                        if (letterBackBack == "⠌") {
                            jong = "ㅆ"
                            i++
                            continue
                        } else if (!JONG_braille.containsKey(letterBackBack)) {
                            jong = " "
                            selectedJong = true
                        }
                    } else if (abb_cho_dict.containsKey(letter + letterBack)) {
                        cho = abb_cho_dict[letter + letterBack]?.get(0) ?: " "
                        jung = abb_cho_dict[letter + letterBack]?.get(1) ?: " "
                        selectedCho = true
                        selectedJung = true
                        if (letterBackBack == "⠌") {
                            jong = "ㅆ"
                        }
                        if (!JONG_braille.containsKey(letterBackBack)) {
                            jong = " "
                            selectedJong = true
                        }
                    } else if (!CHO_braille.containsKey(letterBack)) {
                        cho = CHO_braille[letter] ?: " "
                        selectedCho = true
                    } else if (CHO_braille.containsKey(letterBack)) {
                        cho = double_CHO_braille[letter + letterBack] ?: " "
                        selectedCho = true
                    }
                    i++
                    continue
                } else if (CHO_braille.containsKey(letter)) {
                    cho = CHO_braille[letter] ?: ""
                    selectedCho = true
                } else {
                    i++
                    continue
                }
            }

            // 중성 처리
            if (selectedCho && !selectedJung) {
                if (abb_jung_jong_dict.containsKey(letter)) {
                    jung = abb_jung_jong_dict[letter]?.get(0) ?: ""
                    jong = abb_jung_jong_dict[letter]?.get(1) ?: ""
                    selectedJung = true
                    selectedJong = true
                    if ((jong + letterBack) in double_JONG_braille.keys) {
                        jong = double_JONG_braille[jong + letterBack] ?: ""
                        i++
                        continue
                    }
                } else if (JUNG_braille.containsKey(letter)) {
                    jung = JUNG_braille[letter] ?: ""
                    selectedJung = true
                    if ((letter + letterBack) in double_JUNG_braille.keys) {
                        jung = double_JUNG_braille[letter + letterBack] ?: ""
                        if (!JONG_braille.containsKey(letterBackBack)) {
                            jong = " "
                            selectedJong = true
                        }
                        i++
                        continue
                    } else if (!JONG_braille.containsKey(letterBack)) {
                        jong = " "
                        selectedJong = true
                    } else if (JONG_braille.containsKey(letterBack)) {
                        i++
                        continue
                    }
                }
            }

            // 종성 처리
            if (selectedCho && selectedJung && !selectedJong) {
                if (JONG_braille.containsKey(letter)) {
                    jong = JONG_braille[letter] ?: ""
                    selectedJong = true
                    if ((jong + letterBack) in double_JONG_braille.keys) {
                        jong = double_JONG_braille[jong + letterBack] ?: ""
                        i++
                        continue
                    }
                }
            }

            // 음절 완성
            if (selectedCho && selectedJung && selectedJong) {
                if ((cho == "ㅅ" || cho == "ㅆ" || cho == "ㅈ" || cho == "ㅉ" || cho == "ㅊ") && jung == "ㅕ" && jong == "ㅇ") {
                    jung = "ㅓ"
                }
                wordResult += jamoCombination(cho, jung, jong)
                selectedCho = false
                selectedJung = false
                selectedJong = false
                cho = ""
                jung = ""
                jong = ""
            }
            i++
        }
        return wordResult
    }

    // 전체 문장 번역
    fun translation(input: String): String {
        var result = ""
        // 숫자 번역
        var inputStr = translateNumber(input)
        // 문장부호 번역
        val punctuationTranslatedWords = translatePunc(inputStr.replace('⠀', ' ').split(" ").toMutableList())

        for (word in punctuationTranslatedWords) {
            var replacedWord = word
            var replace123456Flag = false
            var replace1245Flag = false
            if (word.contains("⠛⠛")) {
                replacedWord = word.replace("⠛⠛", "")
                replace1245Flag = true
            }
            if (replace1245Flag) {
                replacedWord = replacedWord.replace("⠛", "")
                replace1245Flag = false
            }
            if (word.contains("⠿⠿")) {
                replacedWord = word.replace("⠿⠿", "")
                replace123456Flag = true
            }
            if (replace123456Flag) {
                replacedWord = replacedWord.replace("⠿", "")
                replace123456Flag = false
            }
            result += brailleToSyllable(replacedWord)
            result += " "
        }
        return result
    }
}
