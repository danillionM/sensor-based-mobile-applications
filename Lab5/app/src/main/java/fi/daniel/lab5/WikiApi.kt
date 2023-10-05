package fi.daniel.lab5

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object WikiApi {
    const val URL = "https://en.wikipedia.org/"

    object Model {
        data class WikiResponse(
            val query: Query2
        )

        data class Query2(
            val searchinfo: SearchInfo,
        )

        data class SearchInfo(
            val totalhits: Int
        )
    }

    interface WikiService {
        @GET("w/api.php")
        suspend fun hitCountCheck(
            @Query("action") action: String = "query",
            @Query("format") format: String = "json",
            @Query("list") list: String = "search",
            @Query("srsearch") srsearch: String,
        ): Response<Model.WikiResponse>
    }

    private val retrofit =
        Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create()).build()

    val service: WikiService by lazy {
        retrofit.create(WikiService::class.java)
    }
}
