package com.clloret.speakingpractice.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    private const val VERSION_1 = 1
    private const val VERSION_2 = 2
    private const val VERSION_3 = 3
    private const val VERSION_4 = 4
    private const val VERSION_5 = 5

    val MIGRATION_1_2 = object : Migration(VERSION_1, VERSION_2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            baseMigration(database)
        }
    }

    val MIGRATION_2_3 = object : Migration(VERSION_2, VERSION_3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            migration2to3(database)
        }
    }

    val MIGRATION_3_4 = object : Migration(VERSION_3, VERSION_4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            migration3to4(database)
        }
    }

    val MIGRATION_4_5 = object : Migration(VERSION_4, VERSION_5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            migration4to5(database)
        }
    }

    private fun migration2to3(database: SupportSQLiteDatabase) {
        baseMigration(database)
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS practice_words (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `exercise_id` INTEGER NOT NULL, `time` INTEGER NOT NULL, `word` TEXT NOT NULL, `result` INTEGER NOT NULL, FOREIGN KEY(`exercise_id`) REFERENCES `exercises`(`exercise_id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_practice_words_exercise_id` ON practice_words (`exercise_id`)"
        )
    }

    private fun migration3to4(database: SupportSQLiteDatabase) {
        baseMigration(database)

        // Migrate exercise_attempts table

        database.execSQL(
            "CREATE TABLE IF NOT EXISTS exercise_attempts_new (`exercise_attempt_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `exercise_id` INTEGER NOT NULL, `time` INTEGER NOT NULL, `result` INTEGER NOT NULL, `recognized_text` TEXT NOT NULL, FOREIGN KEY(`exercise_id`) REFERENCES `exercises`(`exercise_id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        database.execSQL(
            "INSERT INTO exercise_attempts_new (exercise_attempt_id, exercise_id, time, result, recognized_text)"
                    + " SELECT id, exercise_id, time, result,  recognized_text FROM exercise_attempts"
        )
        database.execSQL("DROP TABLE exercise_attempts")
        database.execSQL("ALTER TABLE exercise_attempts_new RENAME TO exercise_attempts")
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_exercise_attempts_exercise_id` ON exercise_attempts (`exercise_id`)"
        )

        // Drop old practice_words table

        database.execSQL("DROP TABLE practice_words")
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS practice_words (`practice_word_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `exercise_attempt_id` INTEGER NOT NULL, `word` TEXT NOT NULL, `result` INTEGER NOT NULL, FOREIGN KEY(`exercise_attempt_id`) REFERENCES `exercise_attempts`(`exercise_attempt_id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_practice_words_exercise_attempt_id` ON practice_words (`exercise_attempt_id`)"
        )
    }

    private fun migration4to5(database: SupportSQLiteDatabase) {
        database.execSQL("DROP VIEW IF EXISTS exercise_results")
        database.execSQL(
            """
        CREATE VIEW `exercise_results` AS SELECT 
            exercises.exercise_id,
            CAST(TOTAL(result) AS INT) AS correct, 
            COUNT(exercise_attempts.result) - CAST(TOTAL(result) AS INT) AS incorrect
        FROM 
            exercises
            LEFT OUTER JOIN exercise_attempts ON exercises.exercise_id = exercise_attempts.exercise_id 
            GROUP BY exercises.exercise_id"""
        )
    }

    private fun baseMigration(database: SupportSQLiteDatabase) {
        // Migrate exercises table

        database.execSQL(
            "CREATE TABLE IF NOT EXISTS exercises_new (`exercise_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `practice_phrase` TEXT NOT NULL, `translated_phrase` TEXT NOT NULL)"
        )
        database.execSQL(
            "INSERT INTO exercises_new (exercise_id, practice_phrase, translated_phrase)"
                    + " SELECT exercise_id, practice_phrase, translated_phrase FROM exercises"
        )
        database.execSQL("DROP TABLE exercises")
        database.execSQL("ALTER TABLE exercises_new RENAME TO exercises")

        // Migrate exercise_attempts table

        database.execSQL(
            "CREATE TABLE IF NOT EXISTS exercise_attempts_new (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `exercise_id` INTEGER NOT NULL, `time` INTEGER NOT NULL, `result` INTEGER NOT NULL, `recognized_text` TEXT NOT NULL, FOREIGN KEY(`exercise_id`) REFERENCES `exercises`(`exercise_id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        database.execSQL(
            "INSERT INTO exercise_attempts_new (id, exercise_id, time, result, recognized_text)"
                    + " SELECT id, exercise_id, time, result, recognized_text FROM exercise_attempts"
        )
        database.execSQL("DROP TABLE exercise_attempts")
        database.execSQL("ALTER TABLE exercise_attempts_new RENAME TO exercise_attempts")
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_exercise_attempts_exercise_id` ON exercise_attempts (`exercise_id`)"
        )

        // Migrate tags table

        database.execSQL(
            "CREATE TABLE IF NOT EXISTS tags_new (`tag_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL)"
        )
        database.execSQL(
            "INSERT INTO tags_new (tag_id, name)"
                    + " SELECT tag_id, name FROM tags"
        )
        database.execSQL("DROP TABLE tags")
        database.execSQL("ALTER TABLE tags_new RENAME TO tags")
        database.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_tags_name` ON tags (`name`)"
        )

        // Migrate tag_exercise_join table

        database.execSQL(
            "CREATE TABLE IF NOT EXISTS tag_exercise_join_new (`tag_id` INTEGER NOT NULL, `exercise_id` INTEGER NOT NULL, PRIMARY KEY(`tag_id`, `exercise_id`), FOREIGN KEY(`tag_id`) REFERENCES `tags`(`tag_id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`exercise_id`) REFERENCES `exercises`(`exercise_id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        database.execSQL(
            "INSERT INTO tag_exercise_join_new (tag_id, exercise_id)"
                    + " SELECT tag_id, exercise_id FROM tag_exercise_join"
        )
        database.execSQL("DROP TABLE tag_exercise_join")
        database.execSQL("ALTER TABLE tag_exercise_join_new RENAME TO tag_exercise_join")
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_tag_exercise_join_tag_id` ON tag_exercise_join (`tag_id`)"
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_tag_exercise_join_exercise_id` ON tag_exercise_join (`exercise_id`)"
        )
    }
}
