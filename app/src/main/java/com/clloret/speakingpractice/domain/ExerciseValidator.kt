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
            val cleanText = cleanText(recognizedPhrase)
            val cleanPhrase = cleanText(practicePhrase)
            val textWords = cleanText.split(" ")
            val practiceWords = cleanPhrase.split(" ")
            val practiceWordsMap = mutableMapOf<String, List<Int>>()

            for ((index, value) in practiceWords.withIndex()) {
                if (practiceWordsMap.containsKey(value)) {
                    val positions = practiceWordsMap[value] as MutableList<Int>
                    positions.add(index)
                } else {
                    practiceWordsMap[value] = mutableListOf(index)
                }
            }

            val recognizedWordsIndex = mutableListOf<Int>()
            val correctWords = mutableListOf<String>()
            var lastIndex: Int

            for (word in textWords) {
                if (practiceWordsMap.containsKey(word)) {
                    correctWords.add(word)

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
            val wordList = mutableListOf<Pair<String, Boolean>>()
            val practiceWords = practicePhrase.split(" ")

            practiceWords.withIndex().forEach { wordWithIndex ->
                val correct = correctWordsPositions.contains(wordWithIndex.index)
                wordList.add(Pair(wordWithIndex.value, correct))
                if (practiceWords.lastIndex != wordWithIndex.index) {
                    wordList.add(Pair(" ", true))
                }
            }
            return wordList
        }

    }
}