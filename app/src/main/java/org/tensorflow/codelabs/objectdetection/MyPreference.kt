package org.tensorflow.codelabs.objectdetection

import android.content.Context
import android.content.SharedPreferences
import java.util.*

const val PREFERENCE_NAME = "SharedPreferenceExample"
const val PREFERENCE_LANGUAGE = "Language"


class MyPreference(context: Context) {
    private val sysLang: String = Locale.getDefault().language
    private val preference: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getLoginCount(): String {
        return preference.getString(PREFERENCE_LANGUAGE, sysLang)!!
    }

    fun setLoginCount(Language: String) {
        val editor = preference.edit()
        editor.putString(PREFERENCE_LANGUAGE, Language)
        editor.apply()
    }

}