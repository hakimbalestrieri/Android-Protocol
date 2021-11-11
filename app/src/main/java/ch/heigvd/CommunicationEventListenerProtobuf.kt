package ch.heigvd

interface CommunicationEventListenerProtobuf {
    fun handleServerResponseByteArray(response: ByteArray)
}