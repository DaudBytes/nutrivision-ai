package com.example.foodclassifierapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.material.button.MaterialButton

class SetupActivity : ComponentActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)

        val etName = findViewById<EditText>(R.id.etName)
        val spinnerCondition = findViewById<Spinner>(R.id.spinnerCondition)
        val checkDisclaimer = findViewById<CheckBox>(R.id.checkDisclaimer)
        val btnContinue = findViewById<MaterialButton>(R.id.btnContinue)

        val conditions = listOf("obesity", "diabetes", "hypertension")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, conditions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCondition.adapter = adapter

        btnContinue.setOnClickListener {
            val name = etName.text.toString().trim()
            val condition = spinnerCondition.selectedItem.toString()

            if (!checkDisclaimer.isChecked) {
                Toast.makeText(this, "Please accept the disclaimer first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.edit()
                .putString("user_name", if (name.isEmpty()) "User" else name)
                .putString("user_condition", condition)
                .putBoolean("setup_complete", true)
                .apply()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}