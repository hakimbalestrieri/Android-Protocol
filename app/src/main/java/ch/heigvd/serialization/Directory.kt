package ch.heigvd.serialization

import ch.heigvd.serialization.protobuf.DirectoryOuterClass
import kotlinx.serialization.Serializable

/**
 * Data class to define a directory
 */
@Serializable
data class Directory(
    var people: List<Person>
) {
    companion object {
        fun serializeAsProtoBuf(directory: Directory): ByteArray {
            var protobufDirectory = DirectoryOuterClass.Directory.newBuilder()
            directory.people.forEach {
                protobufDirectory.addResults(Person.serializeAsProtoBuf(it))
            }
            return protobufDirectory.build().toByteArray()
        }

        fun deserializeProtobuf(input: ByteArray): Directory {
            var inputProtobuf = DirectoryOuterClass.Directory.newBuilder().mergeFrom(input)
            var peopleProtobuf = inputProtobuf.resultsBuilderList
            val people: MutableList<Person> = mutableListOf()
            peopleProtobuf.forEach {
                if (it != null)
                    people.add(Person.deserializeProtobuf(it!!))
            }
            return Directory(people)
        }

        fun serializeAsXML(directory: Directory): String {
            return "TODO"
        }

        fun deserializeXML(xml: String): Directory {
            return Directory(mutableListOf())
        }
    }
}