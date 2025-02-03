package com.example.ballin

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ColorPickerActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var selectedColorName: String = "Brak"
    private var selectedColorDrawable: Int = R.drawable.benson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_picker)

        val selectedBallNameTextView = findViewById<TextView>(R.id.selectedBallName)

        findViewById<ImageView>(R.id.imageBenson).setOnClickListener {
            updateSelection("Benson", R.drawable.benson, selectedBallNameTextView)
        }

        findViewById<ImageView>(R.id.imageBluson).setOnClickListener {
            updateSelection("Bluson", R.drawable.bluson, selectedBallNameTextView)
        }

        findViewById<ImageView>(R.id.imageGreenson).setOnClickListener {
            updateSelection("Greenson", R.drawable.greenson, selectedBallNameTextView)
        }

        findViewById<ImageView>(R.id.imageRoson).setOnClickListener {
            updateSelection("Roson", R.drawable.roson, selectedBallNameTextView)
        }

        findViewById<ImageView>(R.id.imageYellson).setOnClickListener {
            updateSelection("Yellson", R.drawable.yellson, selectedBallNameTextView)
        }
    }

    private fun updateSelection(ballName: String, ballDrawable: Int, textView: TextView) {
        textView.text = "Wybrana kulka: $ballName"

        val sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("selected_ball_name", ballName)
            putInt("selected_ball_drawable", ballDrawable)
        }.apply()

        finish()
    }

    private fun saveSelectedColor() {
        sharedPreferences.edit().apply {
            putString("selected_ball_name", selectedColorName)
            putInt("selected_ball_drawable", selectedColorDrawable)
        }.apply()

        Toast.makeText(this, "Kolor $selectedColorName zapisany!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
