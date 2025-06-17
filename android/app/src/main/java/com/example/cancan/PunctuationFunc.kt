package com.example.cancan

object PunctuationFunc {

    val front_punctuation_list = mapOf(
        "⠸⠌" to "/", "⠦" to "\"", "⠠⠦" to "'", "⠦⠄" to "(", "⠦⠂" to "{",
        "⠦⠆" to "[", "⠐⠦" to "〈", "⠔⠔" to "*", "⠰⠆" to "〃",
        "⠈⠺" to "₩", "⠈⠙" to "$", "⠠⠄" to " "
    )

    val middle_punctuation_list = mapOf(
        "⠐⠆" to "·", "⠐⠂" to ":", "⠦⠄" to "(", "⠠⠴" to ")", "⠦⠂" to "{",
        "⠐⠴" to "}", "⠦⠆" to "[", "⠰⠴" to "]", "⠐⠦" to "〈", "⠴⠂" to "〉",
        "⠤" to "-", "⠤⠤" to "~", "⠸⠌" to "/", "⠠⠦" to "'", "⠴⠄" to "'",
        "⠠⠠⠠" to "...", "⠔⠔" to "*", "⠰⠆" to "〃", "⠈⠺" to "₩", "⠈⠙" to "$"
    )

    val end_punctuation_list = mapOf(
        "⠲" to ".", "⠦" to "?", "⠖" to "!", "⠐" to ",", "⠐⠂" to ":", "⠴" to "\"",
        "⠠⠴" to ")", "⠐⠴" to "}", "⠰⠴" to "]", "⠤⠤" to "~", "⠠⠠⠠" to "...",
        "⠸⠌" to "/", "⠔⠔" to "*", "⠰⠆" to "〃", "⠈⠺" to "₩", "⠈⠙" to "$", "⠠⠄" to " "
    )

    fun translatePunc(words: MutableList<String>): List<String> {
        val stringWithTranslatedPunc = words
        for ((index, word) in words.withIndex()) {
            var wordArr = word
            wordArr = translateMiddlePunc(translateLastPunc(translateFirstPunc(wordArr)))
            stringWithTranslatedPunc[index] = wordArr
        }
        return stringWithTranslatedPunc
    }

    fun translateFirstPunc(word: String): String {
        if (word.isEmpty()) return word
        val resultWord = word.toMutableList()
        val firstWord = word.getOrNull(0)?.toString() ?: ""
        val secondWord = word.getOrNull(1)?.toString() ?: " "

        val combined = firstWord + secondWord
        when {
            front_punctuation_list.containsKey(combined) -> {
                val punctuation = front_punctuation_list[combined] ?: ""
                resultWord.removeAt(0)
                if (punctuation == " ") {
                    if (resultWord.isNotEmpty()) resultWord.removeAt(0)
                    return translateFirstPunc(resultWord.joinToString(""))
                } else {
                    resultWord[0] = punctuation[0]
                }
            }
            front_punctuation_list.containsKey(firstWord) -> {
                val punctuation = front_punctuation_list[firstWord] ?: ""
                resultWord[0] = punctuation[0]
            }
        }
        return resultWord.joinToString("")
    }

    fun translateMiddlePunc(word: String): String {
        val resultWord = word.toMutableList()
        var index = 0
        while (index < resultWord.size) {
            val oneWord = resultWord[index].toString()
            val backIndexWord = resultWord.getOrNull(index + 1)?.toString() ?: " "
            val backBackIndexWord = resultWord.getOrNull(index + 2)?.toString() ?: " "

            when {
                middle_punctuation_list.containsKey(oneWord + backIndexWord + backBackIndexWord) -> {
                    resultWord[index] = '.'
                    resultWord[index + 1] = '.'
                    resultWord[index + 2] = '.'
                }
                middle_punctuation_list.containsKey(oneWord + backIndexWord) -> {
                    val punctuation = middle_punctuation_list[oneWord + backIndexWord]!!
                    resultWord[index] = punctuation[0]
                }
                middle_punctuation_list.containsKey(oneWord) -> {
                    val punctuation = middle_punctuation_list[oneWord]!!
                    resultWord[index] = punctuation[0]
                }
            }
            index++
        }
        return resultWord.joinToString("")
    }

    fun translateLastPunc(word: String): String {
        val resultWord = word.toMutableList()
        val wordCount = resultWord.size
        if (wordCount == 0) return ""

        val lastWord = word.getOrNull(wordCount - 1)?.toString() ?: ""
        val frontWord = word.getOrNull(wordCount - 2)?.toString() ?: " "
        val frontFrontWord = word.getOrNull(wordCount - 3)?.toString() ?: " "

        when {
            end_punctuation_list.containsKey(frontFrontWord + frontWord + lastWord) -> {
                resultWord[wordCount - 1] = '.'
                resultWord[wordCount - 2] = '.'
                resultWord[wordCount - 3] = '.'
            }
            end_punctuation_list.containsKey(lastWord) &&
                    (end_punctuation_list[lastWord] == "\"" || end_punctuation_list[lastWord] == "'") &&
                    end_punctuation_list[frontWord] in listOf(".", ",") -> {
                resultWord[wordCount - 2] = end_punctuation_list[frontWord]!![0]
                resultWord[wordCount - 1] = end_punctuation_list[lastWord]!![0]
            }
            end_punctuation_list.containsKey(frontWord + lastWord) -> {
                val punctuation = end_punctuation_list[frontWord + lastWord]
                if (punctuation == " ") {
                    val trimmed = resultWord.dropLast(2).joinToString("")
                    return translateLastPunc(trimmed)
                } else {
                    resultWord[wordCount - 1] = punctuation!![0]
                }
            }
            end_punctuation_list.containsKey(lastWord) -> {
                val punctuation = end_punctuation_list[lastWord]!!
                resultWord.removeAt(wordCount - 1)
                resultWord.addAll(punctuation.toList())
            }
        }
        return resultWord.joinToString("")
    }

}