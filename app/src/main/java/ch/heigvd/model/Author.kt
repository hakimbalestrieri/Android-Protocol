package ch.heigvd.model

import kotlinx.serialization.Serializable

/**
 * Data class to define an Author
 */
@Serializable
data class Author(var name : String) {
    override fun toString(): String {
        return name
    }
}
