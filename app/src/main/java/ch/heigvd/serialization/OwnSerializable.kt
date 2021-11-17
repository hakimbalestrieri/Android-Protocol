package ch.heigvd.serialization

interface OwnSerializable<X, Y> {
    fun serializeAsProtoBuf(valueToSerialize: X): Y
    fun deserializeProtobuf(valueToDeserialize: Y): X
    fun serializeAsXML(valueToDeserialize: X): String
    fun deserializeXML(valueToDeserialize: String): X
}
