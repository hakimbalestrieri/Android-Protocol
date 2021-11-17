package ch.heigvd.serialization

import ch.heigvd.serialization.protobuf.DirectoryOuterClass
import kotlinx.serialization.Serializable
import org.w3c.dom.Document
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Data class to define a person
 */
@Serializable
data class Person(
    var name: String,
    var firstname: String,
    var middlename: String,
    var phone: List<Phone>
) {
    companion object : OwnSerializable<Person, DirectoryOuterClass.Person.Builder> {
        override fun serializeAsProtoBuf(person: Person): DirectoryOuterClass.Person.Builder {
            val protobufPerson = DirectoryOuterClass.Person.newBuilder()
                .setFirstname(person.firstname)
                .setMiddlename(person.middlename)
                .setName(person.name)
            person.phone.forEach { protobufPerson.addPhone(Phone.serializeAsProtoBuf(it)) }
            return protobufPerson
        }

        override fun deserializeProtobuf(personProtoBuf: DirectoryOuterClass.Person.Builder): Person {
            val phones: MutableList<Phone> = mutableListOf()
            personProtoBuf.phoneList.forEach {
                if (it != null)
                    phones.add(Phone.deserializeProtobuf(it.toBuilder()))
            }
            return Person(
                personProtoBuf.name,
                personProtoBuf.firstname,
                personProtoBuf.middlename,
                phones
            )
        }

        override fun serializeAsXML(person: Person, document : Document): Element {
            val personElement = document.createElement("person")
            val nameElement = document.createElement("name")
            nameElement.appendChild(document.createTextNode(person.name))
            personElement.appendChild(nameElement)
            val firstnameElement = document.createElement("firstname")
            firstnameElement.appendChild(document.createTextNode(person.firstname))
            personElement.appendChild(firstnameElement)
            val middlenameElement = document.createElement("middlename")
            middlenameElement.appendChild(document.createTextNode(person.middlename))
            personElement.appendChild(middlenameElement)
            person.phone.forEach {
                personElement.appendChild(Phone.serializeAsXML(it, document))
            }
            return personElement
        }

        override fun deserializeXML(xml: String): Person {
            return Person("", "", "", mutableListOf())
        }
    }
}
