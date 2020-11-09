package com.clloret.speakingpractice.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.clloret.speakingpractice.domain.entities.Stats
import com.clloret.speakingpractice.domain.entities.StatsPerDay

@Dao
interface StatsDao {
    @Query(
        """
                SELECT COUNT() AS total_attempts,
                       SUM(result) AS correct_attempts,
                       COUNT() - SUM(result) AS incorrect_attempts,
                       CAST (SUM(result) AS FLOAT) / COUNT() * 100 AS success_rate,
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
                -      COUNT(DISTINCT exercise_id) AS non_practiced_exercises
                  FROM exercise_attempts;
        """
    )
    fun getStats(): LiveData<Stats>

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

}
