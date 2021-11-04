package ch.heigvd

import ch.heigvd.iict.sym.lab.comm.CommunicationEventListener

/**
 * Asynchronous transmission service
 * @author Allemann, Balestrieri, Gomes
 */
class SymComManager() {

    /**
     * Send request text to the server designated by url
     * @param url where to send the request
     * @param request text to send
     */
    fun sendRequest(url: String, request: String) {
        //TODO implement
    }

    /**
     * Define a listener that will be invoked when the response reaches the client
     * @param l the listener to invoke
     */
    fun setCommunicationEventListener(l: CommunicationEventListener?) {
        // TODO implement
    }
}