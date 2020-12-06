package com.clloret.speakingpractice.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.clloret.speakingpractice.domain.entities.CalculatedStats
import com.clloret.speakingpractice.domain.entities.DailyStats
import com.clloret.speakingpractice.domain.entities.Stats
import com.clloret.speakingpractice.domain.entities.StatsPerDay
import java.time.LocalDate

@Dao
interface StatsDao {
    @Query(
        """
                SELECT COUNT() AS total_attempts,
                       SUM(result) AS correct_attempts,
                       COUNT() - SUM(result) AS incorrect_attempts,
                       (
                           SELECT COUNT() 
                             FROM exercises
                       )
                       AS total_exercises,
                       COUNT(DISTINCT exercise_id) AS practiced_exercises,
                       (
                           SELECT COUNT() 
                             FROM exercises
                       )
                -      COUNT(DISTINCT exercise_id) AS non_practiced_exercises,
                       (SELECT current_streak FROM stats) AS current_streak
                  FROM exercise_attempts;
        """
    )
    fun getCalculatedStats(): LiveData<CalculatedStats>

    @Query(
        """
                SELECT DATE(time / 1000, 'unixepoch') AS day,
                       COUNT() AS total_attempts,
                       SUM(result) AS correct_attempts,
                       COUNT() - SUM(result) AS incorrect_attempts
                  FROM exercise_attempts
                 GROUP BY day
                HAVING day = :day;
"""
    )
    fun getStatsPerDay(day: String): LiveData<StatsPerDay>

    @Query("SELECT * FROM daily_stats WHERE date = :date")
    suspend fun getDailyStatsByDate(date: LocalDate): DailyStats?

    @Query("SELECT * FROM daily_stats WHERE date >= :date")
    fun getDailyStatsFromDate(date: LocalDate): LiveData<List<DailyStats>>

    @Query(
        """
                UPDATE daily_stats
                   SET
                    total_correct = (
                      SELECT CAST (TOTAL(result) AS INT) 
                        FROM exercise_attempts                    
                    ),
                    total_incorrect = (
                      SELECT COUNT(*) - CAST (TOTAL(result) AS INT) 
                        FROM exercise_attempts                    
                    ),
                    correct = (
                      SELECT CAST(TOTAL(result) AS INT)
                        FROM exercise_attempts
                        WHERE time / 86400000 = :epochDate
                    ),
                    incorrect = (
                      SELECT COUNT(*) - CAST(TOTAL(result) AS INT)
                        FROM exercise_attempts
                        WHERE time / 86400000 = :epochDate
                    ),
                    time_practicing = :timePracticing
                 WHERE date = :epochDate
"""
    )
    suspend fun updateDailyStats(timePracticing: Int, epochDate: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dailyStats: DailyStats): Long

    @Query("""SELECT * FROM stats LIMIT 1""")
    suspend fun getStats(): Stats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stats: Stats)
}
