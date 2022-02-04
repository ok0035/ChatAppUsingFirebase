package com.zerodeg.chatrecyclerview.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.zerodeg.chatrecyclerview.R
import com.zerodeg.chatrecyclerview.databinding.ChatFragmentBinding
import com.zerodeg.chatrecyclerview.firebase.ChatData
import com.zerodeg.chatrecyclerview.recyclerview.ChatAdapter
import com.zerodeg.chatrecyclerview.recyclerview.ChatInterface
import com.zerodeg.chatrecyclerview.model.ChatModel
import java.util.*

class ChatFragment : Fragment(), ChatInterface, View.OnClickListener, View.OnKeyListener {
    private val TAG = "ChatFragment"
    lateinit var binding: ChatFragmentBinding
    lateinit var navController: NavController

    private val modelList = ArrayList<ChatModel>()
    private lateinit var chatAdapter: ChatAdapter

    // Write a message to the database
    private val database =
        Firebase.database("https://chatapptest-9433d-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val chatRef = database.getReference("chat")
    private val usersRef = database.getReference("users")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.chat_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ChatFragmentBinding.bind(view)
        navController = Navigation.findNavController(binding.root)
        Log.d(TAG, "onViewCreated: called")

        binding.tvSend.setOnClickListener(this)
        binding.etContent.setOnKeyListener(this)

        chatAdapter = ChatAdapter(this)
        chatAdapter.submitList(modelList)

        binding.recyclerView.apply {
            adapter = chatAdapter
            layoutManager =
                LinearLayoutManager(this@ChatFragment.context, LinearLayoutManager.VERTICAL, false)
        }

        // Write a message to the database
//        val database = Firebase.database
//        val myRef = database.getReference("message")

        /*앱 실행시 딱 한 번만 받아오는 이벤트
        * 지난 채팅 기록을 가져올때 아주 유용함 */
        chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                Log.d(TAG, "onDataChangeForSingleValue: called")

                val value = dataSnapshot.children
                value.forEach {
                    val data = it.getValue<ChatData>()
                    val uid = data?.uid
                    val name = data?.username
                    val content = data?.content
                    addChatItem(uid, name, content)
//                    Log.d(TAG, "Received message: ${it.toString()}")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

        chatRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded: called -> $snapshot $previousChildName")
                val data = snapshot.getValue<ChatData>()
                addChatItem(data?.uid, data?.username, data?.content)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged: called -> $snapshot $previousChildName")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved: called -> $snapshot")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved: called -> $snapshot $previousChildName")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: called -> ${error.toException()}")
            }

        })

    }


    @SuppressLint("NotifyDataSetChanged")
    fun addChatItem(uid: String?, name: String?, content: String?) {
        val model: ChatModel = ChatModel(uid, name, content)
        this.modelList.add(model)
        binding.recyclerView.scrollToPosition(modelList.size - 1)
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun clickChatItem(pos: Int) {
        Log.d(TAG, "clickChatItem: called")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvSend -> {
                var content = binding.etContent.text.toString()
                binding.etContent.setText("")
                writeMessage(content)
            }
        }
    }

    private fun writeMessage(content: String) {
        Log.d(TAG, "writeMessage : " + content)

        var auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user?.uid
        usersRef.child(uid.toString()).get().addOnSuccessListener { it ->
            Log.d(TAG, "writeMessage: username -> ${it.getValue<ChatData>()!!.username}")
            val username = it.getValue<ChatData>()?.username
            val chatData = ChatData(uid, username, content)
//        database.getReference("chat").child(Date().time.toString()).setValue(chatData)
            val date = Date().time.toString()
            chatRef.child(date).setValue(chatData)
        }
    }

    /*
    *
    * sendMessage 메소드에는 문제가 없다. (onClick 이벤트에서는 정상작동)
    * EditText에서 개행이 되면서 문제가 발생하는 듯 하다.
    * */

    override fun onKey(view: View?, keycode: Int, event: KeyEvent?): Boolean {

        when (keycode) {
            KeyEvent.KEYCODE_ENTER -> {
                if (binding.etContent.text.toString() == "" || binding.etContent.text.toString() == "\n") return false
                var content = binding.etContent.text.toString()
                binding.etContent.setText("")
                writeMessage(content)
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}