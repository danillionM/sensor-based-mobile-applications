package fi.daniel.lab5

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WikiRepository {
    private val wikiService = WikiApi.service

    suspend fun hitCountCheck(name: String): Int {
        val response = wikiService.hitCountCheck(srsearch = name)
        if (response.isSuccessful) {
            val body = response.body()
            val searchInfo = body?.query?.searchinfo
            return searchInfo?.totalhits ?: 0
        } else {
            throw Exception("Failed to fetch hit count")
        }
    }
}

class MainViewModel : ViewModel() {
    private val repository: WikiRepository = WikiRepository()
    var wikiUiState: Int by mutableStateOf(0)
        private set

    fun getHits(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val hitCount = repository.hitCountCheck(name)
                wikiUiState = hitCount
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching hit count: ${e.message}")
            }
        }
    }
}