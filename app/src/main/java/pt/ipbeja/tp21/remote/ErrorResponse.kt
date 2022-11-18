package pt.ipbeja.tp21.remote

import kotlinx.serialization.Serializable

/**
 * @author Diogo Pina Manique
 * @version 27/11/2021
 */

/**
 * The Error Response Model with the status [code], the status code [description] and a [message]
 */
@Serializable
data class ErrorResponse(
    val code: Int,
    val description: String,
    val message: String
)
