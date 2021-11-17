package ch.heigvd

import android.os.Handler
import android.os.Looper
import java.io.*
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.zip.Deflater
import java.util.zip.Deflater.DEFLATED
import java.util.zip.DeflaterOutputStream
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream

/**
 * Data transmission service
 * @author Allemann, Balestrieri, Gomes
 */
class SymComManager(var communicationEventListener: CommunicationEventListener) {

    /**
     * Send a request to the server at the given URL
     * @param url where to send the request
     * @param headers map of headers to define
     * @param write function that receives the connection's output stream and writes data to it
     * @param read function that receives the connection's input stream and read data from it
     */
    private fun send(
        url: String,
        headers: Map<String, String>? = null,
        write: (outputStream: OutputStream) -> Unit,
        read: (inputStream: InputStream) -> Unit
    ) {
        // Connection configuration
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.doInput = true

        // Adding headers
        headers?.forEach { (k, v) -> connection.setRequestProperty(k, v) }

        // Sending the request
        write(connection.outputStream)

        // Check if the request was handled successfully
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {

            // Read body of the response
            read(connection.inputStream)
        }
    }

    /**
     * Send a request to the server at the given URL
     * @param url where to send the request
     * @param request text to send inside the body
     * @param headers map of headers to define
     */
    fun sendRequest(
        url: String,
        request: String,
        headers: Map<String, String>? = null,
    ) {
        Thread {
            send(url, headers, {
                val outputStream = OutputStreamWriter(
                    BufferedOutputStream(it),
                    StandardCharsets.UTF_8
                )
                outputStream.write(request)
                outputStream.close()
            }, {
                val inputStream = InputStreamReader(
                    BufferedInputStream(it),
                    StandardCharsets.UTF_8
                )
                val sb = StringBuilder()
                inputStream.forEachLine { line -> sb.append(line) }
                HANDLER.post { communicationEventListener.handleServerResponse(sb.toString()) }
            })
        }.start()
    }

    /**
     * Send a request to the server at the given URL
     * @param url where to send the request
     * @param request byte array to send inside the body
     * @param headers map of headers to define
     */
    fun sendRequest(
        url: String,
        request: ByteArray,
        headers: Map<String, String>? = null,
    ) {
        Thread {
            send(url, headers, {
                val outputStream = BufferedOutputStream(it)
                outputStream.write(request)
                outputStream.close()
            }, {
                val inputStream = BufferedInputStream(it)
                HANDLER.post { communicationEventListener.handleServerResponse(inputStream.readBytes()) }
            })
        }.start()
    }

    /**
     * Send a compressed request to the server at the given URL
     * @param url where to send the request
     * @param request string to send inside the body
     * @param headers map of headers to define
     */
    fun sendCompressedRequest(
        url: String,
        request: String,
        headers: Map<String, String>? = null,
    ) {
        Thread {
            send(url, headers, {
                val outputStream = OutputStreamWriter(
                    BufferedOutputStream(
                        DeflaterOutputStream(
                            it, Deflater(DEFLATED, true)
                        )
                    )
                )

                outputStream.write(request)
                outputStream.close()
            }, {
                val inputStream = InputStreamReader(
                    BufferedInputStream(InflaterInputStream(it, Inflater(true)))
                )
                val sb = StringBuilder()
                inputStream.forEachLine { line -> sb.append(line) }
                HANDLER.post { communicationEventListener.handleServerResponse(sb.toString()) }
            })
        }.start()
    }

    companion object {
        private val HANDLER = Handler(Looper.getMainLooper())
    }
}