package com.example.boggle

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class ScoreFragment : Fragment() {
    private lateinit var textViewScore: TextView
    private lateinit var newGameButton: Button
    var listener: ScoreFragmentListener? = null

    interface ScoreFragmentListener {
        fun onNewGameRequested()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_score, container, false)
        textViewScore = view.findViewById(R.id.textViewScore)
        newGameButton = view.findViewById(R.id.buttonNewGame)

        newGameButton.setOnClickListener {
            listener?.onNewGameRequested()
        }

        return view
    }


    fun updateScore(score: Int) {
        println(score)
        view?.findViewById<TextView>(R.id.textViewScore)?.text = "Score: $score"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ScoreFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement ScoreFragmentListener")
        }
    }

    companion object {
        fun newInstance() = ScoreFragment()
    }
}
