package ch.heigvd

import android.os.Handler
import android.os.Looper
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.zip.Deflater
import java.util.zip.Deflater.DEFLATED
import java.util.zip.DeflaterOutputStream
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream

/**
 * Asynchronous transmission service for Protobuf protocol
 * @author Allemann, Balestrieri, Gomes
 */
class SymComManagerProtobuf(var communicationEventListenerProtobuf: CommunicationEventListenerProtobuf) {

    /**
     * Send request text to the server designated by url
     * @param url where to send the request
     * @param request text to send
     * @param contentType content type of the request body
     * @param headers additional headers
     */
    fun sendRequest(
        url: String,
        request: ByteArray,
        contentType: String,
    ) {
        val handler = Handler(Looper.getMainLooper())
        Thread {
            // Connection configuration
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.doInput = true

            // Headers of the request
            //val postData: ByteArray = request.toByteArray(StandardCharsets.UTF_8)
            connection.setRequestProperty("charset", "utf-8")
            connection.setRequestProperty("Content-Type", contentType)

            // Send the bytes
            var outputStream = BufferedOutputStream(connection.outputStream)
            outputStream.write(request)
            outputStream.close()

            // Check if the request was handled successfully
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {

                // Read body of the response
                var inputStream = DataInputStream(connection.inputStream) as InputStream

                var result = inputStream.readBytes()

                // Send back the data to the activity
                handler.post { communicationEventListenerProtobuf.handleServerResponseByteArray(result) }
            }
        }.start()
    }
}