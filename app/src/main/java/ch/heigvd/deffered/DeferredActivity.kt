package ch.heigvd.deffered

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import ch.heigvd.CommunicationEventListener
import ch.heigvd.R
import ch.heigvd.SymComManager
import ch.heigvd.databinding.ActivityDeferredBinding
import kotlin.math.log

/**
 * Activity in which is realized the point of the laboratory about the deferred transmissions
 * @author Allemann, Balestrieri, Gomes
 */
class DeferredActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeferredBinding
    private val stringsToSend = mutableListOf<String>()
    private val logs = mutableListOf<String>()
    private lateinit var mainHandler: Handler
    private val updateTextTask = object : Runnable {
        override fun run() {
            sendData()
            mainHandler.postDelayed(this, TIMER_DELAY)
        }
    }
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deferred)

        // Binding components
        binding = ActivityDeferredBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handler to delay action on main thread
        mainHandler = Handler(Looper.getMainLooper())
        Thread {
            updateTextTask.run()
        }.start()

        // Adding listener on button to send data
        binding.btnSend.setOnClickListener {
            val text = binding.tbxText.text.toString()
            if (text != "") {
                stringsToSend.add(text)
                binding.tbxText.text.clear()
            }
        }

        // Add adapter to the list view
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, logs)
        binding.listLogs.adapter = adapter;
    }

    /**
     * Send the data
     */
    private fun sendData() {
        // Handle response of SymComManager and update UI
        val mcm = SymComManager(object : CommunicationEventListener {
            override fun handleServerResponse(response: Any) {
                logs.add(response as String)
                adapter.notifyDataSetChanged()
            }
        })

        // Send data to the server
        // Note: Here we open one connection per data item to be transmitted.
        // We could have used multiplexing and sent all the data in one request
        stringsToSend.forEach {
            mcm.sendRequest(
                getString(R.string.api_txt),
                it,
                mapOf("content-type" to "text/plain")
            )
        }
        stringsToSend.clear()
    }

    companion object {
        private const val TIMER_DELAY: Long = 10 * 1000L
    }
}