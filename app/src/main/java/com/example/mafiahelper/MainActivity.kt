package com.example.mafiahelper

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val MainHeader: TextView = findViewById(R.id.main_header)
        val SlartLoader: ProgressBar = findViewById(R.id.progressBar)

        val db = DbHelper(context = this, factory = null)
        db.printAllIcons()
    }





}