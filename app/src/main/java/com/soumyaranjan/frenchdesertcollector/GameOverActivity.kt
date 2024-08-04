package com.soumyaranjan.frenchdesertcollector

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.soumyaranjan.frenchdesertcollector.databinding.ActivityGameOverBinding

class GameOverActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding= DataBindingUtil.setContentView<ActivityGameOverBinding>(this,R.layout.activity_game_over)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val score = intent.getIntExtra("Score",0)
        val time = intent.getIntExtra("Time",0)
        val baguette = intent.getIntExtra("Baguette",0)
        val macaron = intent.getIntExtra("Macaron",0)
        val puff = intent.getIntExtra("Puff",0)
        val fishbone = intent.getIntExtra("Fishbone",0)
        val bone = intent.getIntExtra("Bone",0)

        binding.apply {
            totalScore.text = score.toString()
            totalTime.text = formatTime(time)
            baguetteCount.text = "x $baguette"
            baguetteScore.text = "+ ${baguette*10}"
            macaronCount.text = "x $macaron"
            macaronScore.text = "+ ${macaron*20}"
            puffCount.text = "x $puff"
            puffScore.text = "+ ${puff*30}"
            fishBoneCount.text = "x $fishbone"
            fishBoneScore.text = "- ${fishbone*(20)}"
            boneCount.text = "x $bone"
            boneScore.text = "- ${bone*(10)}"

            nextBtn.setOnClickListener {
                startActivity(Intent(this@GameOverActivity,RankingActivity::class.java))
                finish()
            }
        }


    }

    private fun formatTime(timeLeft: Int): CharSequence {
        val min = timeLeft / 60
        val sec = timeLeft % 60
        return String.format("%02d:%02d", min, sec)
    }
}