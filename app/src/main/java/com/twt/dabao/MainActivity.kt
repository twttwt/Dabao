package com.twt.dabao

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.twt.router_annotations.Destination
import com.twt.router_runtime.Router

@Destination(url = "router://page-home",description = "首页")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btn).setOnClickListener { v ->
            Router.go(
                v.context,
                "router://imooc/profile?name=imooc&message=hello"
            )
        }
    }
}