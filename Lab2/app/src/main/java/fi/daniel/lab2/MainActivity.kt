package fi.daniel.lab2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fi.daniel.lab2.ui.theme.Lab2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val presidents: MutableList<President> = DataProvider.presidents
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            Lab2Theme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavHost(navController, startDestination = "presidentList") {
                        composable("presidentList") { PresidentList(presidents, navController) }
                        composable("presidentDetail/{name}") { navBackStackEntry ->
                            val name = navBackStackEntry.arguments?.getString("name")!!
                            val president = DataProvider.presidents.find { it.name == name }
                            PresidentDetail(president!!)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PresidentList(presidents: List<President>, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        presidents.forEach { president ->
            Text(
                text = president.name,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("presidentDetail/${president.name}")
                    }
            )
        }
    }
}


@Composable
fun PresidentDetail(president: President) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = president.name,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Start Duty: ${president.startDuty}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "End Duty: ${president.endDuty}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Description: ${president.description}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PresidentPreview() {
    Lab2Theme {}
}