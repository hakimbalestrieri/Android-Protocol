package ch.heigvd

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ch.heigvd.databinding.ActivityMainBinding

/**
 * Main activity from which user can navigate to other activities
 * @author Allemann, Balestrieri, Gomes
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding components
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Adding listeners on buttons to navigate through activities
        binding.btnAsynchronousActivity.setOnClickListener {
            startActivity(Intent(this, AsynchronousActivity::class.java))
        }
        binding.btnDeferredActivity.setOnClickListener {
            startActivity(Intent(this, DeferredActivity::class.java))
        }
        binding.btnSerializationActivity.setOnClickListener {
            startActivity(Intent(this, SerializationActivity::class.java))
        }
        binding.btnCompressingActivity.setOnClickListener {
            startActivity(Intent(this, CompressingActivity::class.java))
        }
        binding.btnGraphQLActivity.setOnClickListener {
            startActivity(Intent(this, GraphQLActivity::class.java))
        }
    }
}