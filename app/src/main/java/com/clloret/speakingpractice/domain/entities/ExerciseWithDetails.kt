package com.clloret.speakingpractice.domain.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.clloret.speakingpractice.domain.exercise.list.sort.ExerciseSortable
import com.clloret.speakingpractice.utils.databinding.ChipEntryBinding
import com.clloret.speakingpractice.utils.databinding.adapters.TagChipEntry

data class ExerciseWithDetails(
    @Embedded val exercise: Exercise,
    @Relation(
        parentColumn = "exercise_id",
        entityColumn = "tag_id",
        associateBy = Junction(TagExerciseJoin::class)
    )
    val tags: List<Tag>,
    @Relation(
        parentColumn = "exercise_id",
        entityColumn = "exercise_id"
    )
    val results: ExerciseResults
) : ExerciseSortable {
    override val practicePhrase: String
        get() = exercise.practicePhrase
    override val correct: Int
        get() = results.correct
    override val incorrect: Int
        get() = results.incorrect
    override val successRate: Int
        get() = results.successRate

    val chipTags: List<ChipEntryBinding>
        get() = tags.map { TagChipEntry(it) }
}
