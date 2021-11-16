package ch.heigvd.asynchronous

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ch.heigvd.CommunicationEventListener
import ch.heigvd.R
import ch.heigvd.SymComManager
import ch.heigvd.databinding.ActivityAsynchronousBinding

/**
 * Activity in which is realized the point about the asynchronous transmissions
 * @author Allemann, Balestrieri, Gomes
 */
class AsynchronousActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAsynchronousBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asynchronous)

        // Binding components
        binding = ActivityAsynchronousBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle response of SymComManager and update UI
        val mcm = SymComManager(object : CommunicationEventListener {
            override fun handleServerResponse(response: Any) {
                binding.txtResult.text = response as String
            }
        })

        // Adding listener on button to send data
        binding.btnSend.setOnClickListener {
            val text = binding.tbxText.text.toString()
            if (text != "") {
                mcm.sendRequest(
                    getString(R.string.api_txt),
                    text,
                    mapOf("content-type" to "text/plain")
                )
                binding.tbxText.text.clear()
            }
        }
    }
}