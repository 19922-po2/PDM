package pt.ipbeja.tp21.remote

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.auth.*
import io.ktor.client.features.auth.providers.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.observer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import pt.ipbeja.tp21.BuildConfig

/**
 * @author Diogo Pina Manique
 * @version 27/11/2021
 */

object AppAPI {

    private const val SIGHTINGS = "${BuildConfig.SERVER_URL}/sightings"

    private const val TIME_OUT = 30_000

    private lateinit var client: HttpClient

    @Deprecated("Não invocar! Do not call!")
    fun init(accessToken: String) {
        if (this::client.isInitialized) throw IllegalStateException("NÃO invocar init / Do NOT call init")

        client = HttpClient(Android) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })

                engine {
                    connectTimeout = TIME_OUT
                    socketTimeout = TIME_OUT
                }
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.v("Logger Ktor =>", message)
                    }
                }
                level = LogLevel.ALL
            }

            install(ResponseObserver) {
                onResponse { response ->
                    Log.d("HTTP status:", "${response.status.value}")
                }
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(accessToken, "")
                    }
                }
            }
        }
    }


    private suspend inline fun <reified T, reified F> requestAsResult(block: () -> HttpResponse): NetworkResult<T, F> {
        return try {
            val response = block()
            NetworkResult.Success(response.receive(), response.status, response.headers)
        } catch (e: Exception) {
            e.toNetworkResult()
        }
    }


    // TODO Adicionar pedidos aqui / Add your http requests here
    //  example of usage: AppApi.examplePost("posting some data")

    suspend fun examplePost(someData: String) = requestAsResult<List<String>, ErrorResponse> {
        client.post(SIGHTINGS) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            body = someData
        }
    }


}