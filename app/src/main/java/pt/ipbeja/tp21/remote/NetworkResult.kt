package pt.ipbeja.tp21.remote

import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.http.*


/**
 * @author Diogo Pina Manique
 * @version 27/11/2021
 */

/**
 * Network response wrapper
 */
sealed class NetworkResult<out S, out F> {

    /**
     * Http success response (status code 2XX)
     */
    data class Success<out S>(val data: S, val statusCode: HttpStatusCode, val headers: Headers) :
        NetworkResult<S, Nothing>()

    /**
     * Http failure response (status code >=300)
     */
    sealed class Failure<out F>(
        val error: F,
        val statusCode: HttpStatusCode,
        val headers: Headers
    ) :
        NetworkResult<Nothing, F>() {

        class Client<out F>(error: F, statusCode: HttpStatusCode, headers: Headers) :
            Failure<F>(error, statusCode, headers)

        class Server<out F>(error: F, statusCode: HttpStatusCode, headers: Headers) :
            Failure<F>(error, statusCode, headers)

        class Redirection<out F>(error: F, statusCode: HttpStatusCode, headers: Headers) :
            Failure<F>(error, statusCode, headers)
    }

    /**
     * Other unexpected exceptions
     */
    data class UnknownError(val e: Throwable) : NetworkResult<Nothing, Nothing>()

}

/**
 * Throwable to NetworkResult error types mapper
 */
internal suspend inline fun <reified NR, reified E> Throwable.toNetworkResult(): NetworkResult<NR, E> =
    when (this) {
        is ResponseException -> try {
            with(response) {
                when (status.value) {
                    in 300..399 -> NetworkResult.Failure.Redirection(receive(), status, headers)
                    in 400..499 -> NetworkResult.Failure.Client(receive(), status, headers)
                    else -> NetworkResult.Failure.Server(receive(), status, headers)
                }
            }
        } catch (throwable: Throwable) {
            // In case receive() fails to deserialize
            NetworkResult.UnknownError(throwable)
        }
        else -> NetworkResult.UnknownError(this) // Some other exception
    }

