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

        fun getWordsWithResults(
            recognizedPhrase: String, practicePhrase: String
        ): List<Pair<String, Boolean>> {
            val practiceWords = cleanText(practicePhrase).split(" ")
            val practiceWordsMap = practiceWords
                .withIndex()
                .groupBy(keySelector = { it.value }, valueTransform = { it.index })
                .toMutableMap()

            val correctWordsPositions = mutableListOf<Int>()
            var lastIndex: Int
            val recognizedWords = cleanText(recognizedPhrase).split(" ")

            for (word in recognizedWords) {
                if (practiceWordsMap.containsKey(word)) {
                    val indexes = practiceWordsMap[word] as MutableList<Int>
                    lastIndex = indexes.removeAt(0)
                    if (indexes.size == 0) {
                        practiceWordsMap.remove(word)
                    }

                    correctWordsPositions.add(lastIndex)
                }
            }

            return practicePhrase.split(" ").mapIndexed { idx, value ->
                Pair(
                    value,
                    correctWordsPositions.contains(idx)
                )
            }
        }


    }
}