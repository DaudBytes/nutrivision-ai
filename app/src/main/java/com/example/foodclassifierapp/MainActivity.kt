package com.example.foodclassifierapp

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class MainActivity : ComponentActivity() {

    private lateinit var classifier: FoodClassifier
    private lateinit var prefs: SharedPreferences

    private lateinit var imagePreview: ImageView
    private lateinit var tvFoodName: TextView
    private lateinit var tvConfidence: TextView
    private lateinit var tvUserName: TextView
    private lateinit var tvCondition: TextView
    private lateinit var tvCalories: TextView
    private lateinit var tvProtein: TextView
    private lateinit var tvCarbs: TextView
    private lateinit var tvFat: TextView
    private lateinit var tvSalt: TextView
    private lateinit var tvAdvice: TextView
    private lateinit var tvImageHint: TextView
    private lateinit var progressConfidence: ProgressBar
    private lateinit var btnSelectImage: MaterialButton
    private lateinit var btnCamera: MaterialButton
    private lateinit var btnEditProfile: MaterialButton
    private lateinit var cardPrediction: MaterialCardView
    private lateinit var cardNutrition: MaterialCardView
    private lateinit var cardAdvice: MaterialCardView

    private var userName: String = "User"
    private var userCondition: String = "diabetes"

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                try {
                    val bitmap: Bitmap =
                        MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    handleImage(bitmap)
                } catch (e: Exception) {
                    showErrorState(e)
                }
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            try {
                if (bitmap != null && bitmap.width > 0 && bitmap.height > 0) {
                    handleImage(bitmap)
                } else {
                    cardAdvice.visibility = View.VISIBLE
                    tvAdvice.text = "No valid image was captured from the camera."
                }
            } catch (e: Exception) {
                showErrorState(e)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)

        val setupComplete = prefs.getBoolean("setup_complete", false)
        if (!setupComplete) {
            startActivity(Intent(this, SetupActivity::class.java))
            finish()
            return
        }

        userName = prefs.getString("user_name", "User") ?: "User"
        userCondition = prefs.getString("user_condition", "diabetes") ?: "diabetes"

        setContentView(R.layout.activity_main)

        classifier = FoodClassifier(this)

        imagePreview = findViewById(R.id.imagePreview)
        tvFoodName = findViewById(R.id.tvFoodName)
        tvConfidence = findViewById(R.id.tvConfidence)
        tvUserName = findViewById(R.id.tvUserName)
        tvCondition = findViewById(R.id.tvCondition)
        tvCalories = findViewById(R.id.tvCalories)
        tvProtein = findViewById(R.id.tvProtein)
        tvCarbs = findViewById(R.id.tvCarbs)
        tvFat = findViewById(R.id.tvFat)
        tvSalt = findViewById(R.id.tvSalt)
        tvAdvice = findViewById(R.id.tvAdvice)
        tvImageHint = findViewById(R.id.tvImageHint)
        progressConfidence = findViewById(R.id.progressConfidence)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnCamera = findViewById(R.id.btnCamera)
        btnEditProfile = findViewById(R.id.btnEditProfile)
        cardPrediction = findViewById(R.id.cardPrediction)
        cardNutrition = findViewById(R.id.cardNutrition)
        cardAdvice = findViewById(R.id.cardAdvice)

        cardPrediction.visibility = View.GONE
        cardNutrition.visibility = View.GONE
        cardAdvice.visibility = View.GONE
        tvImageHint.visibility = View.VISIBLE

        tvUserName.text = "User: $userName"
        tvCondition.text =
            "Condition: ${formatFoodName(userCondition).replaceFirstChar { it.uppercase() }}"

        btnSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnCamera.setOnClickListener {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                cardAdvice.visibility = View.VISIBLE
                tvAdvice.text = "Camera is not available on this device or emulator."
                e.printStackTrace()
            }
        }

        btnEditProfile.setOnClickListener {
            startActivity(Intent(this, SetupActivity::class.java))
            finish()
        }
    }

    private fun handleImage(bitmap: Bitmap) {
        if (bitmap.width == 0 || bitmap.height == 0) {
            tvAdvice.text = "Invalid image captured."
            return
        }

        imagePreview.setImageBitmap(bitmap)
        imagePreview.alpha = 1.0f
        imagePreview.scaleType = ImageView.ScaleType.CENTER_CROP

        tvImageHint.visibility = View.GONE
        cardPrediction.visibility = View.VISIBLE
        cardNutrition.visibility = View.VISIBLE
        cardAdvice.visibility = View.VISIBLE

        tvFoodName.text = "Analysing..."
        tvConfidence.text = "Please wait"
        progressConfidence.progress = 0

        tvCalories.text = "Calories: -"
        tvProtein.text = "Protein: -"
        tvCarbs.text = "Carbs: -"
        tvFat.text = "Fat: -"
        tvSalt.text = "Salt: -"
        tvAdvice.text = "Generating personalised advice..."

        val (label, confidence) = classifier.classify(bitmap)
        val nutrition = NutritionData.getNutrition(label)
        val advice = HealthAdvisor.getAdvice(userCondition, nutrition)

        val confidencePercent = confidence * 100f
        val progress = confidencePercent.toInt()

        val confidenceLabel = when {
            progress > 80 -> "High confidence"
            progress > 50 -> "Moderate confidence"
            else -> "Low confidence"
        }

        tvFoodName.text = "Food: ${formatFoodName(label)}"
        tvConfidence.text = "Confidence: ${"%.1f".format(confidencePercent)}% ($confidenceLabel)"
        progressConfidence.progress = progress

        when {
            progress > 80 -> {
                progressConfidence.progressTintList =
                    ColorStateList.valueOf(Color.parseColor("#2E7D32"))
            }
            progress > 50 -> {
                progressConfidence.progressTintList =
                    ColorStateList.valueOf(Color.parseColor("#F9A825"))
            }
            else -> {
                progressConfidence.progressTintList =
                    ColorStateList.valueOf(Color.parseColor("#C62828"))
            }
        }

        tvCalories.text = "Calories: ${nutrition?.calories?.toInt() ?: "-"} kcal"
        tvProtein.text = "Protein: ${nutrition?.protein?.toInt() ?: "-"} g"
        tvCarbs.text = "Carbs: ${nutrition?.carbs?.toInt() ?: "-"} g"
        tvFat.text = "Fat: ${nutrition?.fat?.toInt() ?: "-"} g"
        tvSalt.text = "Salt: ${nutrition?.salt ?: "-"} g"

        tvAdvice.text = if (confidence < 0.6f) {
            "The model is unsure about this image. Try a clearer image for a more reliable result."
        } else {
            "$advice\n\nTip: Results are estimates based on AI analysis."
        }
    }

    private fun showErrorState(e: Exception) {
        cardPrediction.visibility = View.VISIBLE
        cardNutrition.visibility = View.VISIBLE
        cardAdvice.visibility = View.VISIBLE
        tvImageHint.visibility = View.GONE

        tvFoodName.text = "Food: Error"
        tvConfidence.text = "Confidence: -"
        progressConfidence.progress = 0
        progressConfidence.progressTintList =
            ColorStateList.valueOf(Color.parseColor("#C62828"))

        tvCalories.text = "Calories: -"
        tvProtein.text = "Protein: -"
        tvCarbs.text = "Carbs: -"
        tvFat.text = "Fat: -"
        tvSalt.text = "Salt: -"

        tvAdvice.text = "Error: ${e.message}"
        e.printStackTrace()
    }

    private fun formatFoodName(rawName: String): String {
        return rawName
            .replace("_", " ")
            .split(" ")
            .joinToString(" ") { word ->
                word.replaceFirstChar { char -> char.uppercase() }
            }
    }
}