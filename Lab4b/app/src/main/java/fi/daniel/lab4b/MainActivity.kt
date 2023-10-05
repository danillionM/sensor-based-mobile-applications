package fi.daniel.lab4b

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import fi.daniel.lab4b.ui.theme.Lab4bTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imgUrl = URL("https://users.metropolia.fi/~jarkkov/folderimage.jpg")
        lifecycleScope.launch {
            val decodedImg = withContext(Dispatchers.IO) {
                downloadImage(imgUrl)
            }
            val imgBitmap = decodedImg?.asImageBitmap()

            setContent {
                Lab4bTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Content(imgUrl, imgBitmap)
                    }
                }
            }
        }

    }
}

suspend fun downloadImage(imgUrl: URL): Bitmap? {
    try {
        val myConn = imgUrl.openConnection() as HttpURLConnection
        myConn.connect()

        val inputStream: InputStream = myConn.inputStream
        return BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

@Composable
fun Content(imageUrl: URL, imgBitmap: ImageBitmap?) {
    Box(modifier = Modifier.fillMaxSize()) {
        imgBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap, contentDescription = null, modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            text = imageUrl.toString(),
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Lab4bTheme {}
}