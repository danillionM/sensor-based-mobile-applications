package fi.daniel.lab5a

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import fi.daniel.lab5a.entities.Player
import fi.daniel.lab5a.entities.Team
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val teamDao = database.teamDao()
    private val playerDao = database.playerDao()

    fun getAllTeams(): LiveData<List<Team>> {
        return teamDao.getAllTeams()
    }

    fun getTeamById(teamId: Long): LiveData<Team?> {
        return teamDao.getTeamById(teamId)
    }

    fun insertTeam(name: String, year: Int? = null) {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val team = Team(0, name, year ?: currentYear)
        viewModelScope.launch { teamDao.insertTeam(team) }
    }

    fun getPlayersByTeam(teamId: Long): LiveData<List<Player>> {
        return playerDao.getPlayersByTeam(teamId)
    }

    fun insertPlayer(name: String, position: String, teamId: Long) {
        val player = Player(0, name, position, teamId)
        viewModelScope.launch { playerDao.insertPlayer(player) }
    }
}