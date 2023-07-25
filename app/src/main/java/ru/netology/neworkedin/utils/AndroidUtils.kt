package ru.netology.neworkedin.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/** КЛАСС ОТВЕЧАЮЩИЙ ЗА УТИЛИТЫ  АНДРОИНД */

object AndroidUtils {

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}