package com.example.foodclassifierapp

data class Nutrition(
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val salt: Float
)

object NutritionData {

    private val data = mapOf(
        "fish_supper" to Nutrition(800, 25f, 90f, 35f, 2.5f),
        "haggis" to Nutrition(350, 20f, 15f, 25f, 1.8f),
        "porridge" to Nutrition(150, 5f, 27f, 3f, 0.1f),
        "scotch_pie" to Nutrition(400, 12f, 30f, 25f, 1.6f),
        "scottish_breakfast" to Nutrition(900, 30f, 50f, 60f, 3.0f)
    )

    fun getNutrition(label: String): Nutrition? {
        return data[label]
    }
}