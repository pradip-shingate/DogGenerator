package com.frankly.doggenerator.view


import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.frankly.doggenerator.R
import com.frankly.doggenerator.models.CacheLru
import com.frankly.doggenerator.models.DataBaseHandler
import com.frankly.doggenerator.models.DogAdapter
import com.frankly.doggenerator.models.DogRepository
import com.google.android.material.snackbar.Snackbar


class ViewerActivity : AppCompatActivity() {
    private val cache: CacheLru = DogRepository.getCache()
    private val list = ArrayList<Bitmap>(20)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        for (str: String in cache.getLruCache()?.snapshot()?.keys!!) {
            cache.getLruCache()?.get(str)?.let { list.add(it) }
        }
        list.reverse()
        initUI()
        if (cache.getLruCache()?.size() == 0)
            showSnackBar("No recent data available.")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        initUI()
    }

    private fun initUI() {
        setContentView(R.layout.activity_viewer)

        val recyclerView: RecyclerView = findViewById(R.id.recycleView)
        val recycleLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(
            this@ViewerActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        recyclerView.layoutManager = recycleLayoutManager

        val dogAdapter = DogAdapter(list);
        recyclerView.adapter = dogAdapter

        val buttonClear: Button = findViewById(R.id.clear)
        buttonClear.setOnClickListener {
            list.clear()
            cache.getLruCache()?.evictAll()
            recyclerView.adapter?.notifyDataSetChanged()
            clearDB()
        }
    }

    private fun clearDB() {
        Thread {
            DataBaseHandler(this@ViewerActivity).deleteData()
        }.start()
    }

    private fun showSnackBar(message: String) {
        val snackBar = Snackbar.make(findViewById(R.id.rootView), message, Snackbar.LENGTH_LONG)
        snackBar.show()
    }
}