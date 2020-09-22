package com.clloret.speakingpractice.domain

import androidx.annotation.VisibleForTesting
import java.util.*

class ExerciseValidator(private val recognizedPhrases: List<String>, val practicePhrase: String) {

    fun getValidPhrase(): Pair<Boolean, String> {
        val index = getCorrectPhraseIndex()

        if (index == NOT_FOUND) {
            val bestPhrase = getBestPhrase()
            return Pair(false, bestPhrase)
        }

        val phrase = recognizedPhrases[index]
        return Pair(true, phrase)
    }

    @VisibleForTesting
    fun validatePhrase(recognizedPhrase: String): Boolean {
        val cleanText = cleanText(recognizedPhrase)
        val cleanPhrase = cleanText(practicePhrase)
        return cleanPhrase.equals(cleanText, true)
    }

    private fun getCorrectPhraseIndex(): Int {
        for (indexedValue in recognizedPhrases.withIndex()) {
            if (validatePhrase(indexedValue.value)) {
                return indexedValue.index
            }
        }
        return NOT_FOUND
    }

    private fun getBestPhrase(): String {
        val maxCorrect = recognizedPhrases
            .map { Pair(getWordsWithResults(it, practicePhrase), it) }
            .maxByOrNull { wordsWithPhrase -> wordsWithPhrase.first.count { it.second } }

        return maxCorrect!!.second
    }

    companion object {
        const val NOT_FOUND = -1

        private fun cleanText(text: String): String {
            return text
                .filter { it.isLetterOrDigit() || it.isWhitespace() }
                .toLowerCase(Locale.US)
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
