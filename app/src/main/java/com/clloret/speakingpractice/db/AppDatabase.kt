package com.clloret.speakingpractice.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.clloret.speakingpractice.db.dao.*
import com.clloret.speakingpractice.domain.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@Database(
    entities = [Exercise::class, ExerciseAttempt::class, Tag::class, TagExerciseJoin::class,
        PracticeWord::class],
    views = [ExerciseResults::class], version = 3, exportSchema = true
)
@TypeConverters(DbConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseAttemptDao(): ExerciseAttemptDao
    abstract fun tagDao(): TagDao
    abstract fun tagExerciseJoinDao(): TagExerciseJoinDao
    abstract fun statisticsDao(): StatisticsDao
    abstract fun practiceWordDao(): PracticeWordDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "speaking-practice"
                )
                    .addCallback(ExercisesDatabaseCallback(scope))
                    .addMigrations(DatabaseMigrations.MIGRATION_1_2)
                    .addMigrations(DatabaseMigrations.MIGRATION_2_3)
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

