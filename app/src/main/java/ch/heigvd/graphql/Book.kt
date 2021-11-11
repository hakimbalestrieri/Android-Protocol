package ch.heigvd.graphql

import kotlinx.serialization.Serializable

/**
 * Data class to define a Book
 */
@Serializable
data class Book(var title: String) {
    override fun toString(): String {
        return title
    }
}
