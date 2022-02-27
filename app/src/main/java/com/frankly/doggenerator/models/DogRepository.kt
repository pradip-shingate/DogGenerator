package com.frankly.doggenerator.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import java.io.IOException
import java.lang.reflect.Type
import java.util.*


object DogRepository : Observable() {

    private lateinit var currentDogBitmap: Bitmap


    // Get max available VM memory, exceeding this amount will throw an
    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
    // int in its constructor.
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

    private val cacheLru: CacheLru = CacheLru(maxMemory / 8)


    fun getCurrentDog(): Bitmap {
        return currentDogBitmap
    }

    fun getCache(): CacheLru {
        return cacheLru
    }

    fun getDogFromRepository(context: Context) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://dog.ceo/api/breeds/image/random")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(request: Request?, e: IOException?) {
                handleImageFailure()
            }

            override fun onResponse(response: Response?) {
                val type: Type = object : TypeToken<DogSource?>() {}.type
                val currentDog: DogSource? = Gson().fromJson(response?.body()!!.string(), type)
                currentDog?.status?.let {
                    if (TextUtils.equals(it, "success"))
                        downloadImage(context, currentDog)
                }

            }
        })
    }

    fun downloadImage(context: Context, dogSource: DogSource) {
        dogSource.message?.let {
            Glide.with(context)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .load(it)
                .override(300, 250)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        currentDogBitmap = resource
                        putInCache(context, it, currentDogBitmap)
                        setChanged()
                        notifyObservers(true)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        handleImageFailure()
                    }
                })

        }
    }

    private fun handleImageFailure() {
        setChanged()
        notifyObservers(false)
    }

    fun initCache(context: Context) {
        Thread {
            DataBaseHandler(context).getFromDB(cacheLru)
        }.start()
    }

    private fun putInCache(context: Context, url: String, bitmap: Bitmap) {
        Thread {
            cacheLru.getLruCache()?.put(url, currentDogBitmap)
            DataBaseHandler(context).putInDB(url, currentDogBitmap)
        }.start()
    }
}