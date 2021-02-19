package com.twt.dabao

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.twt.router_annotations.Destination
import com.twt.router_runtime.Router
import java.lang.Exception

@Destination(url = "router://page-home",description = "首页")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            val appInfo=packageManager.getApplicationInfo(packageName,PackageManager.GET_META_DATA)
            val channelName=appInfo.metaData.getString("MTA_CHANNEL")
            Log.i("channel_test","channel=$channelName")
        }catch (e:Exception){

        }
        findViewById<View>(R.id.btn).setOnClickListener { v ->
            Router.go(
                v.context,
                "router://imooc/profile?name=imooc&message=hello"
            )
        }
    }
}