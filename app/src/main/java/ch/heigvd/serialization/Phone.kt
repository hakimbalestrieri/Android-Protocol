package ch.heigvd.serialization

import ch.heigvd.serialization.protobuf.DirectoryOuterClass
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * Data class to define a phone
 * @author Allemann, Balestrieri, Gomes
 */
@Serializable
data class Phone(var type: Type, var number: String) {
    @Serializable(with = TypeSerializer::class)
    enum class Type {
        HOME, MOBILE, WORK
    }

    @Serializer(forClass = Type::class)
    object TypeSerializer {
        override fun deserialize(decoder: Decoder): Type {
            val result = decoder.decodeString().split(" ")
            return Type.valueOf(result[0])
        }

        override fun serialize(encoder: Encoder, obj: Type) {
            encoder.encodeString(obj.name)
        }
    }

    companion object : OwnSerializable<Phone, DirectoryOuterClass.Phone.Builder> {
        override fun serializeAsProtoBuf(phone: Phone): DirectoryOuterClass.Phone.Builder {
            return DirectoryOuterClass.Phone.newBuilder()
                .setNumber(phone.number)
                .setType(DirectoryOuterClass.Phone.Type.valueOf(phone.type.toString()))
        }

        override fun deserializeProtobuf(phoneProtobuf: DirectoryOuterClass.Phone.Builder): Phone {
            return Phone(Type.values()[phoneProtobuf.type.ordinal], phoneProtobuf.number)
        }

        override fun serializeAsXML(phone: Phone, document : Document): Element {
            val phoneElement = document.createElement("phone")
            phoneElement.setAttribute("type", phone.type.name.lowercase())
            phoneElement.appendChild(document.createTextNode(phone.number))
            return phoneElement
        }

        override fun deserializeXML(element: Element): Phone {
            val type = Type.valueOf(element.getAttribute("type").uppercase())
            return Phone(type, element.firstChild.nodeValue)
        }
    }
}