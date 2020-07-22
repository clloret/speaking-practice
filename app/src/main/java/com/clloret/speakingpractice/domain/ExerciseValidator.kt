package com.clloret.speakingpractice.domain

import java.util.*

class ExerciseValidator {
    companion object {
        private fun cleanText(text: String): String {
            return text
                .filter { it.isLetterOrDigit() || it.isWhitespace() }
                .toLowerCase(Locale.US)
        }

        fun validatePhrase(recognizedPhrase: String, practicePhrase: String): Boolean {
            val cleanText = cleanText(recognizedPhrase)
            val cleanPhrase = cleanText(practicePhrase)
            return cleanPhrase.equals(cleanText, true)
        }

        fun checkCorrectWords(recognizedPhrase: String, practicePhrase: String): List<Int> {
            val cleanRecognized = cleanText(recognizedPhrase)
            val cleanPractice = cleanText(practicePhrase)
            val recognizedWords = cleanRecognized.split(" ")
            val practiceWords = cleanPractice.split(" ")
            val practiceWordsMap = practiceWords
                .withIndex()
                .groupBy(keySelector = { it.value }, valueTransform = { it.index })
                .toMutableMap()

            val recognizedWordsIndex = mutableListOf<Int>()
            var lastIndex: Int

            for (word in recognizedWords) {
                if (practiceWordsMap.containsKey(word)) {
                    val indexes = practiceWordsMap[word] as MutableList<Int>
                    lastIndex = indexes.removeAt(0)
                    if (indexes.size == 0) {
                        practiceWordsMap.remove(word)
                    }

                    recognizedWordsIndex.add(lastIndex)
                }
            }

            return recognizedWordsIndex
        }

        fun getWordsWithResults(
            practicePhrase: String,
            correctWordsPositions: List<Int>
        ): List<Pair<String, Boolean>> {
            val practiceWords = practicePhrase.split(" ")
            return practiceWords.mapIndexed { idx, value ->
                Pair(
                    value,
                    correctWordsPositions.contains(idx)
                )
            }
        }

    }
}