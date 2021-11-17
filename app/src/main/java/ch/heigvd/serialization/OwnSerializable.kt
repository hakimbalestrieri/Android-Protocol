package ch.heigvd.serialization

import org.w3c.dom.Document
import org.w3c.dom.Element

interface OwnSerializable<X, Y> {
    fun serializeAsProtoBuf(valueToSerialize: X): Y
    fun deserializeProtobuf(valueToDeserialize: Y): X
    fun serializeAsXML(valueToDeserialize: X, document : Document): Element
    fun deserializeXML(element: Element): X
}