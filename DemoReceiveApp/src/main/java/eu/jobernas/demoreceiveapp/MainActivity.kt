package eu.jobernas.demoreceiveapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import eu.jobernas.demoreceiveapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.apply {
            setContentView(root)
            setSupportActionBar(toolbar)
            mainViewModel.apply {
                demoReceiveButton.setOnClickListener(this)
                handleImage(applicationContext, intent)
                onImageReceived.observe(this@MainActivity) {
                    demoReceiveImage.setImageURI(it)
                }
            }
        }
    }
}