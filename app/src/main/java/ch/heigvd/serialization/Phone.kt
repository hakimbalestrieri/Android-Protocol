package ch.heigvd.serialization

import ch.heigvd.serialization.protobuf.DirectoryOuterClass
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Data class to define a phone
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

    companion object {
        fun serializeAsProtoBuf(phone: Phone): DirectoryOuterClass.Phone.Builder? {
            return DirectoryOuterClass.Phone.newBuilder()
                .setNumber(phone.number)
                .setType(DirectoryOuterClass.Phone.Type.valueOf(phone.type.toString()))
        }

        fun deserializeProtobuf(phoneProtobuf: DirectoryOuterClass.Phone): Phone {
            return Phone(Type.values()[phoneProtobuf.type.ordinal], phoneProtobuf.number)
        }

        fun serializeAsXML(phone: Phone): String {
            return "TODO"
        }

        fun deserializeXML(xml: String): Phone {
            return Phone(Type.WORK, "")
        }
    }
}