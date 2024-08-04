package com.soumyaranjan.frenchdesertcollector

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.soumyaranjan.frenchdesertcollector.databinding.RankingItemBinding

class RankingAdapter(): RecyclerView.Adapter<RankingAdapter.RankingViewHolder>(){
    val rankList = Game.Ranking
    class RankingViewHolder(val binding: RankingItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(score: Score){
            binding.rank.text = score.rank.toString()
            binding.name.text = score.name
            val timeLeft = score.time
            val min = timeLeft / 60
            val sec = timeLeft % 60
            binding.time.text = String.format("%02d:%02d", min, sec)
            binding.score.text = score.score.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val binding = RankingItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RankingViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return rankList.size
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        holder.bind(rankList[position])
    }
}