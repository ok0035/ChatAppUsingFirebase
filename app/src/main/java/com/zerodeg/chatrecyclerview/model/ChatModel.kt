package com.zerodeg.chatrecyclerview.model

import android.util.Log

class ChatModel(var uid:String?, var name : String? = null, var content : String? = null) {

    private val TAG = "ChatModel"

    init {
        Log.d(TAG, "init: create chat model")
    }

}