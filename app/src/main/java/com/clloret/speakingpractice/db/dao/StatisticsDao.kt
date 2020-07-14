package com.clloret.speakingpractice.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.clloret.speakingpractice.domain.entities.Statistics

@Dao
interface StatisticsDao {
    @Query(
        """SELECT COUNT() AS total_attempts,
       SUM(result) AS correct_attempts,
       COUNT() - SUM(result) AS incorrect_attempts,
       CAST (SUM(result) AS FLOAT) / COUNT() AS success_rate,
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
    fun getStatistics(): LiveData<Statistics>

}