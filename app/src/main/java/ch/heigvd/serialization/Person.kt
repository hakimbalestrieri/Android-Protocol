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

        override fun serializeAsXML(person: Person): String {
            return "TODO"
        }

        override fun deserializeXML(xml: String): Person {
            return Person("", "", "", mutableListOf())
        }
    }
}
