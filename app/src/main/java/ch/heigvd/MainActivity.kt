package ch.heigvd

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ch.heigvd.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnAsynchronousActivity.setOnClickListener {
            startActivity(
                Intent(this, AsynchronousActivity::class.java)
            )
        }

    }


}