package ch.heigvd.serialization

import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * Interface to define our own serializable methods
 * @author Allemann, Balestrieri, Gomes
 */
interface OwnSerializable<X, Y> {

    /**
     * Serialize to protobuf given object
     * @param objectToSerialize
     * @return protobuf builder
     */
    fun serializeAsProtoBuf(objectToSerialize: X): Y

    /**
     * Deserialize with protobuf a given object
     * @param valueToDeserialize
     * @return object parsed
     */
    fun deserializeProtobuf(valueToDeserialize: Y): X

    /**
     * Serialize a given protobuf object
     * @param objectToSerialize to serialize
     * @param document XML document to write into
     * @return an XML element
     */
    fun serializeAsXML(objectToSerialize: X, document: Document): Element


    /**
     * Deserialize given XML object
     * @param element to deserialize
     * @return object parsed
     */
    fun deserializeXML(element: Element): X
}