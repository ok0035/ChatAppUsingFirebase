package com.zerodeg.chatrecyclerview.recyclerview

import android.content.ContentValues.TAG
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.zerodeg.chatrecyclerview.R
import com.zerodeg.chatrecyclerview.model.ChatModel
import javax.sql.RowSetEvent

class ChatAdapter(iChat: ChatInterface) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var modelList : ArrayList<ChatModel>? = null
    private val chatInterface:ChatInterface = iChat

    companion object {
        const val VIEW_TYPE_RECEIVE = 0
        const val VIEW_TYPE_SEND = 1
    }

    override fun getItemViewType(position: Int): Int {

        var auth = FirebaseAuth.getInstance()
        var user = auth.currentUser
        val uid = user!!.uid
        val chat : ChatModel = this.modelList!![position]

        if(chat.uid == uid) {
            /* app에 저장되어 있는 사용자 정보 uid와 firebase에 저장되어 있는 uid를 비교해서
            *  같으면 본인이라고 판단하고 viewtype을 send로, 반대인 경우 receive로 return 해준다.
            *  즉, firebase에 저장되어 있는 uid를 가지고 와야한다.
            *  firebase에 */

            //right, sender
            return VIEW_TYPE_SEND

        } else {
            //left, receiver
            return VIEW_TYPE_RECEIVE
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when(viewType) {
            VIEW_TYPE_SEND -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_for_send, parent, false)
                return SentMessageHolder(view, chatInterface)
            }

            VIEW_TYPE_RECEIVE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_for_receive, parent, false)
                return ReceivedMessageHolder(view, chatInterface)
            }

            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_for_receive, parent, false)
                return ReceivedMessageHolder(view, chatInterface)
            }

        }

    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder.bindingAdapter?.getItemViewType(position)) {
            VIEW_TYPE_SEND -> {
                (holder as SentMessageHolder).bind(this.modelList!![position])
            }

            VIEW_TYPE_RECEIVE -> {
                (holder as ReceivedMessageHolder).bind(this.modelList!![position])
            }

            else -> {
                Log.d(TAG, "onBindViewHolder: else")
                (holder as ReceivedMessageHolder).bind(this.modelList!![position])
            }
        }


    }

    override fun getItemCount(): Int {
        return modelList!!.size
    }

    fun submitList(list : ArrayList<ChatModel>) {
        modelList = list
    }
}