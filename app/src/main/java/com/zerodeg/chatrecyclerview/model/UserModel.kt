package com.zerodeg.chatrecyclerview.model

import android.util.Log

class UserModel(var username : String?, var email:String?) {

    private val TAG = "UserModel"

    init {
        Log.d(TAG, "init: create user model")
    }

}