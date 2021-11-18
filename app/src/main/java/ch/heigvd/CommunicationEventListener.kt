package ch.heigvd

/**
 * Communication listener to handle server response
 */
interface CommunicationEventListener {

    /**
     * Handle server response
     * @param response to handle
     */
    fun handleServerResponse(response: Any)
}