package ch.heigvd.compressing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ch.heigvd.CommunicationEventListener
import ch.heigvd.R
import ch.heigvd.SymComManager
import ch.heigvd.databinding.ActivityCompressingBinding

/**
 * Activity in which is realized the point of the laboratory about compressing
 * @author Allemann, Balestrieri, Gomes
 */
class CompressingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompressingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compressing)

        // Binding components
        binding = ActivityCompressingBinding.inflate(layoutInflater)
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
                mcm.sendCompressedRequest(
                    getString(R.string.api_txt),
                    text,
                    mapOf(
                        "content-type" to "text/plain",
                        "X-Network" to "CSD",
                        "X-Content-Encoding" to "deflate"
                    ),
                )
                binding.tbxText.text.clear()
            }
        }
    }
}