package com.frankly.doggenerator.view

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.frankly.doggenerator.R
import com.frankly.doggenerator.models.DogRepository
import com.google.android.material.snackbar.Snackbar
import java.util.*

class GeneratorActivity : AppCompatActivity(), Observer {

    private lateinit var btnGenerate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generator)
        DogRepository.addObserver(this)
        btnGenerate = findViewById(R.id.generate)
        btnGenerate.setOnClickListener {
            btnGenerate.isEnabled = false
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
            DogRepository.getDogFromRepository(this@GeneratorActivity)
        }
    }

    override fun update(o: Observable?, arg: Any?) {
        if (o is DogRepository) {
            runOnUiThread {
                if (arg is Boolean && arg) {
                    val imageView: ImageView = findViewById(R.id.imageDog)
                    imageView.setImageBitmap(o.getCurrentDog())

                } else {
                    showSnackBar("Unable to load image.")
                }
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                btnGenerate.isEnabled = true
            }
        }
    }

    private fun showSnackBar(message: String) {
        val snackBar = Snackbar.make(findViewById(R.id.rootView), message, Snackbar.LENGTH_LONG)
        snackBar.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        DogRepository.deleteObserver(this)
    }
}