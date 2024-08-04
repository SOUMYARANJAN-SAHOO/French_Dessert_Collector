package com.soumyaranjan.frenchdesertcollector

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.soumyaranjan.frenchdesertcollector.databinding.ActivityUserNameBinding

class UserNameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding= DataBindingUtil.setContentView<ActivityUserNameBinding>(this,R.layout.activity_user_name)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.startButton.setOnClickListener {
            val name = binding.name.text.toString()

            if(name.isNullOrEmpty()){
                binding.name.error = "Please enter your name"
                return@setOnClickListener
            }

            val intent = Intent(this,GameActivity::class.java)
            intent.putExtra("Name",name)
            startActivity(intent)
            finish()
        }
    }
}