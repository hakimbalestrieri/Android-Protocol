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
     */
    fun sendRequest(url: String, request: String) {
        Thread {
            val handler = Handler(Looper.getMainLooper())
            handler.post(object : Runnable {
                override fun run() {
                    val url = URL(url)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "POST"
                    connection.connectTimeout = 300000
                    connection.connectTimeout = 300000
                    connection.doOutput = true
                    connection.doInput = true

                    val postData: ByteArray = request.toByteArray(StandardCharsets.UTF_8)

                    connection.setRequestProperty("charset", "utf-8")
                    connection.setRequestProperty("Content-length", postData.size.toString())
                    connection.setRequestProperty("Content-Type", "text/plain")

                    try {
                        val outputStream = DataOutputStream(connection.outputStream)
                        outputStream.write(postData)
                        outputStream.flush()
                        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                            try {
                                val inputStream = DataInputStream(connection.inputStream)
                                val reader = BufferedReader(InputStreamReader(inputStream))
                                val output: String = reader.readLine()
                                communicationEventListener.handleServerResponse(output)
                            } catch (exception: Exception) {
                                throw Exception("Exception while push the notification  $exception.message")
                            }
                        }
                    } catch (exception: Exception) {
                    }
                }
            })
        }.start()
    }
}