package com.soumyaranjan.frenchdesertcollector

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.soumyaranjan.frenchdesertcollector.Game.Ranking

object Game {

    var dessertsList = listOf(
        Dessert("Baguette", R.drawable.baguette, 10, 0,0),
        Dessert("Macaron", R.drawable.macaron,20,0,0),
        Dessert("Puff",R.drawable.puff,30,0,30),
        Dessert("Fishbone",R.drawable.fishbone,-20,-1,0),
        Dessert("Bone",R.drawable.bone,-10,0,0)
    )

    var Ranking = mutableListOf<Score>()

    fun addScore(context: Context, score: Score){
        Ranking = sharedPref(context).loadRanking()
        Ranking.add(score)
        Ranking.sortBy { it.score }
        Ranking.reverse()
        for(i in 0..Ranking.size-1){
            Ranking[i].rank = i+1
        }
        sharedPref(context).saveRanking(Ranking)
    }



}

data class Dessert(
    val name: String,
    val image: Int,
    val points: Int,
    val hp: Int,
    val time:Int
)

data class Score(
    var rank : Int,
    val name: String,
    val time:Int,
    val score:Int
)

class sharedPref(context: Context){
    private val sharedPref = context.getSharedPreferences("my_ranking",Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveRanking(ranking: List<Score>){
        sharedPref.edit()
            .putString("ranking",gson.toJson(Ranking))
            .apply()
    }

    fun loadRanking(): MutableList<Score>{
        val json = sharedPref.getString("ranking",null)
        return if(json != null){
            gson.fromJson(json,Array<Score>::class.java).toMutableList()
        }else{
            mutableListOf()
            }
    }
}