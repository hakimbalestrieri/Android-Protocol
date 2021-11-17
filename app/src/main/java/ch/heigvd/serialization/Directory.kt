package ch.heigvd.serialization

import ch.heigvd.serialization.protobuf.DirectoryOuterClass
import kotlinx.serialization.Serializable
import org.w3c.dom.Document
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Data class to define a directory
 */
@Serializable
data class Directory(
    var people: List<Person>
) {
    companion object : OwnSerializable<Directory, ByteArray> {
        override fun serializeAsProtoBuf(directory: Directory): ByteArray {
            var protobufDirectory = DirectoryOuterClass.Directory.newBuilder()
            directory.people.forEach {
                protobufDirectory.addResults(Person.serializeAsProtoBuf(it))
            }
            return protobufDirectory.build().toByteArray()
        }

        override fun deserializeProtobuf(input: ByteArray): Directory {
            var inputProtobuf = DirectoryOuterClass.Directory.newBuilder().mergeFrom(input)
            var peopleProtobuf = inputProtobuf.resultsBuilderList
            val people: MutableList<Person> = mutableListOf()
            peopleProtobuf.forEach {
                people.add(Person.deserializeProtobuf(it))
            }
            return Directory(people)
        }

        override fun serializeAsXML(directory: Directory, document : Document): Element {
            val directoryElement = document.createElement("directory")
            directory.people.forEach {
                directoryElement.appendChild(Person.serializeAsXML(it, document))
            }
            return directoryElement
        }

        override fun deserializeXML(xml: String): Directory {
            return Directory(mutableListOf())
        }
    }
}