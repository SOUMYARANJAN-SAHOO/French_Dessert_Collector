package com.soumyaranjan.frenchdesertcollector

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.soumyaranjan.frenchdesertcollector.databinding.ActivityGameBinding
import java.util.Random

class GameActivity : AppCompatActivity() {

    lateinit var binding: ActivityGameBinding

    var gyroSensitivity: Long = 20

    var score = 0
    var hp = 3

    var name = ""

    var gameRunning = true
    var timeLeft = 30
    var playTime = 0

    val desserts: MutableList<ImageView> = mutableListOf()
    val handler = Handler(Looper.getMainLooper())
    val updateInterval: Long = 50
    var dessertDropSpeed = 10f
    var spawnInterval = 1000L

    var nextlevel = 0
    var nextHp = 0

    var baguette = 0
    var macaron = 0
    var puff = 0
    var fishbone = 0
    var bone = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView<ActivityGameBinding>(this, R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        name = intent.getStringExtra("Name").toString()

        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        sensorManager.registerListener(
            object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
                        if (gameRunning) {
                            val xVal = binding.basket.x + (event.values[1] * gyroSensitivity)
                            binding.basket.x = xVal.coerceIn(
                                0f, binding.main.width - binding.basket.width.toFloat()
                            )
                        }

                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

                }
            }, sensor, SensorManager.SENSOR_DELAY_GAME
        )

        binding.pauseBtn.setOnClickListener {
                gameRunning = false
                pauseGame(true)
        }
        binding.resumeBtn.setOnClickListener {
            gameRunning = true
            pauseGame(false)
        }

        binding.restartBtn.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setMessage("Whether to abandon the current game progress and restart the game?")
                setPositiveButton("Yes") { _, _ ->
                    gameRunning = true
                    pauseGame(false)
                    restartGame()
                }
                setNegativeButton("No") { _, _ ->
                }
                show()
            }
        }

        binding.quitBtn.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setMessage("Whether to abandon the current game progress and quit the game?")
                setPositiveButton("Yes") { _, _ ->
                    finishAffinity()
                }
                setNegativeButton("No") { _, _ ->
                }
                show()
            }
        }

        binding.basket.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                if (gameRunning) {
                    binding.apply {
                        basket.x = event.rawX - basket.width / 2
                    }
                }

            }
            true
        }


        binding.scoreBoard.text = score.toString()
        binding.hpBoard.text = hp.toString()

        handler.post(updateGame)
        handler.post(updateTime)
        handler.post(spawner)
        handler.post(playTimeRegister)
    }

    private fun restartGame() {
        score = 0
        timeLeft = 30
        hp = 3
        dessertDropSpeed = 10f
        binding.scoreBoard.text = score.toString()
        binding.hpBoard.text = hp.toString()
        binding.timeBoard.text = formatTime(timeLeft)

        desserts.forEach {
            binding.main.removeView(it)
        }
        desserts.clear()
    }

    private fun pauseGame(pause: Boolean) {
        if (pause) {
            binding.pauseInteface.visibility = View.VISIBLE
            binding.textView3.visibility = View.VISIBLE
            binding.resumeBtn.visibility = View.VISIBLE
            binding.restartBtn.visibility = View.VISIBLE
            binding.quitBtn.visibility = View.VISIBLE
        } else {
            binding.pauseInteface.visibility = View.GONE
            binding.textView3.visibility = View.GONE
            binding.resumeBtn.visibility = View.GONE
            binding.restartBtn.visibility = View.GONE
            binding.quitBtn.visibility = View.GONE
        }

    }

    val playTimeRegister = object : Runnable {
        override fun run() {
            if(gameRunning) playTime++
            handler.postDelayed(this, 1000)
        }
    }

    val updateTime = object : Runnable {
        override fun run() {
            if (gameRunning) {
                timeLeft--
                if (timeLeft == 0 && gameRunning) {
                    gameRunning = false
                    gameOver()
                }
                binding.timeBoard.text = formatTime(timeLeft)
            }
            handler.postDelayed(this, 1000)
        }
    }

    private fun formatTime(timeLeft: Int): CharSequence {
        val min = timeLeft / 60
        val sec = timeLeft % 60
        return String.format("%02d:%02d", min, sec)
    }

    val spawner = object : Runnable {
        override fun run() {
            if (gameRunning) {
                addDessert()
            }
            handler.postDelayed(this, spawnInterval)
        }
    }

    val updateGame = object : Runnable {
        override fun run() {
            if (gameRunning) {
                updateLevel()
                updateDesserts()
                checkCollisions()
            }
            handler.postDelayed(this, updateInterval)
        }
    }

    private fun updateLevel() {
        if (gameRunning) {
            if (nextlevel >= 100) {
                dessertDropSpeed += 10
                spawnInterval -= 100
                nextlevel -= 100
            }
            if (nextHp >= 50) {
                hp++
                binding.hpBoard.text = hp.toString()
                nextHp -= 50
            }
        }
    }

    fun addDessert() {
        val dessert = Game.dessertsList.get(Random().nextInt(5))
        val dessertItem = ImageView(this).apply {
            setImageResource(dessert.image)
            layoutParams = ViewGroup.LayoutParams(100, 100)
            x = Random().nextInt(binding.main.width - binding.basket.width).toFloat()
            y = 400f
            tag = dessert
        }
        binding.main.addView(dessertItem)
        desserts.add(dessertItem)
    }

    fun updateDesserts() {
        desserts.forEach { dessert ->
            dessert.y += dessertDropSpeed
        }
        desserts.filter { it.y > binding.main.height }.forEach {
            binding.main.removeView(it)
            desserts.remove(it)
            val dessert = it.tag as Dessert
            if (dessert.points > 0) hp--
            if (hp <= 0 && gameRunning) {
                gameRunning = false
                gameOver()
            }
            binding.hpBoard.text = hp.toString()
        }
    }

    fun gameOver() {
        val currScore = Score(1, name, playTime, score)
        Game.addScore(this,currScore)
        startActivity(Intent(this, GameOverActivity::class.java).apply {
            putExtra("Score", score)
            putExtra("Time", playTime)
            putExtra("Baguette",baguette)
            putExtra("Macaron",macaron)
            putExtra("Puff",puff)
            putExtra("Fishbone",fishbone)
            putExtra("Bone",bone)
        })
        finish()
    }

    fun checkCollisions() {
        binding.apply {
            val basketBounds = basket.x..(basket.x + basket.width)
            desserts.filter { it.y + it.height >= basket.y }.forEach { dessert ->
                if (dessert.x in basketBounds) {
                    val dessertItem = dessert.tag as Dessert
                    score += dessertItem.points
                    nextlevel += dessertItem.points
                    nextHp += dessertItem.points
                    hp += dessertItem.hp
                    if (hp <= 0 && gameRunning) {
                        gameRunning = false
                        gameOver()
                    }
                    timeLeft += dessertItem.time
                    hpBoard.text = hp.toString()
                    scoreBoard.text = score.toString()
                    main.removeView(dessert)
                    desserts.remove(dessert)

                    when(dessertItem.name){
                        "Baguette" -> baguette++
                        "Macaron" -> macaron++
                        "Puff" -> puff++
                        "Fishbone" -> fishbone++
                        "Bone" -> bone++
                    }


                }
            }
        }

    }
}
