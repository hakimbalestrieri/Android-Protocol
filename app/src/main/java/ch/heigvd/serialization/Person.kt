package ch.heigvd.serialization

import ch.heigvd.serialization.protobuf.DirectoryOuterClass
import kotlinx.serialization.Serializable

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
    companion object {
        fun serializeAsProtoBuf(person: Person): DirectoryOuterClass.Person.Builder? {
            val protobufPerson = DirectoryOuterClass.Person.newBuilder()
                .setFirstname(person.firstname)
                .setMiddlename(person.middlename)
                .setName(person.name)
            person.phone.forEach { protobufPerson.addPhone(Phone.serializeAsProtoBuf(it)) }
            return protobufPerson
        }

        fun deserializeProtobuf(personProtoBuf: DirectoryOuterClass.Person.Builder): Person {
            val phones: MutableList<Phone> = mutableListOf()
            personProtoBuf.phoneList.forEach {
                if (it != null)
                    phones.add(Phone.deserializeProtobuf(it!!))
            }
            return Person(
                personProtoBuf.name,
                personProtoBuf.firstname,
                personProtoBuf.middlename,
                phones
            )
        }

        fun serializeAsXML(person: Person): String {
            return "TODO"
        }

        fun deserializeXML(xml: String): Person {
            return Person("", "","", mutableListOf())
        }
    }
}
