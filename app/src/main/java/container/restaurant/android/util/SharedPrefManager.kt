package container.restaurant.android.util

import android.content.Context
import android.content.SharedPreferences
import java.lang.StringBuilder
import java.util.*

object SharedPrefManager {
    /** preference 를 가져올 때 사용할 이름 **/
    private const val PREFERENCE_NAME = "container-android"

    /** 키 값에 해당하는 value값이 없을 때 반환할 기본 값 **/
    const val DEFAULT_VALUE_STRING=""
    const val DEFAULT_VALUE_BOOLEAN= false
    const val DEFAULT_VALUE_INT=-1
    const val DEFAULT_VALUE_LONG=-1L
    const val DEFAULT_VALUE_FLOAT=-1F

    /** preference 객체 반환 **/
    private fun getPreferences(context: Context): SharedPreferences = context.getSharedPreferences(
        PREFERENCE_NAME, Context.MODE_PRIVATE)

    /** String 값 게터, 세터 **/
    fun getString(context: Context, key: String): String {
        val prefs = getPreferences(context)
        return prefs.getString(key, DEFAULT_VALUE_STRING) ?: DEFAULT_VALUE_STRING
    }
    fun setString(context: Context, key: String, value: String) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }
    fun removeString(context: Context, key: String) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putString(key, DEFAULT_VALUE_STRING)
        editor.apply()
    }

    /** Boolean 값 게터, 세터 **/
    fun getBoolean(context: Context, key: String): Boolean{
        val prefs = getPreferences(context)
        return prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN)
    }
    fun setBoolean(context: Context, key: String, value: Boolean) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }
    fun removeBoolean(context: Context, key: String) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putBoolean(key, DEFAULT_VALUE_BOOLEAN)
        editor.apply()
    }

    /** Int 값 게터, 세터 **/
    fun getInt(context: Context, key: String): Int {
        val prefs = getPreferences(context)
        return prefs.getInt(key, DEFAULT_VALUE_INT)
    }
    fun setInt(context: Context, key: String, value:Int) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putInt(key, value)
        editor.apply()
    }
    fun removeInt(context: Context, key: String) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putInt(key, DEFAULT_VALUE_INT)
        editor.apply()
    }

    /** Long 값 게터, 세터 **/
    fun getLong(context: Context, key: String): Long {
        val prefs = getPreferences(context)
        return prefs.getLong(key, DEFAULT_VALUE_LONG)
    }
    fun setLong(context: Context, key: String, value:Long) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putLong(key, value)
        editor.apply()
    }
    fun removeLong(context: Context, key: String){
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putLong(key, DEFAULT_VALUE_LONG)
        editor.apply()
    }

    /** Float 값 게터, 세터 **/
    fun getFloat(context: Context, key: String): Float {
        val prefs = getPreferences(context)
        return prefs.getFloat(key, DEFAULT_VALUE_FLOAT)
    }
    fun setFloat(context: Context, key: String, value:Float) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putFloat(key, value)
        editor.apply()
    }
    fun removeFloat(context: Context, key: String){
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putFloat(key, DEFAULT_VALUE_FLOAT)
        editor.apply()
    }

    /** String 값들을 보관하는 Queue 값 게터, 세터 **/
    fun getStringDeque(context: Context, key: String): Deque<String> {
        val prefs = getPreferences(context)
        val queueStr = prefs.getString(key, DEFAULT_VALUE_STRING) ?: DEFAULT_VALUE_STRING
        val arrayListDeque = LinkedList<String>()
        if(queueStr != DEFAULT_VALUE_STRING) {
            for(itemStr in queueStr.split(" ")) {
                arrayListDeque.add(itemStr)
            }
        }
        return arrayListDeque
    }
    fun setStringDeque(context: Context, key: String, value: Deque<String>) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        val strBuilder = StringBuilder()
        for(item in value) {
            if(item == value.last()) {
                strBuilder.append(item.toString())
            }
            else {
                strBuilder.append("$item ")
            }
        }
        editor.putString(key, strBuilder.toString())
        editor.apply()
    }
    fun removeStringDeque(context: Context, key: String) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putString(key, DEFAULT_VALUE_STRING)
        editor.apply()
    }
}