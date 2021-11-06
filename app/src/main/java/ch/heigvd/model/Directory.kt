package ch.heigvd.model

import kotlinx.serialization.Serializable

/**
 * Data class to define a directory
 */
@Serializable
data class Directory(var people: MutableList<Person> = mutableListOf())