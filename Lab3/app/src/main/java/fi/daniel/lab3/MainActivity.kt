package fi.daniel.lab3

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fi.daniel.lab3.ui.theme.Lab3Theme
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab3Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Content()
                }
            }
        }
    }
}

@Composable
fun Content() {
    var text by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = text)
        Button(onClick = {
            thread {
                try {
                    Log.i("DBG", "Thread started")
                    val myUrl = URL("https://users.metropolia.fi/~jarkkov/koe.txt")
                    val myConn = myUrl.openConnection() as HttpURLConnection
                    myConn.setRequestProperty("Accept-Encoding", "identity")
                    myConn.requestMethod = "GET"
                    myConn.doOutput = true

                    val istream: InputStream = myConn.inputStream
                    val allText = istream.bufferedReader().use {
                        it.readText()
                    }
                    val result = StringBuilder()
                    result.append(allText)
                    text = result.toString()
                    Log.i("DBG", "Data received!")
                } catch (e: Exception) {
                    Log.e("FYI", "Network thread error: $e")
                }
            }
        }) {
            Text(text = "Read file")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Lab3Theme {
        Content()
    }
}