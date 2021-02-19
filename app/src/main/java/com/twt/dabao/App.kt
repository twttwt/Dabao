package com.twt.dabao

import android.app.Application
import com.twt.router_runtime.Router

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        Router.init()
    }
}