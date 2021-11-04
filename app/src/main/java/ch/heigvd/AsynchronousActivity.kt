package ch.heigvd

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ch.heigvd.databinding.ActivityAsynchronousBinding
import ch.heigvd.iict.sym.lab.comm.CommunicationEventListener

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
            override fun handleServerResponse(response: String) {
                binding.txtResult.text = response
            }
        })

        // Adding listeners on buttons to navigate through activities
        binding.btnSend.setOnClickListener {
            mcm.sendRequest(getString(R.string.api_txt), binding.tbxText.text.toString())
        }
    }
}