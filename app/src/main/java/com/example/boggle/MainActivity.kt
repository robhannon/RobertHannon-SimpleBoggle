package com.example.boggle

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity(), GameFragment.GameFragmentListener, ScoreFragment.ScoreFragmentListener {
    private var score = 0
    private lateinit var wordValidator: WordValidator
    private var shakeDetector: ShakeDetector? = null
    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        wordValidator = WordValidator()
        wordValidator.downloadWordList(this)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        shakeDetector = ShakeDetector {
            resetGame()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.gameFragmentContainer, GameFragment.newInstance(), "GAME_FRAGMENT")
                replace(R.id.scoreFragmentContainer, ScoreFragment.newInstance(), "SCORE_FRAGMENT")
            }
        }
    }



    override fun onWordSubmitted(word: String) {
        val (scoreChange, message) = calculateScore(word)
        if (scoreChange == -10) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
        score += scoreChange
        val scoreFragment = supportFragmentManager.findFragmentById(R.id.scoreFragmentContainer) as? ScoreFragment
        scoreFragment?.updateScore(score)
    }

    override fun onNewGameRequested() {
        resetGame()
    }

    override fun onResetBoardRequested() {
        resetBoardAndWord()
    }

    fun calculateScore(word: String): Pair<Int, String> {
        if (!wordValidator.isWordValid(word)) {
            Toast.makeText(this, "Invalid word", Toast.LENGTH_SHORT).show()
            return Pair(-10, "Word not valid")
        }

        if (word.length < 4) return Pair(-10, "Word too short")

        val vowels = "AEIOU"
        val specialLetters = "SZPXQ"
        var score = 0
        var vowelCount = 0
        var containsSpecialLetter = false

        word.toUpperCase().forEach { letter ->
            when {
                vowels.contains(letter) -> {
                    score += 5
                    vowelCount++
                }
                specialLetters.contains(letter) -> {
                    score += 1
                    containsSpecialLetter = true
                }
                else -> score += 1
            }
        }

        if (vowelCount < 2) return Pair(-10, "Not enough vowels")

        if (containsSpecialLetter) score *= 2

        return Pair(score, "Valid word")
    }

    private fun resetBoardAndWord() {
        val gameFragment = supportFragmentManager.findFragmentById(R.id.gameFragmentContainer) as? GameFragment
        gameFragment?.clearCurrentWord(keepDisabled = true)
    }

    private fun updateScoreFragment() {
        val scoreFragment = supportFragmentManager.findFragmentByTag("SCORE_FRAGMENT") as? ScoreFragment
        scoreFragment?.updateScore(score)
    }


    private fun resetGame() {
        score = 0
        updateScoreFragment()

        val gameFragment = supportFragmentManager.findFragmentByTag("GAME_FRAGMENT") as? GameFragment
        gameFragment?.resetBoardAndWord()
    }

    override fun onResume() {
        super.onResume()
        shakeDetector?.let {
            sensorManager.registerListener(it,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        shakeDetector?.let {
            sensorManager.unregisterListener(it)
        }
    }

}

