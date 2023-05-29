package com.syukron.nutriary.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.syukron.nutriary.R
import com.syukron.nutriary.databinding.CardCalorieCounterBinding

class Counter(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {

    private val binding: CardCalorieCounterBinding = CardCalorieCounterBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    fun setCalories(calories: Double) {
        binding.caloriesValue.text = formatDouble(calories)
    }

    fun setCaloriesGoal(calories: Int) {
        binding.caloriesGoal.text = calories.toString()
    }

    fun setCaloriesRemaining(calories: Double) {
        binding.caloriesRemaining.text = context.getString(R.string.remaining_value, calories)
        if (calories >= 0) {
            binding.caloriesRemaining.setTextColor(
                ContextCompat.getColor(context, R.color.safe_color)
            )
        } else {
            binding.caloriesRemaining.setTextColor(
                ContextCompat.getColor(context, R.color.danger_color)
            )
        }
    }

    private fun formatDouble(double: Double) = "%.0f".format(double)
}