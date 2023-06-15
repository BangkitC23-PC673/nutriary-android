package com.syukron.nutriary.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlin.random.Random

enum class ListType { BREAKFAST, LUNCH, DINNER, SNACKS, HISTORY }

typealias Nutrient = Pair<String, Double>

data class FoodList(val data: List<Food>)

@Entity(tableName = "saved_foods_table")
data class Food(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = Random.nextInt(),

    @ColumnInfo(name = "list_type")
    var listType: Int = ListType.BREAKFAST.ordinal,

    @ColumnInfo(name = "kode")
    @Json(name = "kode")
    val kode: String = "",

    @ColumnInfo(name = "nama_bahan_makanan")
    @Json(name = "nama_bahan_makanan")
    val name: String = "",

    /* Food Nutrients */
    @ColumnInfo(name = "energi_kal")
    @Json(name = "energi_kal")
    var calories: String = "0.0",

//    @ColumnInfo(name = "calories")
//    var calories: String = "0.0",

    @ColumnInfo(name = "protein_g")
    @Json(name = "protein_g")
    var protein: String = "0.0",

    @ColumnInfo(name = "lemak_g")
    @Json(name = "lemak_g")
    var lemak: String = "0.0",

    @ColumnInfo(name = "karbohidrat_g")
    @Json(name = "karbohidrat_g")
    var karbohidrat: String = "0.0",

    @ColumnInfo(name = "serat_g")
    @Json(name = "serat_g")
    var serat: String = "0.0",

    @ColumnInfo(name = "kalsium_mg")
    @Json(name = "kalsium_mg")
    var kalsium: String = "0.0",

    @ColumnInfo(name = "besi_mg")
    @Json(name = "besi_mg")
    var besi: String = "0.0",

    @ColumnInfo(name = "natrium_mg")
    @Json(name = "natrium_mg")
    var natrium: String = "0.0",

    @ColumnInfo(name = "serving_size")
    @Json(name = "serving_size_g")
    var servingSize: String = "0.0"

) {
    @Override
    operator fun plus(food: Food) =
        Food(
            calories = (this.calories.toDouble() + food.calories.toDouble()).toString(),
            protein = (this.protein.toDouble() + food.protein.toDouble()).toString(),
            lemak = (this.lemak.toDouble() + food.lemak.toDouble()).toString(),
            karbohidrat = (this.karbohidrat.toDouble() + food.karbohidrat.toDouble()).toString(),
            serat = (this.serat.toDouble() + food.serat.toDouble()).toString(),
            kalsium = (this.kalsium.toDouble() + food.kalsium.toDouble()).toString(),
            besi = (this.besi.toDouble() + food.besi.toDouble()).toString(),
            natrium = (this.natrium.toDouble() + food.natrium.toDouble()).toString()

        )

    fun getNutrients() = listOf(
        calories.toDouble(),
        protein.toDouble(),
        lemak.toDouble(),
        karbohidrat.toDouble(),
        serat.toDouble(),
        kalsium.toDouble(),
        besi.toDouble(),
        natrium.toDouble()

    )

    fun edit(newServingSize: Double, newListType: ListType? = null):
            Food {
        listType = newListType?.ordinal ?: listType
        val ratio = newServingSize / servingSize.toDouble()
        servingSize = (servingSize.toDouble() * ratio).toString()
        calories = (calories.toDouble() * ratio).toString()
        protein = (protein.toDouble() * ratio).toString()
        lemak = (lemak.toDouble() * ratio).toString()
        karbohidrat = (karbohidrat.toDouble() * ratio).toString()
        serat = (serat.toDouble() * ratio).toString()
        kalsium = (kalsium.toDouble() * ratio).toString()
        besi = (besi.toDouble() * ratio).toString()
        natrium = (natrium.toDouble() * ratio).toString()
        return this
    }
}