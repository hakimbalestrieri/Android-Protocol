package ch.heigvd.serialization

import kotlinx.serialization.Serializable

/**
 * Data class to define a directory
 */
@Serializable
data class Directory(var people: MutableList<Person> = mutableListOf()) {
    companion object {
        fun parseXML(input: String): Directory {
            var peopleAsArray =
                input.substring(input.indexOf("<person"), input.lastIndexOf("</person>") + 9)
                    .split("<person")
            var people = mutableListOf<Person>()
            peopleAsArray.forEach { people.add(Person.parseXML(it + "</person>")) }
            return Directory(people)
        }
    }

    fun serializeToXML(): String {
        return "<directory>" + people.joinToString("") { it.serializeToXML() } + "</directory>"
    }
}