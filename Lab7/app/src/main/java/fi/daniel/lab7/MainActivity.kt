package fi.daniel.lab7

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
                ShowDevices(mBluetoothAdapter!!, viewModel)
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

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("MissingPermission")
@Composable
fun ShowDevices(mBluetoothAdapter: BluetoothAdapter, viewModel: MainViewModel) {
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

            Text(
                text = "Heart Rate: ${heartRate ?: "N/A"}",
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp)
            )

            LazyColumn {
                items(value ?: emptyList()) { scanResult ->
                    val device = scanResult.device
                    val deviceAddress = device.address
                    val deviceName = device.name ?: "Unknown"
                    val rssi = scanResult.rssi
                    val connectable = scanResult.isConnectable

                    val textColor = if (connectable) Color.Black else Color.Gray

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        onClick = {
                            viewModel.connectToHeartRateDevice(scanResult.device.address)
                        }
                    ) {
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