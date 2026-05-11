package com.example.foodclassifierapp

object HealthAdvisor {

    fun getAdvice(condition: String, nutrition: Nutrition?): String {
        if (nutrition == null) {
            return "Nutritional data is unavailable for this item. Try another image for a more reliable result."
        }

        return when (condition.lowercase()) {

            "obesity" -> {
                when {
                    nutrition.calories > 600 -> {
                        "This meal is high in calories and fat. Consider a smaller portion or a lower-calorie alternative to better support weight management."
                    }
                    nutrition.calories > 400 -> {
                        "This meal has a moderate calorie content. It can be included occasionally, but portion control is recommended."
                    }
                    nutrition.fat > 20f -> {
                        "Although the calorie level is not very high, the fat content is moderate. It is best consumed in controlled portions."
                    }
                    else -> {
                        "This is a lower-calorie option, making it more suitable for weight management."
                    }
                }
            }

            "diabetes" -> {
                when {
                    nutrition.carbs > 60f -> {
                        "This meal is high in carbohydrates and may cause a rise in blood sugar levels. Portion control is strongly recommended."
                    }
                    nutrition.carbs > 30f -> {
                        "This meal has a moderate carbohydrate content. Pairing it with protein or fibre may help support more stable blood sugar levels."
                    }
                    nutrition.calories > 600 -> {
                        "This meal is energy-dense, so keeping the portion size moderate would be advisable."
                    }
                    else -> {
                        "This appears to be a more suitable option for supporting blood sugar control."
                    }
                }
            }

            "hypertension" -> {
                when {
                    nutrition.salt > 2.0f -> {
                        "This meal has a high salt content, which may not be suitable for blood pressure management. A lower-sodium option would be preferable."
                    }
                    nutrition.salt > 1.0f -> {
                        "This meal contains a moderate amount of salt. Try balancing it with lower-sodium foods across the rest of the day."
                    }
                    nutrition.fat > 25f -> {
                        "Salt levels are reasonable, but the fat content is relatively high. Moderation is still recommended."
                    }
                    else -> {
                        "This is a lower-salt option, making it more suitable for supporting blood pressure control."
                    }
                }
            }

            else -> {
                "Maintain a balanced diet with appropriate portion sizes and variety across meals."
            }
        }
    }
}