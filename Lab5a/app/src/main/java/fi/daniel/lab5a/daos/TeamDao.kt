package fi.daniel.lab5a.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fi.daniel.lab5a.entities.Team

@Dao
interface TeamDao {
    @Query("SELECT * FROM teams")
    fun getAllTeams(): LiveData<List<Team>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: Team)

    @Query("SELECT * FROM teams WHERE id = :teamId")
    fun getTeamById(teamId: Long): LiveData<Team?>
}