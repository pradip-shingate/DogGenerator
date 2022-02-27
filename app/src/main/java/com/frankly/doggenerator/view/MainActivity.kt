package com.frankly.doggenerator.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.frankly.doggenerator.R
import com.frankly.doggenerator.models.DogRepository


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DogRepository.initCache(this)
        val generate: Button = findViewById(R.id.generate)
        generate.setOnClickListener {
            startActivity(Intent(this@MainActivity, GeneratorActivity::class.java))
        }

        val viewer: Button = findViewById(R.id.viewRecent)
        viewer.setOnClickListener {
            startActivity(Intent(this@MainActivity, ViewerActivity::class.java))
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            handleConfigChange(80)

        } else {
            handleConfigChange(150)
        }
    }

    private fun handleConfigChange(margin: Int) {
        val textView = findViewById<TextView>(R.id.title)
        val layoutParams = textView.layoutParams as MarginLayoutParams
        layoutParams.bottomMargin = margin
        textView.layoutParams = layoutParams
    }
}