package ch.heigvd

import android.os.Handler
import android.os.Looper
import ch.heigvd.iict.sym.lab.comm.CommunicationEventListener
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * Asynchronous transmission service
 * @author Allemann, Balestrieri, Gomes
 */
class SymComManager(var communicationEventListener: CommunicationEventListener) {

    /**
     * Send request text to the server designated by url
     * @param url where to send the request
     * @param request text to send
     * @param contentType content type of the request body
     */
    fun sendRequest(url: String, request: String, contentType: String) {
        val handler = Handler(Looper.getMainLooper())
        Thread {
            // Connection configuration
            val url = URL(url)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.doInput = true

            // Headers of the request
            val postData: ByteArray = request.toByteArray(StandardCharsets.UTF_8)
            connection.setRequestProperty("charset", "utf-8")
            connection.setRequestProperty("Content-length", postData.size.toString())
            connection.setRequestProperty("Content-Type", contentType)

            // Send the text
            val outputStream = DataOutputStream(connection.outputStream)
            outputStream.write(postData)
            outputStream.flush()

            // Check if the request was handled successfully
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {

                // Read body of the response
                val inputStream = DataInputStream(connection.inputStream)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val output: String = reader.readLine()

                // Send back the data to the activity
                handler.post { communicationEventListener.handleServerResponse(output) }
            }
        }.start()
    }
}