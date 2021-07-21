package com.dsmmachina.marvelmadness.application

import android.app.Application
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        buildPicasso()
    }

    // let's build our Picasso singleton and give it our app context
    private fun buildPicasso() {
        val builder = Picasso.Builder(this)
        builder.downloader(OkHttp3Downloader(this, Long.MAX_VALUE))

        val built = builder.build()
        built.setIndicatorsEnabled(false)
        built.isLoggingEnabled = true
        Picasso.setSingletonInstance(built)
    }

}