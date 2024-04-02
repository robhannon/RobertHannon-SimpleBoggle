package com.example.boggle

import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Website.URL
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class WordValidator {
    private var wordSet = mutableSetOf<String>()

    fun downloadWordList(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://raw.githubusercontent.com/dwyl/english-words/master/words.txt")
                val connection = url.openConnection()
                wordSet = connection.getInputStream().bufferedReader().use { it.readLines().toMutableSet() }
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Word list downloaded successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to download word list", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun isWordValid(word: String): Boolean {
        return wordSet.contains(word.toLowerCase())
    }
}