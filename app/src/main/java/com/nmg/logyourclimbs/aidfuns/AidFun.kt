package com.nmg.logyourclimbs.aidfuns

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import java.text.Normalizer

class AidFun(private val context: Context) {

    // Elimino los signos diacríticos de una palabra.
    fun removeSigns(word: String): String {
        val regex = "\\p{M}".toRegex()
        val temp = Normalizer.normalize(word, Normalizer.Form.NFD)
        return regex.replace(temp, "")
    }

    // Muestro un mensaje en un toast.
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    // Formateo el tiempo en milisegundos a un formato MM:SS.
    fun formatTime(millis: Long): String {
        val minutes = millis / 1000 / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
