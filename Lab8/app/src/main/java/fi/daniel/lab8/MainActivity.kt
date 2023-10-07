package fi.daniel.lab8

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class MainActivity : ComponentActivity() {
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private val viewModel: MainViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter

        if (hasPermissions()) {
            setContent {
                MainAppNav(mBluetoothAdapter!!, viewModel)
            }
        }
    }

    private fun hasPermissions(): Boolean {
        if (mBluetoothAdapter == null || !mBluetoothAdapter!!.isEnabled) {
            Log.d("DBG", "No Bluetooth LE capability")
            return false
        } else if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("DBG", "No fine location access")
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return true // assuming that the user grants permission
        }
        return true
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainAppNav(mBluetoothAdapter: BluetoothAdapter, viewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "main") {
        composable("main") {
            MainScreen(mBluetoothAdapter, viewModel, navController)
        }
        composable(
            "graph"
        ) {
            GraphScreen(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("MissingPermission")
@Composable
fun MainScreen(
    mBluetoothAdapter: BluetoothAdapter, viewModel: MainViewModel, navController: NavController
) {
    val value: List<ScanResult>? by viewModel.scanResults.observeAsState(null)
    val fScanning: Boolean by viewModel.fScanning.observeAsState(false)
    val heartRate: Int? by viewModel.mBPM.observeAsState(null)

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Top
        ) {
            Button(
                onClick = {
                    viewModel.scanDevices(mBluetoothAdapter.bluetoothLeScanner)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentPadding = PaddingValues(16.dp),
            ) {
                Text(text = "Start Scanning")
            }

            Text(text = "Heart Rate: ${heartRate ?: "N/A"}",
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp)
                )

            Button(
                onClick = {
                    navController.navigate("graph")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentPadding = PaddingValues(16.dp),
            ) {
                Text(text = "See graph")
            }

            LazyColumn {
                items(value ?: emptyList()) { scanResult ->
                    val device = scanResult.device
                    val deviceAddress = device.address
                    val deviceName = device.name ?: "Unknown"
                    val rssi = scanResult.rssi
                    val connectable = scanResult.isConnectable

                    val textColor = if (connectable) Color.Black else Color.Gray

                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp), onClick = {
                        viewModel.connectToHeartRateDevice(scanResult.device.address)
                    }) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Name: $deviceName", color = textColor
                            )
                            Text(
                                text = "Address: $deviceAddress", color = textColor
                            )
                            Text(
                                text = "RSSI: $rssi dBm", color = textColor
                            )
                        }
                    }
                }
            }

            // Display scanning status
            Text(
                text = if (fScanning) "Scanning..." else "Not scanning",
                color = if (fScanning) Color.Green else Color.Red,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp)
            )
        }
    }
}


@Composable
fun GraphScreen(viewModel: MainViewModel) {
    val bpms = viewModel.mBPMs

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        if (bpms!!.isNotEmpty()) {
            val entries = mutableListOf<Entry>()
            for ((index, bpm) in bpms!!.withIndex()) {
                entries.add(Entry(index.toFloat(), bpm.toFloat()))
            }
            LineChart(
                data = entries
            )
        } else {
            // Show a message when there's no data
            Text(
                text = "No Heart Rate data available.", color = Color.Black
            )
        }
    }
}

@Composable
fun LineChart(
    data: MutableList<Entry>
) {

    AndroidView(modifier = Modifier.fillMaxSize(), factory = { context: Context ->
        val view = LayoutInflater.from(context).inflate(R.layout.graph, null, false)
        val graph = view.findViewById<com.github.mikephil.charting.charts.LineChart>(R.id.graph)
        val data = LineData(LineDataSet(data, "Data"))
        graph.data = data
        view
    }, update = { view ->
        // Update the view
        view.invalidate()
    })
}
