package ch.heigvd.model

import kotlinx.serialization.Serializable

/**
 * Data class to define a phone
 */
@Serializable
data class Phone(var type: Type, var number: String) {
    companion object {
        fun parseXML(input: String): Phone {
            val type = input.substring(input.indexOf("\""), input.lastIndexOf("\""))
            var number = input.replace("</phone>", "")
            number = number.substring(number.indexOf(">"), number.length)
            return Phone(Type.valueOf(type), number)
        }
    }

    enum class Type(val type: String) {
        home("home"), mobile("mobile"), work("work")
    }

    fun serializeToXML(): String {
        return "<phone type=\"${type}\">${number}</phone>"
    }
}