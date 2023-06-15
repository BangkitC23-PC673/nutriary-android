package com.syukron.nutriary.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import com.syukron.nutriary.R
import com.syukron.nutriary.data.RemoteDataSource
import com.syukron.nutriary.data.database.FoodDatabase
import com.syukron.nutriary.data.model.Food
import com.syukron.nutriary.data.model.ListType
import com.syukron.nutriary.util.ConnectionChecker
import com.syukron.nutriary.util.FoodNotFoundException
import com.syukron.nutriary.util.NoConnectionException
import java.util.*

class FoodRepository(val context: Context) {
    private val sharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(context)

    private val datePreferencesKey = context.getString(R.string.date_preferences_key)
    private val goalPreferencesKey = context.getString(R.string.goal_preferences_key)

    private val localDataSource = FoodDatabase.getInstance(context).foodDao
    private val remoteDataSource = RemoteDataSource.httpClient

    private val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

    suspend fun searchFoodsThatMatchQuery(query: String): List<Food> {
        if (!ConnectionChecker.isOnline()) throw NoConnectionException()
        val list = remoteDataSource.getFoodList( query).data
        if (list.isEmpty()) throw FoodNotFoundException()
        return list
    }

    suspend fun addFoodsToDatabase(foods: List<Food>) {
        localDataSource.insertFoods(foods)
    }

    suspend fun addFoodToDatabase(food: Food) {
        localDataSource.insertFood(food)
    }

    suspend fun deleteFoodFromTheDatabase(food: Food) {
        localDataSource.deleteFood(food)
    }

    suspend fun editFoodFromTheDatabase(food: Food): Food {
        localDataSource.updateFood(food)
        return food
    }

    suspend fun clearNonHistoryFoods() {
        localDataSource.clearAllExceptHistoryFoods()
    }

    suspend fun clearOnlyHistoryFoods() {
        localDataSource.clearOnlyHistoryFoods()
    }

    suspend fun getAllExceptHistoryFoods(): List<Food> {
        return localDataSource.getAllExceptHistoryFoods()
    }

    fun getAllFoodsWithListType(listType: ListType): LiveData<List<Food>> {
        return localDataSource.getAllFoodsWithListType(listType.ordinal)
    }

    fun getSavedGoalFromPreferences(valueGoal: Int): Int {
        val savedGoal = sharedPreferences.getString(
            goalPreferencesKey,
            valueGoal.toString()
        )
        return savedGoal?.toInt() ?: valueGoal
    }

    fun setSavedGoalFromPreferences(newSavedGoal: Int) {
        sharedPreferences.edit()
            .putString(goalPreferencesKey, newSavedGoal.toString())
            .apply()
    }

    fun saveTodayDateInPreferences() {
        sharedPreferences.edit().putInt(datePreferencesKey, today).apply()
    }

    fun isSavedDateToday(): Boolean {
        val savedDay = sharedPreferences.getInt(datePreferencesKey, 0)
        return today == savedDay
    }

    fun getKcalSum(): LiveData<Double> {
        return localDataSource.getKcalSum()
    }
}