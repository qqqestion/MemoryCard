package ru.lebedeva.memorycard.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.lebedeva.memorycard.app.FirebaseApi
import ru.lebedeva.memorycard.app.NetworkHandler
import ru.lebedeva.memorycard.app.viewmodels.ViewModelProviderFactory
import ru.lebedeva.memorycard.databinding.ActivityMainBinding
import ru.lebedeva.memorycard.domain.MainRepository
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)

        Timber.plant(Timber.DebugTree())

        val firebase = FirebaseApi()
        val networkHandler = NetworkHandler(applicationContext)
        val repository = MainRepository(firebase, networkHandler)
        viewModelProviderFactory = ViewModelProviderFactory(repository)
    }
}