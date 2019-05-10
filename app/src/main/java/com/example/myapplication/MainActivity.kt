package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var et_user_name = findViewById(R.id.et_user_name) as EditText
        var et_password = findViewById(R.id.et_password) as EditText
        var btn_reset = findViewById(R.id.btn_reset) as Button
        var btn_submit = findViewById(R.id.btn_submit) as Button

        btn_reset.setOnClickListener {
            et_user_name.setText("")
            et_password.setText("")
        }

        btn_submit.setOnClickListener {
            val user_name = et_user_name.text;
            val password = et_password.text;


            if (user_name.isEmpty() || password.isEmpty()) {

                if (user_name.isEmpty()) {
                    et_user_name!!.error = "Enter Valid User Name"

                }
                if (password.isEmpty()) {

                    et_user_name!!.error = "Enter Valid Password"
                }

            } else {

                signup()
            }

        }

    }



    private fun signup() {


        val intent = Intent(this, AnotherActivity::class.java)
        startActivity(intent)

    }





}
