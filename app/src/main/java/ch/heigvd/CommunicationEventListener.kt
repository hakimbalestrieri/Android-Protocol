package ch.heigvd

interface CommunicationEventListener {
    fun handleServerResponse(response :String)
}