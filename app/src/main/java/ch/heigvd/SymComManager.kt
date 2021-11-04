package ch.heigvd

import android.os.Handler
import android.os.Looper
import ch.heigvd.iict.sym.lab.comm.CommunicationEventListener
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

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
        val handler = Handler(Looper.getMainLooper())
        Thread {
            handler.post(Runnable {
                val connection =
                    URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doInput = true
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "text/plain")
                val os: OutputStream = connection.outputStream
                val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
                writer.write(request)
                writer.flush()
                writer.close()
                os.close()
                val responseCode: Int = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = DataInputStream(connection.inputStream)
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val output = reader.readLine()
                    communicationEventListener.handleServerResponse(output)
                }
            })
        }
    }
}