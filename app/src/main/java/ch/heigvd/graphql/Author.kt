package ch.heigvd.graphql

import kotlinx.serialization.Serializable

/**
 * Data class to define an Author
 */
@Serializable
data class Author(var id: String, var name : String) {
    override fun toString(): String {
        return name
    }
}
