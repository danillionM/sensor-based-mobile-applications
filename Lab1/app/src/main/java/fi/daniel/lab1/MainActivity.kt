package fi.daniel.lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fi.daniel.lab1.ui.theme.Lab1Theme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Scaffold(topBar = {
                        TopAppBar(colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = Color.White,
                        ), title = { Text(stringResource(id = R.string.app_name)) })
                    }) { innerPadding ->
                        Content(innerPadding)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Content(innerPadding: PaddingValues) {
    var isGoodbyeMessage by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Surface(
            shadowElevation = 2.dp, modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.45f)
                    .background(colorResource(id = R.color.secondary_blue))
            ) {
                Text(
                    text = if (message.isNotEmpty()) stringResource(id = R.string.hello) + " " + message else stringResource(
                        id = if (isGoodbyeMessage) R.string.goodbye_darkness else R.string.hello_world
                    ), modifier = Modifier.align(Alignment.TopCenter)
                )

                Surface(
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .fillMaxHeight(0.5f)
                            .background(colorResource(id = R.color.primary_blue))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "2",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }

        TextField(
            value = name,
            onValueChange = { newName ->
                name = newName
            },
            label = { Text(stringResource(id = R.string.input_hint)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Button(
            onClick = {
                if (name.isNotEmpty()) {
                    isGoodbyeMessage = false // Reset the message to "Hello World" mode
                    message = name
                } else {
                    message = ""
                    isGoodbyeMessage = !isGoodbyeMessage // Toggle the message
                }
            }, modifier = Modifier.padding(16.dp)
        ) {
            Text(text = stringResource(id = R.string.btn_txt))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Lab1Preview() {
    Lab1Theme {}
}