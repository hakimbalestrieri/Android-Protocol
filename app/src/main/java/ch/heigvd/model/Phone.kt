package ch.heigvd.model

import kotlinx.serialization.Serializable

/**
 * Data class to define a phone
 */
@Serializable
data class Phone(var type: Type, var number: String) {
    enum class Type {
        HOME, MOBILE, WORK
    }
}