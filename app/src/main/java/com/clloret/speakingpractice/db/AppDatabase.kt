package com.clloret.speakingpractice.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.db.converters.DateConverter
import com.clloret.speakingpractice.db.converters.LocalDateConverter
import com.clloret.speakingpractice.db.dao.*
import com.clloret.speakingpractice.domain.entities.*
import com.clloret.speakingpractice.exercise.import_.ImportExercises
import dev.matrix.roomigrant.GenerateRoomMigrations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@Database(
    entities = [
        Exercise::class,
        ExerciseAttempt::class,
        Tag::class,
        TagExerciseJoin::class,
        PracticeWord::class,
        DailyStats::class
    ],
    views = [ExerciseResults::class],
    version = AppDatabase.CURRENT_VERSION,
    exportSchema = true
)
@TypeConverters(DateConverter::class, LocalDateConverter::class)
@GenerateRoomMigrations
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseAttemptDao(): ExerciseAttemptDao
    abstract fun tagDao(): TagDao
    abstract fun tagExerciseJoinDao(): TagExerciseJoinDao
    abstract fun statsDao(): StatsDao
    abstract fun practiceWordDao(): PracticeWordDao

    companion object {
        const val CURRENT_VERSION = 6

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
                    .addCallback(ExercisesDatabaseCallback(context.applicationContext, scope))
                    .addMigrations(DatabaseMigrations.MIGRATION_1_2)
                    .addMigrations(DatabaseMigrations.MIGRATION_2_3)
                    .addMigrations(DatabaseMigrations.MIGRATION_3_4)
                    .addMigrations(DatabaseMigrations.MIGRATION_4_5)
                    .addMigrations(*AppDatabase_Migrations.build())
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class ExercisesDatabaseCallback(
        private val context: Context,
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback(), KoinComponent {

        private val importExercises: ImportExercises by inject { parametersOf(context) }

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let {
                scope.launch {
                    populateDatabase(context)
                }
            }
        }

        suspend fun populateDatabase(context: Context) {
            context.resources.openRawResource(R.raw.exercises).use {
                importExercises.import(it, false) {}
            }
        }
    }
}

