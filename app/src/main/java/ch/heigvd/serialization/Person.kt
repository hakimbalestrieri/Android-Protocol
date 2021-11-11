package ch.heigvd.serialization

import kotlinx.serialization.Serializable

/**
 * Data class to define a person
 */
@Serializable
data class Person(
    var name: String,
    var firstname: String,
    var middlename: String?,
    var phone: List<Phone>
) {

    companion object{
        fun parseXML(input: String): Person {
            val name = input.substring(input.indexOf("<name>") + 1, input.indexOf("</name>"))
            val firstname =
                input.substring(input.indexOf("<firstname>") + 1, input.indexOf("</firstname>"))
            var middlename: String? =
                input.substring(input.indexOf("<middlename>") + 1, input.indexOf("</middlename>"))
            middlename = if (middlename == "") null else middlename
            var phonesAsArray =
                input.substring(input.indexOf("<phone") + 1, input.lastIndexOf("</phone>")).split("<phone")
            var phones = mutableListOf<Phone>()
            phonesAsArray.forEach { phones.add(Phone.parseXML(it + "</person>")) }
            return Person(name, firstname, middlename, phones.toList())
        }
    }

    fun serializeToXML(): String {
        val middlename = if (middlename == null) "" else "<middlename>${middlename}</middlename>"
        return "<person><name>${name}</name><firstname>${firstname}</firstname>${middlename}" +
                phone.joinToString("") { it.serializeToXML() } + "</person>"
    }
}
