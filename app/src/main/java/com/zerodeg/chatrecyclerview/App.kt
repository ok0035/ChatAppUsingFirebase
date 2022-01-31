package com.zerodeg.chatrecyclerview

import android.app.Application

class App : Application(){

    //java에서 static을 정의하는 것과 같은 효과를 줌
    companion object {
        lateinit var instance : App
            private set // get에만 접근가능하도록 하는 용도인듯 default가 public이기 때문에
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}