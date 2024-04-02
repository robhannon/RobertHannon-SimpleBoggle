package com.example.boggle
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlin.random.Random

class GameFragment : Fragment() {
    private lateinit var gridLayout: GridLayout
    private lateinit var wordTextView: TextView
    private lateinit var submitButton: Button
    private lateinit var clearButton: Button
    private var letterButtons = mutableListOf<LetterButton>()
    private var lastSelectedPosition: Pair<Int, Int>? = null
    private var currentWordPositions = mutableListOf<Pair<Int, Int>>()

    data class LetterButton(val button: Button, var isSelected: Boolean, val row: Int, val col: Int)

    var listener: GameFragmentListener? = null

    interface GameFragmentListener {
        fun onWordSubmitted(word: String)
        fun onResetBoardRequested()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game, container, false)
        gridLayout = view.findViewById(R.id.gridLayoutLetters)
        wordTextView = view.findViewById(R.id.textViewWord)
        submitButton = view.findViewById(R.id.buttonSubmit)
        clearButton = view.findViewById(R.id.buttonClear)

        generateBoard()

        submitButton.setOnClickListener {
            listener?.onWordSubmitted(wordTextView.text.toString())
            clearCurrentWord(keepDisabled = true)
        }

        clearButton.setOnClickListener {
            clearCurrentWord(keepDisabled = false)
        }

        return view
    }

    private fun generateBoard() {
        val gridSize = 4
        gridLayout.columnCount = gridSize
        gridLayout.rowCount = gridSize
        val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val letter = letters.random().toString()
                val button = Button(context).apply {
                    text = letter
                    layoutParams = GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(col))
                }
                val letterButton = LetterButton(button, isSelected = false, row = row, col = col)
                button.setOnClickListener {
                    handleButtonClick(letterButton)
                }
                gridLayout.addView(button)
                letterButtons.add(letterButton)
            }
        }
    }


    private fun handleButtonClick(letterButton: LetterButton) {
        val (button, isSelected, row, col) = letterButton
        if (!isSelected && isAdjacent(row, col)) {
            wordTextView.append(button.text)
            letterButton.isSelected = true
            lastSelectedPosition = Pair(row, col)
            button.isEnabled = false
            currentWordPositions.add(Pair(row, col))
        }
    }

    private fun isAdjacent(row: Int, col: Int): Boolean {
        lastSelectedPosition?.let { (lastRow, lastCol) ->
            return (row in (lastRow - 1)..(lastRow + 1)) && (col in (lastCol - 1)..(lastCol + 1))
        }
        return true
    }

    fun clearCurrentWord(keepDisabled: Boolean) {
        wordTextView.text = ""
        if (!keepDisabled) {
            currentWordPositions.forEach { (row, col) ->
                val index = row * 4 + col
                (gridLayout.getChildAt(index) as? Button)?.isEnabled = true
            }
            currentWordPositions.clear()
        }
        lastSelectedPosition = null
    }

    fun resetBoardAndWord() {
        wordTextView.text = ""
        lastSelectedPosition = null
        letterButtons.forEach { it.button.isEnabled = true; it.isSelected = false }
        gridLayout.removeAllViews()
        letterButtons.clear()
        generateBoard()
    }

    companion object {
        fun newInstance() = GameFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is GameFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement GameFragmentListener")
        }
    }
}
