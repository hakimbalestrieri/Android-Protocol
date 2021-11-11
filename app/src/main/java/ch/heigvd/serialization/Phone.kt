package ch.heigvd.serialization

import kotlinx.serialization.Serializable

/**
 * Data class to define a phone
 */
@Serializable
data class Phone(var type: Type, var number: String) {
    enum class Type(val type: String) {
        home("home"), mobile("mobile"), work("work")
    }
}