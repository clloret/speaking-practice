package com.clloret.speakingpractice.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.clloret.speakingpractice.db.dao.ExerciseAttemptDao
import com.clloret.speakingpractice.db.dao.ExerciseDao
import com.clloret.speakingpractice.db.dao.TagDao
import com.clloret.speakingpractice.db.dao.TagExerciseJoinDao
import com.clloret.speakingpractice.domain.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@Database(
    entities = [Exercise::class, ExerciseAttempt::class, Tag::class, TagExerciseJoin::class],
    views = [ExerciseResults::class], version = 1, exportSchema = false
)
@TypeConverters(DbConverters::class)
abstract class ExercisesDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseAttemptDao(): ExerciseAttemptDao
    abstract fun tagDao(): TagDao
    abstract fun tagExerciseJoinDao(): TagExerciseJoinDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ExercisesDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ExercisesDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExercisesDatabase::class.java,
                    "exercises"
                )
                    .addCallback(ExercisesDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class ExercisesDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let {
                scope.launch {
                    populateDatabase(it.exerciseDao(), it.tagDao(), it.tagExerciseJoinDao())
                }
            }
        }

        suspend fun populateDatabase(
            exerciseDao: ExerciseDao,
            tagDao: TagDao,
            tagExerciseJoinDao: TagExerciseJoinDao
        ) {
            tagExerciseJoinDao.deleteAll()
            exerciseDao.deleteAll()
            tagDao.deleteAll()

            val tag = Tag(1, "Samples")
            tagDao.insert(tag)

            var exercise = Exercise(
                1,
                "What is your name",
                "¿Cómo te llamas?"
            )
            val insert = exerciseDao.insert(exercise)

            Timber.d("ExerciseId: $insert")

            tagExerciseJoinDao.insert(TagExerciseJoin(1, insert.toInt()))

            exercise = Exercise(
                2,
                "What do you do",
                "¿Qué haces?"
            )
            exerciseDao.insert(exercise)

            exercise = Exercise(
                3,
                "I don’t understand",
                "No entiendo"
            )
            exerciseDao.insert(exercise)

        }
    }
}

