package com.zerodeg.chatrecyclerview.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zerodeg.chatrecyclerview.App
import com.zerodeg.chatrecyclerview.R
import com.zerodeg.chatrecyclerview.databinding.ChatItemBinding
import com.zerodeg.chatrecyclerview.databinding.ChatItemForReceiveBinding
import com.zerodeg.chatrecyclerview.model.ChatModel

class ReceivedMessageHolder(itemView: View, iChat: ChatInterface) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    /*
    * 의문점,
    * 예제 프로젝스에선 여기서 아이템 뷰에 바로 접근하고 있는데,
    * 사실 여기선 View를 매개변수로 가지고 오는 것이기 때문에 바로 아이디를 불러오기 어려울텐데... 뭐지..?
    * 아마도 viewBinding을 쓰냐 혹은 kotlin-extension을 사용하냐의 차이일듯?
    * */

    private var item :ChatItemForReceiveBinding? = ChatItemForReceiveBinding.bind(itemView)
    private var iChat : ChatInterface? = null

    private val profile = item?.ivProfile
    private val name = item?.tvName
    private val content = item?.tvContent

    init {
        itemView.setOnClickListener(this)
        this.iChat = iChat
    }

    override fun onClick(v: View?) {
        this.iChat?.clickChatItem(absoluteAdapterPosition)
    }

    fun bind(model: ChatModel) {
        name?.text = model.name
        content?.text = model.content
//
//        Glide
//            .with(App.instance)
//            .load(model.profile)
//            .centerCrop()
//            .placeholder(R.mipmap.ic_launcher)
//            .into(profile!!);
    }


}