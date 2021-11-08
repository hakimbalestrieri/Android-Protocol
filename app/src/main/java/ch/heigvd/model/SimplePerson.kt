package ch.heigvd.model

import kotlinx.serialization.Serializable

/**
 * Data class to define a test JSON object
 */
@Serializable
data class SimplePerson(var name : String,
                        var firstname : String)
