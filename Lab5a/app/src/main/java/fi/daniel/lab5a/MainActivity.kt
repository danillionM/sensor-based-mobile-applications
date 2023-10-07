package fi.daniel.lab5a

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainAppNav(viewModel)
        }
    }
}

@Composable
fun MainAppNav(viewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "teams") {
        composable("teams") {
            TeamsScreen(viewModel, navController)
        }
        composable(
            "teamDetails/{teamId}",
            arguments = listOf(navArgument("teamId") { type = NavType.LongType })
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val teamId = arguments.getLong("teamId")
            TeamDetailsScreen(viewModel, teamId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(viewModel: MainViewModel, navController: NavController) {
    val teamList = viewModel.getAllTeams().observeAsState(listOf())
    val newTeamName = remember { mutableStateOf("") }
    val newTeamYear = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = newTeamName.value,
            onValueChange = { newTeamName.value = it },
            label = { Text("Enter Team Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = newTeamYear.value,
            onValueChange = { newTeamYear.value = it },
            label = { Text("Enter Year (optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Button(
            onClick = {
                val teamName = newTeamName.value
                val teamYear = newTeamYear.value
                if (teamName.isNotBlank()) {
                    viewModel.insertTeam(teamName, teamYear.toIntOrNull())
                    newTeamName.value = ""
                    newTeamYear.value = ""
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Add Team")
        }

        LazyColumn {
            items(teamList.value) { team ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("teamDetails/${team.id}")
                    }
                    .padding(vertical = 8.dp, horizontal = 16.dp)) {
                    Text(
                        text = "Team: ${team.name}",
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailsScreen(viewModel: MainViewModel, teamId: Long) {
    val team = viewModel.getTeamById(teamId).observeAsState()
    val playerList = viewModel.getPlayersByTeam(teamId).observeAsState(listOf())
    val newPlayerName = remember { mutableStateOf("") }
    val newPlayerPosition = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        team.value?.let { team ->
            Text(
                text = "Details of Team: ${team.name}",
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = newPlayerName.value,
                onValueChange = { newPlayerName.value = it },
                label = { Text("Enter Player Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = newPlayerPosition.value,
                onValueChange = { newPlayerPosition.value = it },
                label = { Text("Enter Player Position") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Button(
                onClick = {
                    val playerNameText = newPlayerName.value
                    val playerPositionText = newPlayerPosition.value
                    if (playerNameText.isNotBlank() && playerPositionText.isNotBlank()) {
                        viewModel.insertPlayer(playerNameText, playerPositionText, team.id)
                        newPlayerName.value = ""
                        newPlayerPosition.value = ""
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Add Player")
            }

            LazyColumn {
                items(playerList.value) { player ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Handle player details navigation if needed
                            }
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                    ) {
                        Text(
                            text = "Player: ${player.name} (${player.position})",
                            style = TextStyle(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
