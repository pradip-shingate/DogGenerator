package com.frankly.doggenerator.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.frankly.doggenerator.R
import com.frankly.doggenerator.models.DogRepository

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DogRepository.initCache(this)
        val generate:Button=findViewById(R.id.generate)
        generate.setOnClickListener{
            startActivity(Intent(this@MainActivity,GeneratorActivity::class.java))
        }

        val viewer:Button=findViewById(R.id.viewRecent)
        viewer.setOnClickListener{
            startActivity(Intent(this@MainActivity,ViewerActivity::class.java))
        }
    }
}