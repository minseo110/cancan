package com.example.cancan.init

import android.content.Context
import com.example.cancan.database.AppDatabase
import com.example.cancan.database.BrailleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun insertAllBrailleData(context: Context) = withContext(Dispatchers.IO) {
    val dao = AppDatabase.getDatabase(context).brailleDao()

        dao.insert(
            BrailleEntity(
                category = "초성",
                text = "ㄱ",
                braille = "⠈",
                answerBraille = listOf(listOf(0, 0, 0, 1, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "초성",
                text = "ㄲ",
                braille = "⠠⠈",
                answerBraille = listOf(listOf(0, 0, 0, 0, 0, 1), listOf(0, 0, 0, 1, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "초성",
                text = "ㄴ",
                braille = "⠉",
                answerBraille = listOf(listOf(1, 0, 0, 1, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "초성",
                text = "ㄷ",
                braille = "⠊",
                answerBraille = listOf(listOf(0, 1, 0, 1, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "초성",
                text = "ㄸ",
                braille = "⠠⠊",
                answerBraille = listOf(listOf(0, 0, 0, 0, 0, 1), listOf(0, 1, 0, 1, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "초성",
                text = "ㄹ",
                braille = "⠐",
                answerBraille = listOf(listOf(0, 0, 0, 0, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "초성",
                text = "ㅁ",
                braille = "⠑",
                answerBraille = listOf(listOf(1, 0, 0, 0, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "초성",
                text = "ㅂ",
                braille = "⠘",
                answerBraille = listOf(listOf(0, 0, 0, 1, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "초성",
                text = "ㅃ",
                braille = "⠠⠘",
                answerBraille = listOf(listOf(0, 0, 0, 0, 0, 1), listOf(0, 0, 0, 1, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "초성",
                text = "ㅅ",
                braille = "⠠",
                answerBraille = listOf(listOf(0, 0, 0, 0, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "초성",
                text = "ㅆ",
                braille = "⠠⠠",
                answerBraille = listOf(listOf(0, 0, 0, 0, 0, 1), listOf(0, 0, 0, 0, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "초성",
                text = "ㅇ",
                braille = "⠛",
                answerBraille = listOf(listOf(1, 1, 0, 1, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "초성",
                text = "ㅈ",
                braille = "⠨",
                answerBraille = listOf(listOf(0, 0, 0, 1, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "초성",
                text = "ㅉ",
                braille = "⠠⠨",
                answerBraille = listOf(listOf(0, 0, 0, 0, 0, 1), listOf(0, 0, 0, 1, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "초성",
                text = "ㅊ",
                braille = "⠰",
                answerBraille = listOf(listOf(0, 0, 0, 0, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "초성",
                text = "ㅋ",
                braille = "⠋",
                answerBraille = listOf(listOf(1, 1, 0, 1, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "초성",
                text = "ㅌ",
                braille = "⠓",
                answerBraille = listOf(listOf(1, 1, 0, 0, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "초성",
                text = "ㅍ",
                braille = "⠙",
                answerBraille = listOf(listOf(1, 0, 0, 1, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "초성",
                text = "ㅎ",
                braille = "⠚",
                answerBraille = listOf(listOf(0, 1, 0, 1, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "중성",
                text = "ㅏ",
                braille = "⠣",
                answerBraille = listOf(listOf(1, 1, 0, 0, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "중성",
                text = "ㅐ",
                braille = "⠗",
                answerBraille = listOf(listOf(1, 1, 1, 0, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "중성",
                text = "ㅑ",
                braille = "⠜",
                answerBraille = listOf(listOf(0, 0, 1, 1, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "중성",
                text = "ㅒ",
                braille = "⠜⠗",
                answerBraille = listOf(listOf(0, 0, 1, 1, 1, 0), listOf(1, 1, 1, 0, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "중성",
                text = "ㅓ",
                braille = "⠎",
                answerBraille = listOf(listOf(0, 1, 1, 1, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "중성",
                text = "ㅔ",
                braille = "⠝",
                answerBraille = listOf(listOf(1, 0, 1, 1, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "중성",
                text = "ㅕ",
                braille = "⠱",
                answerBraille = listOf(listOf(1, 0, 0, 0, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "중성",
                text = "ㅖ",
                braille = "⠌",
                answerBraille = listOf(listOf(0, 0, 1, 1, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "중성",
                text = "ㅗ",
                braille = "⠥",
                answerBraille = listOf(listOf(1, 0, 1, 0, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "중성",
                text = "ㅘ",
                braille = "⠧",
                answerBraille = listOf(listOf(1, 1, 1, 0, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "중성",
                text = "ㅙ",
                braille = "⠧⠗",
                answerBraille = listOf(listOf(1, 1, 1, 0, 0, 1), listOf(1, 1, 1, 0, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "중성",
                text = "ㅚ",
                braille = "⠽",
                answerBraille = listOf(listOf(1, 0, 1, 1, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "중성",
                text = "ㅛ",
                braille = "⠬",
                answerBraille = listOf(listOf(0, 0, 1, 1, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "중성",
                text = "ㅜ",
                braille = "⠍",
                answerBraille = listOf(listOf(1, 0, 1, 1, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "중성",
                text = "ㅝ",
                braille = "⠏",
                answerBraille = listOf(listOf(1, 1, 1, 1, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "중성",
                text = "ㅞ",
                braille = "⠏⠗",
                answerBraille = listOf(listOf(1, 1, 1, 1, 0, 0), listOf(1, 1, 1, 0, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "중성",
                text = "ㅟ",
                braille = "⠍⠗",
                answerBraille = listOf(listOf(1, 0, 1, 1, 0, 0), listOf(1, 1, 1, 0, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "중성",
                text = "ㅠ",
                braille = "⠩",
                answerBraille = listOf(listOf(1, 0, 0, 1, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "중성",
                text = "ㅡ",
                braille = "⠪",
                answerBraille = listOf(listOf(0, 1, 0, 1, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "중성",
                text = "ㅢ",
                braille = "⠺",
                answerBraille = listOf(listOf(0, 1, 0, 1, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "중성",
                text = "ㅣ",
                braille = "⠕",
                answerBraille = listOf(listOf(1, 0, 1, 0, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㄱ",
                braille = "⠁",
                answerBraille = listOf(listOf(1, 0, 0, 0, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㄲ",
                braille = "⠁⠁",
                answerBraille = listOf(listOf(1, 0, 0, 0, 0, 0), listOf(1, 0, 0, 0, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㄳ",
                braille = "⠁⠄",
                answerBraille = listOf(listOf(1, 0, 0, 0, 0, 0), listOf(0, 0, 1, 0, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㄴ",
                braille = "⠒",
                answerBraille = listOf(listOf(0, 1, 0, 0, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㄵ",
                braille = "⠒⠅",
                answerBraille = listOf(listOf(0, 1, 0, 0, 1, 0), listOf(1, 0, 1, 0, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㄶ",
                braille = "⠒⠴",
                answerBraille = listOf(listOf(0, 1, 0, 0, 1, 0), listOf(0, 0, 1, 0, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㄷ",
                braille = "⠔",
                answerBraille = listOf(listOf(0, 0, 1, 0, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㄹ",
                braille = "⠂",
                answerBraille = listOf(listOf(0, 1, 0, 0, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㄺ",
                braille = "⠂⠁",
                answerBraille = listOf(listOf(0, 1, 0, 0, 0, 0), listOf(1, 0, 0, 0, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㄻ",
                braille = "⠂⠢",
                answerBraille = listOf(listOf(0, 1, 0, 0, 0, 0), listOf(0, 1, 0, 0, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㄼ",
                braille = "⠂⠃",
                answerBraille = listOf(listOf(0, 1, 0, 0, 0, 0), listOf(1, 1, 0, 0, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㄽ",
                braille = "⠂⠄",
                answerBraille = listOf(listOf(0, 1, 0, 0, 0, 0), listOf(0, 0, 1, 0, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㄾ",
                braille = "⠂⠦",
                answerBraille = listOf(listOf(0, 1, 0, 0, 0, 0), listOf(0, 1, 1, 0, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㄿ",
                braille = "⠂⠲",
                answerBraille = listOf(listOf(0, 1, 0, 0, 0, 0), listOf(0, 1, 0, 0, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㅀ",
                braille = "⠂⠴",
                answerBraille = listOf(listOf(0, 1, 0, 0, 0, 0), listOf(0, 0, 1, 0, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㅁ",
                braille = "⠢",
                answerBraille = listOf(listOf(0, 1, 0, 0, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㅂ",
                braille = "⠃",
                answerBraille = listOf(listOf(1, 1, 0, 0, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㅄ",
                braille = "⠃⠄",
                answerBraille = listOf(listOf(1, 1, 0, 0, 0, 0), listOf(0, 0, 1, 0, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㅅ",
                braille = "⠄",
                answerBraille = listOf(listOf(0, 0, 1, 0, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㅆ",
                braille = "⠌",
                answerBraille = listOf(listOf(0, 0, 1, 1, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㅇ",
                braille = "⠶",
                answerBraille = listOf(listOf(0, 1, 1, 0, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㅈ",
                braille = "⠅",
                answerBraille = listOf(listOf(1, 0, 1, 0, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㅊ",
                braille = "⠆",
                answerBraille = listOf(listOf(0, 1, 1, 0, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㅋ",
                braille = "⠖",
                answerBraille = listOf(listOf(0, 1, 1, 0, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㅌ",
                braille = "⠦",
                answerBraille = listOf(listOf(0, 1, 1, 0, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㅍ",
                braille = "⠲",
                answerBraille = listOf(listOf(0, 1, 0, 0, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "종성",
                text = "ㅎ",
                braille = "⠴",
                answerBraille = listOf(listOf(0, 0, 1, 0, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "가",
                braille = "⠫",
                answerBraille = listOf(listOf(1, 1, 0, 1, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "나",
                braille = "⠉",
                answerBraille = listOf(listOf(1, 0, 0, 1, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "다",
                braille = "⠊",
                answerBraille = listOf(listOf(0, 1, 0, 1, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "마",
                braille = "⠑",
                answerBraille = listOf(listOf(1, 0, 0, 0, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "바",
                braille = "⠘",
                answerBraille = listOf(listOf(0, 0, 0, 1, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "사",
                braille = "⠇",
                answerBraille = listOf(listOf(1, 1, 1, 0, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "자",
                braille = "⠨",
                answerBraille = listOf(listOf(0, 0, 0, 1, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "카",
                braille = "⠋",
                answerBraille = listOf(listOf(1, 1, 0, 1, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "타",
                braille = "⠓",
                answerBraille = listOf(listOf(1, 1, 0, 0, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "파",
                braille = "⠙",
                answerBraille = listOf(listOf(1, 0, 0, 1, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "하",
                braille = "⠚",
                answerBraille = listOf(listOf(0, 1, 0, 1, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "것",
                braille = "⠸⠎",
                answerBraille = listOf(listOf(0, 0, 0, 1, 1, 1), listOf(0, 1, 1, 1, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "까",
                braille = "⠠⠫",
                answerBraille = listOf(listOf(0, 0, 0, 0, 0, 1), listOf(1, 1, 0, 1, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "싸",
                braille = "⠠⠇",
                answerBraille = listOf(listOf(0, 0, 0, 0, 0, 1), listOf(1, 1, 1, 0, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "껏",
                braille = "⠠⠸⠎",
                answerBraille = listOf(listOf(0, 0, 0, 0, 0, 1), listOf(0, 0, 0, 1, 1, 1), listOf(0, 1, 1, 1, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "성",
                braille = "⠠⠻",
                answerBraille = listOf(listOf(0, 0, 0, 0, 0, 1), listOf(1, 1, 0, 1, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "썽",
                braille = "⠠⠠⠻",
                answerBraille = listOf(listOf(0, 0, 0, 0, 0, 1), listOf(0, 0, 0, 0, 0, 1), listOf(1, 1, 0, 1, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "정",
                braille = "⠨⠻",
                answerBraille = listOf(listOf(0, 0, 0, 1, 0, 1), listOf(1, 1, 0, 1, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "쩡",
                braille = "⠠⠨⠻",
                answerBraille = listOf(listOf(0, 0, 0, 0, 0, 1), listOf(0, 0, 0, 1, 0, 1), listOf(1, 1, 0, 1, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "청",
                braille = "⠰⠻",
                answerBraille = listOf(listOf(0, 0, 0, 0, 1, 1), listOf(1, 1, 0, 1, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "억",
                braille = "⠹",
                answerBraille = listOf(listOf(1, 0, 0, 1, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "언",
                braille = "⠾",
                answerBraille = listOf(listOf(0, 1, 1, 1, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "얼",
                braille = "⠞",
                answerBraille = listOf(listOf(0, 1, 1, 1, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "연",
                braille = "⠡",
                answerBraille = listOf(listOf(1, 0, 0, 0, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "열",
                braille = "⠳",
                answerBraille = listOf(listOf(1, 1, 0, 0, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "영",
                braille = "⠻",
                answerBraille = listOf(listOf(1, 1, 0, 1, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "옥",
                braille = "⠭",
                answerBraille = listOf(listOf(1, 0, 1, 1, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "온",
                braille = "⠷",
                answerBraille = listOf(listOf(1, 1, 1, 0, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "옹",
                braille = "⠿",
                answerBraille = listOf(listOf(1, 1, 1, 1, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "운",
                braille = "⠛",
                answerBraille = listOf(listOf(1, 1, 0, 1, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "울",
                braille = "⠯",
                answerBraille = listOf(listOf(1, 1, 1, 1, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "은",
                braille = "⠵",
                answerBraille = listOf(listOf(1, 0, 1, 0, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "을",
                braille = "⠮",
                answerBraille = listOf(listOf(0, 1, 1, 1, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "일",
                braille = "⠟",
                answerBraille = listOf(listOf(1, 1, 1, 1, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "그래서",
                braille = "⠁⠎",
                answerBraille = listOf(listOf(1, 0, 0, 0, 0, 0), listOf(0, 1, 1, 1, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "그러나",
                braille = "⠁⠉",
                answerBraille = listOf(listOf(1, 0, 0, 0, 0, 0), listOf(1, 0, 0, 1, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "그러면",
                braille = "⠁⠒",
                answerBraille = listOf(listOf(1, 0, 0, 0, 0, 0), listOf(0, 1, 0, 0, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "그러므로",
                braille = "⠁⠢",
                answerBraille = listOf(listOf(1, 0, 0, 0, 0, 0), listOf(0, 1, 0, 0, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "그런데",
                braille = "⠁⠝",
                answerBraille = listOf(listOf(1, 0, 0, 0, 0, 0), listOf(1, 0, 1, 1, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "그리고",
                braille = "⠁⠥",
                answerBraille = listOf(listOf(1, 0, 0, 0, 0, 0), listOf(1, 0, 1, 0, 0, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "약자",
                text = "그리하여",
                braille = "⠁⠱",
                answerBraille = listOf(listOf(1, 0, 0, 0, 0, 0), listOf(1, 0, 0, 0, 1, 1))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "숫자",
                text = "0",
                braille = "⠼⠚",
                answerBraille = listOf(listOf(0, 0, 1, 1, 1, 1), listOf(0, 1, 0, 1, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "숫자",
                text = "1",
                braille = "⠼⠁",
                answerBraille = listOf(listOf(0, 0, 1, 1, 1, 1), listOf(1, 0, 0, 0, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "숫자",
                text = "2",
                braille = "⠼⠃",
                answerBraille = listOf(listOf(0, 0, 1, 1, 1, 1), listOf(1, 1, 0, 0, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "숫자",
                text = "3",
                braille = "⠼⠉",
                answerBraille = listOf(listOf(0, 0, 1, 1, 1, 1), listOf(1, 0, 0, 1, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "숫자",
                text = "4",
                braille = "⠼⠙",
                answerBraille = listOf(listOf(0, 0, 1, 1, 1, 1), listOf(1, 0, 0, 1, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "숫자",
                text = "5",
                braille = "⠼⠑",
                answerBraille = listOf(listOf(0, 0, 1, 1, 1, 1), listOf(1, 0, 0, 0, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "숫자",
                text = "6",
                braille = "⠼⠋",
                answerBraille = listOf(listOf(0, 0, 1, 1, 1, 1), listOf(1, 1, 0, 1, 0, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "숫자",
                text = "7",
                braille = "⠼⠛",
                answerBraille = listOf(listOf(0, 0, 1, 1, 1, 1), listOf(1, 1, 0, 1, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "숫자",
                text = "8",
                braille = "⠼⠓",
                answerBraille = listOf(listOf(0, 0, 1, 1, 1, 1), listOf(1, 1, 0, 0, 1, 0))
            )
        )

        dao.insert(
            BrailleEntity(
                category = "숫자",
                text = "9",
                braille = "⠼⠊",
                answerBraille = listOf(listOf(0, 0, 1, 1, 1, 1), listOf(0, 1, 0, 1, 0, 0))
            )
        )
}