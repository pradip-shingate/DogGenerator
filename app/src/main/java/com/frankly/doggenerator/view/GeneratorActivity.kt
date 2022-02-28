package com.frankly.doggenerator.view

import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.frankly.doggenerator.R
import com.frankly.doggenerator.models.DataBaseHandler
import com.frankly.doggenerator.models.DogRepository
import com.google.android.material.snackbar.Snackbar
import java.util.*

class GeneratorActivity : AppCompatActivity(), Observer, View.OnClickListener {

    private lateinit var btnGenerate: Button
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
        DogRepository.addObserver(this)
    }

    override fun update(obsrvable: Observable?, arg: Any?) {
        if (obsrvable is DogRepository) {
            runOnUiThread {
                if (arg is Boolean && arg) {
                    val imageView: ImageView = findViewById(R.id.imageDog)
                    bitmap = obsrvable.getCurrentDog()
                    imageView.setImageBitmap(bitmap)

                } else if (arg is String) {
                    DataBaseHandler(this@GeneratorActivity).deleteEntry(arg)
                } else {
                    showSnackBar("Unable to load image.")
                }
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                btnGenerate.isEnabled = true
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        initUI()
        bitmap?.let {
            val imageView: ImageView = findViewById(R.id.imageDog)
            imageView.setImageBitmap(it)
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

    override fun onClick(v: View?) {
        if (v?.id == R.id.generate) {
            btnGenerate.isEnabled = false
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
            DogRepository.getDogFromRepository(this@GeneratorActivity)
        }
    }

    private fun initUI() {
        setContentView(R.layout.activity_generator)
        btnGenerate = findViewById(R.id.generate)
        btnGenerate.setOnClickListener(this)
    }
}