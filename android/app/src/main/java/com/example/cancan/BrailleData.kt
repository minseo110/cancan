package com.example.cancan


object BrailleData {
    // 초성 점자
    val CHO_braille = mapOf(
        "⠈" to "ㄱ", "⠉" to "ㄴ", "⠊" to "ㄷ", "⠐" to "ㄹ", "⠑" to "ㅁ", "⠘" to "ㅂ", "⠠" to "ㅅ", "⠨" to "ㅈ", "⠰" to "ㅊ",
        "⠋" to "ㅋ", "⠓" to "ㅌ", "⠙" to "ㅍ", "⠚" to "ㅎ",
        "⠠⠈" to "ㄲ", "⠠⠊" to "ㄸ", "⠠⠘" to "ㅃ", "⠠⠠" to "ㅆ", "⠠⠨" to "ㅉ"
    )

    val abb_CHO_braille = mapOf(
        "⠉" to "ㄴ", "⠊" to "ㄷ", "⠑" to "ㅁ", "⠘" to "ㅂ", "⠨" to "ㅈ", "⠋" to "ㅋ", "⠓" to "ㅌ", "⠙" to "ㅍ", "⠚" to "ㅎ",
        "⠠⠈" to "ㄲ", "⠠⠊" to "ㄸ", "⠠⠘" to "ㅃ", "⠠⠠" to "ㅆ", "⠠⠨" to "ㅉ"
    )

    val double_CHO_braille = mapOf(
        "⠠⠈" to "ㄲ", "⠠⠊" to "ㄸ", "⠠⠘" to "ㅃ", "⠠⠠" to "ㅆ", "⠠⠨" to "ㅉ"
    )

    // 중성 점자
    val JUNG_braille = mapOf(
        "⠣" to "ㅏ", "⠜" to "ㅑ", "⠎" to "ㅓ", "⠱" to "ㅕ", "⠥" to "ㅗ", "⠬" to "ㅛ", "⠍" to "ㅜ", "⠩" to "ㅠ",
        "⠪" to "ㅡ", "⠕" to "ㅣ", "⠗" to "ㅐ", "⠝" to "ㅔ", "⠜⠗" to "ㅒ", "⠌" to "ㅖ",
        "⠧" to "ㅘ", "⠧⠗" to "ㅙ", "⠽" to "ㅚ", "⠏" to "ㅝ", "⠏⠗" to "ㅞ", "⠍⠗" to "ㅟ", "⠺" to "ㅢ"
    )

    val double_JUNG_braille = mapOf(
        "⠜⠗" to "ㅒ", "⠧⠗" to "ㅙ", "⠏⠗" to "ㅞ", "⠍⠗" to "ㅟ"
    )

    // 종성 점자
    val JONG_braille = mapOf(
        "⠁" to "ㄱ", "⠁⠄" to "ㄳ", "⠒" to "ㄴ", "⠒⠅" to "ㄵ", "⠒⠴" to "ㄶ", "⠔" to "ㄷ", "⠂" to "ㄹ",
        "⠂⠁" to "ㄺ", "⠂⠢" to "ㄻ", "⠂⠃" to "ㄼ", "⠂⠄" to "ㄽ", "⠂⠦" to "ㄾ", "⠂⠲" to "ㄿ", "⠂⠴" to "ㅀ",
        "⠢" to "ㅁ", "⠃" to "ㅂ", "⠃⠄" to "ㅄ", "⠄" to "ㅅ", "⠶" to "ㅇ", "⠅" to "ㅈ", "⠆" to "ㅊ", "⠖" to "ㅋ",
        "⠦" to "ㅌ", "⠲" to "ㅍ", "⠴" to "ㅎ", "⠁⠁" to "ㄲ", "⠌" to "ㅆ"
    )

    val double_JONG_braille = mapOf(
        "ㄱ⠄" to "ㄳ", "ㄴ⠅" to "ㄵ", "ㄴ⠴" to "ㄶ", "ㄹ⠁" to "ㄺ", "ㄹ⠢" to "ㄻ", "ㄹ⠃" to "ㄼ", "ㄹ⠄" to "ㄽ",
        "ㄹ⠦" to "ㄾ", "ㄹ⠲" to "ㄿ", "ㄹ⠴" to "ㅀ", "ㅂ⠄" to "ㅄ", "ㄱ⠁" to "ㄲ"
    )

    // 한글 점자 약어
    val abb_word_dict = mapOf(
        "⠁⠎" to "그래서", "⠁⠉" to "그러나", "⠁⠒" to "그러면", "⠁⠢" to "그러므로",
        "⠁⠝" to "그런데", "⠁⠥" to "그리고", "⠁⠱" to "그리하여"
    )

    // 한글 점자 약자
    val abb_cho_dict = mapOf(
        "⠫" to listOf("ㄱ", "ㅏ"),
        "⠇" to listOf("ㅅ", "ㅏ"),
        "⠠⠫" to listOf("ㄲ", "ㅏ"),
        "⠠⠇" to listOf("ㅆ", "ㅏ")
    )

    val abb_jung_jong_dict = mapOf(
        "⠹" to listOf("ㅓ", "ㄱ"), "⠾" to listOf("ㅓ", "ㄴ"), "⠞" to listOf("ㅓ", "ㄹ"),
        "⠡" to listOf("ㅕ", "ㄴ"), "⠳" to listOf("ㅕ", "ㄹ"), "⠻" to listOf("ㅕ", "ㅇ"),
        "⠭" to listOf("ㅗ", "ㄱ"), "⠷" to listOf("ㅗ", "ㄴ"), "⠿" to listOf("ㅗ", "ㅇ"),
        "⠛" to listOf("ㅜ", "ㄴ"), "⠯" to listOf("ㅜ", "ㄹ"), "⠵" to listOf("ㅡ", "ㄴ"),
        "⠮" to listOf("ㅡ", "ㄹ"), "⠟" to listOf("ㅣ", "ㄴ")
    )

    val abb_cho_jung_jong_dict = mapOf(
        "⠸⠎" to listOf("ㄱ", "ㅓ", "ㅅ")
    )

}
