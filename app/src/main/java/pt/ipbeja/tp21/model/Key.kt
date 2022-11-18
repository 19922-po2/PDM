package pt.ipbeja.tp21.model

import kotlinx.serialization.Serializable

@Serializable
data class Key(
    val nodes: List<Node>,
    val results: List<Result>
) {
    @Serializable
    data class Node(
        val id: String,
        val question: String,
        val options: List<Goto>
    ) {
        @Serializable
        data class Goto(
            val goto: String,
            val text: String,
            val description: String,
            val imageLocation: String,
        )
    }

    @Serializable
    data class Result(
        val id: String,
        val ordem: String,
        val description: String,
        val imageLocation: String,
    )
}

