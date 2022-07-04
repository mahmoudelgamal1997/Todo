package dev.elgaml.noteit.helper

import android.content.Context
import android.content.SharedPreferences

class SharedHelper {

    companion object {

    const val PROMPT="PROMOT"
    var sharedPreferences: SharedPreferences? = null

    private fun getSharedPref(context: Context): SharedPreferences? {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("app_data", Context.MODE_PRIVATE)
        }
        return sharedPreferences
    }

    private fun getSharedPrefEditor(context: Context): SharedPreferences.Editor? {
        return getSharedPref(context)?.edit()
    }

    fun saveBoolean(context: Context, key:String,boolean: Boolean){
        getSharedPrefEditor(context)?.putBoolean(key,boolean)?.apply()
    }

    fun getBoolean(context: Context,key:String):Boolean?{
        return getSharedPref(context)?.getBoolean(key,false)
    }    }
}