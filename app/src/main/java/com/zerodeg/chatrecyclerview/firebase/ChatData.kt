package com.zerodeg.chatrecyclerview.firebase

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ChatData(val uid: String? = null, val username: String? = null, val content: String? = null) {

    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}
