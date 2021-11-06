package ch.heigvd

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import ch.heigvd.databinding.ActivityDeferredBinding

/**
 * Activity in which is realized the point of the laboratory about the deferred transmissions
 * @author Allemann, Balestrieri, Gomes
 */
class DeferredActivity : AppCompatActivity() {
    companion object {
        private const val TIMER_DELAY: Long = 10000
    }

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
        // Note : Here, we could imagine sending the data to the server,
        // waiting for a response and then updating the UI.
        stringsToSend.forEach { logs.add(it) }
        adapter.notifyDataSetChanged()
        stringsToSend.clear()
    }
}