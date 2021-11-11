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
) {}
