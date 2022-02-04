package com.zerodeg.chatrecyclerview.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.zerodeg.chatrecyclerview.App
import com.zerodeg.chatrecyclerview.R
import com.zerodeg.chatrecyclerview.databinding.LoginFragmentBinding
import com.zerodeg.chatrecyclerview.model.UserModel

class LoginFragment : Fragment(), View.OnClickListener {

    private val TAG = "LoginFragment"
    lateinit var binding: LoginFragmentBinding
    // TODO: Rename and change types of parameters

    private lateinit var navController: NavController

    // Write a message to the database
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LoginFragmentBinding.bind(view)
        navController = Navigation.findNavController(binding.root)

        database =
            Firebase.database("https://chatapptest-9433d-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        auth = FirebaseAuth.getInstance()

        var username = binding.etEmail.text
        var password = binding.etPassword.text

        Log.d(TAG, "onViewCreated: $username, $password")

        binding.tvSignIn.setOnClickListener(this)
        binding.tvSignUp.setOnClickListener(this)

    }

    override fun onStart() {
        super.onStart()
        // Check auth on Fragment start
        auth.currentUser?.let {
            onAuthSuccess(it)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tvSignIn -> {
                signIn()
            }
            R.id.tvSignUp -> {
                signUp()
            }
        }
    }

    private fun signIn() {
        Log.d(TAG, "signIn")
        if (!validateForm()) {
            Toast.makeText(App.instance.applicationContext, "유효하지 않은 형식입니다.", Toast.LENGTH_SHORT).show()
            return
        }

//        showProgressBar()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                Log.d(TAG, "signIn:onComplete:" + task.isSuccessful)
//                hideProgressBar()

                if (task.isSuccessful) {
                    onAuthSuccess(task.result?.user!!)
                } else {
                    Toast.makeText(context, "Sign In Failed",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signUp() {
        Log.d(TAG, "signUp")
        if (!validateForm()) {
            Toast.makeText(App.instance.applicationContext, "유효하지 않은 형식입니다.", Toast.LENGTH_SHORT).show()
            return
        }

//        showProgressBar()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                Log.d(TAG, "createUser:onComplete:" + task.isSuccessful)
//                hideProgressBar()

                if (task.isSuccessful) {
                    onAuthSuccess(task.result?.user!!)
                } else {
                    Toast.makeText(context, "Sign Up Failed",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun onAuthSuccess(user: FirebaseUser) {
        val username = usernameFromEmail(user.email!!)
        Log.d(TAG, "onAuthSuccess: $username")
        // Write new user
        writeNewUser(user.uid, username, user.email)

        // Go to MainFragment
        findNavController().navigate(R.id.action_loginFragment_to_chatFragment)
    }

    private fun usernameFromEmail(email: String): String {
        return if (email.contains("@")) {
            email.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        } else {
            email
        }
    }

    private fun validateForm(): Boolean {
        var result = true
        if (TextUtils.isEmpty(binding.etEmail.text.toString())) {
            binding.etEmail.error = "Required"
            result = false
        } else {
            binding.etEmail.error = null
        }

        if (TextUtils.isEmpty(binding.etPassword.text.toString())) {
            binding.etPassword.error = "Required"
            result = false
        } else {
            binding.etPassword.error = null
        }

        Log.d(TAG, "validateForm: $result")

        return result
    }

    private fun writeNewUser(userId: String, name: String, email: String?) {
        val user = UserModel(name, email)
        database.child("users").child(userId).setValue(user)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null!!
    }
}